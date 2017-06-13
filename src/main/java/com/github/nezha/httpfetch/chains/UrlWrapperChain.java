package com.github.nezha.httpfetch.chains;

import com.github.nezha.httpfetch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by daiqiang on 17/6/13.
 * 获取url
 */
public class UrlWrapperChain implements HttpApiChain {

    private Logger LOGGER = LoggerFactory.getLogger(UrlWrapperChain.class);

    private HttpApiConfiguration configuration;

    public UrlWrapperChain(HttpApiConfiguration configuration){
        this.configuration = configuration;
    }

    @Override
    public HttpResult doChain(HttpApiInvoker invoker, Invocation invocation) {
        //获取url
        String url = getUrl(invocation.getServiceCls(), invocation.getMethod());
        invocation.getRequestParam().setUrl(url);
        return invoker.invoke(invocation);
    }

    private String getUrl(Class<?> serviceCls, Method method){
        HttpApi httpApi = method.getAnnotation(HttpApi.class);
        if(!CommonUtils.isStringEmpty(httpApi.url())){
            //如果注解中已经写明了url,则直接取 注解的url
            return httpApi.url();
        }
        String code = httpApi.operateCode();
        if(CommonUtils.isStringEmpty(code)){
            //如果为空，则使用默认的操作码
            String beanName = CommonUtils.decapitalize(serviceCls.getSimpleName());
            String methodName = method.getName();
            code = beanName + "." + methodName;
        }
        Object urlObj = configuration.getUrlAlias().get(code);
        if(urlObj == null){
            String msg = String.format("url未找到! code [%s]", code);
            LOGGER.error(msg);
            throw new RuntimeException(msg);
        }
        return urlObj.toString();
    }

    @Override
    public int getOrder() {
        return 9000;
    }
}
