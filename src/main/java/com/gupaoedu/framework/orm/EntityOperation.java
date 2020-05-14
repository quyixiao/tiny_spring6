package com.gupaoedu.framework.orm;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

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
        if(table !=null){
            this.tableName = table.name();
        }else{
            this.tableName = entityClass.getSimpleName();
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
            this.columnName = column == null ? ClassMappings.getGetterName(getter);
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
