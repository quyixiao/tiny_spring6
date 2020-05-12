package com.gupaoedu.framework.context.support;

import com.design.pattern.utils.LogUtils;
import com.gupaoedu.framework.annotation.GPAutowired;
import com.gupaoedu.framework.annotation.GPController;
import com.gupaoedu.framework.annotation.GPService;
import com.gupaoedu.framework.beans.GPBeanWrapper;
import com.gupaoedu.framework.beans.config.GPBeanDefinition;
import com.gupaoedu.framework.beans.config.GPBeanPostProcessor;
import com.gupaoedu.framework.core.GPBeanFactory;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class GPApplicationContext extends GPDefaultListableBeanFactory implements GPBeanFactory {
    private String[] configLocations;
    private GPBeanDefinitionReader reader;
    // 单例Ioc容器
    private Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>();
    // 通用ioc容器
    private Map<String, GPBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();

    public GPApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void refresh() throws Exception {
        // 1.定位，定位配置文件
        reader = new GPBeanDefinitionReader(this.configLocations);
        // 2.加载配置文件，扫描相关的类，把它们封装成BeanDefinition
        List<GPBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        //3 注册配置信息放到容器中（伪IOC容器）
        doRegisterBeanDefinition(beanDefinitions);
        // 4.把不是延时加载的类提前初始化
        doAutowrited();


    }

    private void doAutowrited() {
        for (Map.Entry<String, GPBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegisterBeanDefinition(List<GPBeanDefinition> beanDefinitions) throws Exception {
        for (GPBeanDefinition beanDefinition : beanDefinitions) {
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception(" the " + beanDefinition.getFactoryBeanName() + " is exits ");
            }
            log.info("doRegisterBeanDefinition factoryBeanName:" + beanDefinition.getFactoryBeanName());
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        // 依赖注入，从这里开始，读取 BeanDefinition中的信息
        // 然后通过反射机制创建一个实例并返回
        // 3 Spring 的做法是，不会把原始的对象放出去的，会用一个BeanWrapper 来进行一次包装
        //保留原来的OOP 关系
        // 2.需要对它进行扩展，增强，为以后的AOP 打基础
        GPBeanDefinition beanDefinition = super.beanDefinitionMap.get(beanName);
        // 生成通知事件
        GPBeanPostProcessor beanPostProcessor = new GPBeanPostProcessor();
        Object instance = instantiateBean(beanDefinition);
        if (null == instance) {
            return null;
        }

        // 在实例初始化前调用一次
        beanPostProcessor.postProcessBeforeInitialization(instance, beanName);

        GPBeanWrapper beanWrapper = new GPBeanWrapper(instance);
        LogUtils.info("put beanName beanName: " + beanName + " ,beanWrapper=" + beanWrapper);
        this.factoryBeanInstanceCache.put(beanName, beanWrapper);

        LogUtils.info("getBean beanName : " + beanName + " ,beanWrapper=" + beanWrapper);

        //在实例初始化以后调用一次
        beanPostProcessor.postProcessAfterInitializaction(instance, beanName);

        populateBean(beanName, instance);
        // 通过这样的调用，相当一起给你一我们自己留下可操作的空间
        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }

    private void populateBean(String beanName, Object instance) {
        Class clazz = instance.getClass();
        if (!(clazz.isAnnotationPresent(GPController.class) || clazz.isAnnotationPresent(GPService.class))) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(GPAutowired.class)) {
                continue;
            }
            GPAutowired autowired = field.getAnnotation(GPAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if ("".endsWith(autowiredBeanName)) {
                autowiredBeanName = field.getType().getName();
            }
            field.setAccessible(true);
            try {
                GPBeanWrapper beanWrapper = this.factoryBeanInstanceCache.get(autowiredBeanName);
                if (beanWrapper == null) {
                    log.info("xxxxxxxxx beanName :" + beanName + ",autowiredBeanName :" + autowiredBeanName);
                    Object fieldInstance = getBean(autowiredBeanName);
                    field.set(instance, fieldInstance);
                } else {
                    log.info("populateBean beanName ：" + beanName + " autowiredBeanName:" + autowiredBeanName + "，beanWrapper=" + beanWrapper + ",field =" + field.getName());
                    field.set(instance, beanWrapper.getWrappedInstance());
                }
            } catch (Exception e) {
                log.error("populateBean excption " + beanName, e);
            }
        }
    }


    private Object instantiateBean(GPBeanDefinition beanDefinition) {
        Object instance = null;
        String className = beanDefinition.getBeanClassName();
        try {
            // 因为根据Class才能确定一个类是否是实例
            if (this.factoryBeanObjectCache.containsKey(className)) {
                instance = this.factoryBeanObjectCache.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                this.factoryBeanObjectCache.put(beanDefinition.getFactoryBeanName(), instance);
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }


    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }


    public Properties getConfig() {
        return this.reader.getConfig();
    }
}
