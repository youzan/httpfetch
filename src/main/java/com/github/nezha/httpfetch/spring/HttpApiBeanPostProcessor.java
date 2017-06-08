package com.github.nezha.httpfetch.spring;

import com.github.nezha.httpfetch.HttpApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.lang.reflect.Field;

/**
 * Created by daiqiang on 17/5/25.
 * 自动解析HttpApi的注解
 */
public class HttpApiBeanPostProcessor implements MergedBeanDefinitionPostProcessor, Ordered {

    Logger LOGGER = LoggerFactory.getLogger(HttpApiBeanPostProcessor.class);

    private HttpApiService service;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private boolean needInject(Field field){
        return field.getAnnotation(HttpApiBean.class) != null;
    }

    private void inject(Object bean, Field field) throws IllegalAccessException {
        if(!field.isAccessible()){
            field.setAccessible(true);
        }
        field.set(bean, service.getOrCreateService(field.getType()));
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class clazz = bean.getClass();
        LOGGER.debug("init bean!", "clazz", clazz);
        Field[] fields = clazz.getDeclaredFields();
        if(clazz.getName().indexOf("WechatOauth") > 0){
            System.out.println("==");
        }
        for(Field field : fields){
            if(needInject(field)){
                //需要注入
                try {
                    inject(bean, field);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("HttpApi依赖注入失败!", e);
                }
            }
        }
        return bean;
    }

    public HttpApiService getService() {
        return service;
    }

    public void setService(HttpApiService service) {
        this.service = service;
    }

    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {

    }
}
