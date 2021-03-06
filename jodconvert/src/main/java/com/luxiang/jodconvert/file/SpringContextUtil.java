package com.luxiang.jodconvert.file;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

public class SpringContextUtil implements ApplicationContextAware {

    // Spring应用上下文环境  
    private static ApplicationContext applicationContext;

    /**
     * 实现ApplicationContextAware接口的回调方法，设置上下文环境
     *
     * @param applicationContext
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 获取对象
     * 这里重写了bean方法，起主要作用
     *
     * @param name
     * @return Object 一个以所给名字注册的bean的实例
     * @throws BeansException
     */
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }

    /**
     * 获取一个对象
     *
     * @param name
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T getBean(String name, Class<T> type) {
        Assert.hasText(name, () -> "name is must not null");
        Assert.notNull(type, () -> "type is must not null");
        return applicationContext.getBean(name, type);
    }

    /**
     * 获取一个对象
     *
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> type) {
        Assert.notNull(type, () -> "type is must not null");
        return applicationContext.getBean(type);
    }

}  
