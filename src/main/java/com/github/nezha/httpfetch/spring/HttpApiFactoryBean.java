package com.github.nezha.httpfetch.spring;

import com.github.nezha.httpfetch.HttpApiService;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by daiqiang on 17/7/26.
 */
public class HttpApiFactoryBean implements FactoryBean {

    private HttpApiService service;

    private Class<?> targetClass;

    @Override
    public Object getObject() throws Exception {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[]{targetClass}, new InvocationHandler(){

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object api = service.getOrCreateService(targetClass);
                return method.invoke(api, args);
            }

        });
    }

    @Override
    public Class<?> getObjectType() {
        return targetClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public HttpApiService getService() {
        return service;
    }

    public void setService(HttpApiService service) {
        this.service = service;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }
}
