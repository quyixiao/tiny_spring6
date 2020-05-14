package com.gupaoedu.framework.orm;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
public class EntityOperation<T> {


    public Class<T> entityClass = null;
    public Map<String, PropertyMapping> mappings;
    public RowMapper<T> rowMapper;
    public String tableName;
    public String allColum = "*";
    public Field pkField;


    public EntityOperation(Class<T> clazz, String pk) throws Exception {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new Exception(" 在" + clazz.getName() + " 中没有找到 Entity 注解，不能做 orm");
        }
        this.entityClass = clazz;
        Table table = entityClass.getAnnotation(Table.class);
        if (table != null) {
            this.tableName = table.name();
        } else {
            this.tableName = entityClass.getSimpleName();
        }
        Map<String, Method> getters = ClassMappings.findPublicGetters(entityClass);
        Map<String, Method> setters = ClassMappings.findPublicSetters(entityClass);
        Field[] fields = ClassMappings.findFields(entityClass);
        fillPkFieldAndAllColumn(pk, fields);
        this.mappings = getPropertyMappings(getters, setters, fields);
        this.allColum = this.mappings.keySet().toString().replace("[", "")
                .replace("]", "").replaceAll(" ", "");
        this.rowMapper = createRowMapper();
    }


    Map<String, PropertyMapping> getPropertyMappings(Map<String, Method> getters, Map<String, Method> setters, Field[] fields) {
        Map<String, PropertyMapping> mappings = new HashMap<>();
        String name;
        for (Field field : fields) {
            if (field.isAnnotationPresent(Transient.class)) {
                continue;
            }
            name = field.getName();
            if (name.startsWith("is")) {
                name = name.substring(2);
            }
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
            Method setter = setters.get(name);
            Method getter = setters.get(name);
            if (setter == null || getter == null) {
                continue;
            }
            Column column = field.getAnnotation(Column.class);
            if (column == null) {
                mappings.put(field.getName(), new PropertyMapping(getter, setter, field));
            } else {
                mappings.put(column.name(), new PropertyMapping(getter, setter, field));
            }
        }
        return mappings;
    }


    RowMapper<T> createRowMapper() {
        return new RowMapper<T>() {
            @Override
            public T mapRow(ResultSet rs, int rowNum) throws SQLException {
                try {
                    T t = entityClass.newInstance();
                    ResultSetMetaData meta = rs.getMetaData();
                    int colums = meta.getColumnCount();
                    String columnName = "";
                    for (int i = 0; i <= colums; i++) {
                        Object value = rs.getObject(i);
                        columnName = meta.getColumnName(i);
                        fillBeanFieldValue(t, columnName, value);
                    }
                    return t;
                } catch (Exception e) {
                    throw new RuntimeException("");
                }
            }
        };
    }


    protected void fillBeanFieldValue(T t, String columnName, Object value) {
        if (value != null) {
            PropertyMapping pm = mappings.get(columnName);
            if (pm != null) {
                try {
                    pm.set(t, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    protected void fillPkFieldAndAllColumn(String pk, Field[] fields) {
        if (!StringUtil.isNotBlank(pk)) {
            try {
                pkField = entityClass.getDeclaredField(pk);
                pkField.setAccessible(true);

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                if (StringUtil.isBlank(pk)) {
                    Id id = f.getAnnotation(Id.class);
                    if (id != null) {
                        pkField = f;
                        break;
                    }
                }
            }
        }
    }


    public T parse(ResultSet rs) {
        T t = null;
        if (null == rs) {
            return null;
        }
        Object value = null;
        try {
            t = (T) entityClass.newInstance();
            for (String columnName : mappings.keySet()) {
                value = rs.getObject(columnName);
                fillBeanFieldValue(t, columnName, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }


    public Map<String, Object> parse(T t) {
        Map<String, Object> _map = new TreeMap<>();
        for (String columnName : mappings.keySet()) {
            try {
                Object value = mappings.get(columnName).getter.invoke(t);
                if (value == null) {
                    continue;
                }
                _map.put(columnName, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return _map;
    }


    public void print(T t) {
        for (String columnName : mappings.keySet()) {
            try {
                Object value = mappings.get(columnName).getter.invoke(t);
                if (value == null) {
                    continue;
                }
                log.info(columnName + "=" + value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class PropertyMapping {
        private boolean insertable;
        private boolean updatable;
        private String columnName;
        private boolean id;
        private Method getter;
        private Method setter;
        private Class enumClass;
        private String fieldName;


        public PropertyMapping(Method getter, Method setter, Field field) {
            this.getter = getter;
            this.setter = setter;
            this.enumClass = getter.getReturnType().isEnum() ? getter.getReturnType() : null;
            Column column = field.getAnnotation(Column.class);
            this.insertable = column == null || column.insertable();
            this.updatable = column == null || column.updatable();
            this.columnName = column == null ? ClassMappings.getGetterName(getter) :
                    ("".equals(column.name()) ? ClassMappings.getGetterName(getter) : column.name());
            this.id = field.isAnnotationPresent(Id.class);
            this.fieldName = field.getName();
        }

        Object get(Object target) throws Exception {
            Object r = getter.invoke(target);
            return enumClass == null ? r : Enum.valueOf(enumClass, (String) r);
        }

        void set(Object target, Object value) throws Exception {
            if (enumClass != null && value != null) {
                value = Enum.valueOf(enumClass, (String) value);
            }
            if (value != null) {
                setter.invoke(target, setter.getParameterTypes()[0].cast(value));
            }
        }
    }


}
