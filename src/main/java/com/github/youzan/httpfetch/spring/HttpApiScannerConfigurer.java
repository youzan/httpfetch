package com.github.youzan.httpfetch.spring;

import com.github.youzan.httpfetch.HttpApiService;
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

    private String basePackage;

    private HttpApiService httpApiService;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        HttpApiClassPathBeanDefinitionScanner scanner = new HttpApiClassPathBeanDefinitionScanner(registry, httpApiService);
        scanner.register();
        scanner.doScan(StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public HttpApiService getHttpApiService() {
        return httpApiService;
    }

    public void setHttpApiService(HttpApiService httpApiService) {
        this.httpApiService = httpApiService;
    }
}
