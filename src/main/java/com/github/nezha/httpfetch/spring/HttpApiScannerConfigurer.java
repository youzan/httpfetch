package com.github.nezha.httpfetch.spring;

import com.github.nezha.httpfetch.HttpApiService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

/**
 * Created by daiqiang on 17/5/25.
 * 自动解析HttpApi的注解
 */
public class HttpApiScannerConfigurer implements BeanDefinitionRegistryPostProcessor {

    private HttpApiService service;

    private String basePackage;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        HttpApiClassPathBeanDefinitionScanner scanner = new HttpApiClassPathBeanDefinitionScanner(registry, service);
        scanner.register();
        scanner.doScan(StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    public HttpApiService getService() {
        return service;
    }

    public void setService(HttpApiService service) {
        this.service = service;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }
}
