package com.github.nezha.httpfetch.spring;

import com.github.nezha.httpfetch.HttpApiService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by daiqiang on 17/7/26.
 */
public class HttpApiFactoryBean implements FactoryBean, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Class<?> targetClass;

    @Override
    public Object getObject() throws Exception {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[]{targetClass}, new InvocationHandler(){

            private HttpApiService service;

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object api = getService().getOrCreateService(targetClass);
                return method.invoke(api);
            }

            public HttpApiService getService(){
                if(service == null){
                    synchronized (this){
                        if(service == null){
                            service = applicationContext.getBean(HttpApiService.class);
                        }
                    }
                }
                return service;
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

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
