package com.github.youzan.httpfetch.chains;

import com.github.youzan.httpfetch.*;
import com.github.youzan.httpfetch.resolver.FormParam;
import com.github.youzan.httpfetch.resolver.MethodParameterResolver;
import com.github.youzan.httpfetch.resolver.PostParam;
import com.github.youzan.httpfetch.resolver.RequestBody;
import com.github.youzan.httpfetch.*;

import java.util.List;

/**
 * Created by daiqiang on 17/6/13.
 */
public class ParameterResolverChain implements HttpApiChain {

    private HttpApiConfiguration configuration;

    public ParameterResolverChain(HttpApiConfiguration configuration){
        this.configuration = configuration;
    }

    @Override
    public HttpResult doChain(HttpApiInvoker invoker, Invocation invocation) {
        HttpApiMethodWrapper wrapper = invocation.getWrapper();
        HttpApiRequestParam requestParam = invocation.getRequestParam();
        List<RequestParameter> requestParameters = invocation.getParameters();
        if(requestParameters != null){
            //遍历所有的参数
            for(RequestParameter requestParameter : requestParameters){
                MethodParameterResolver resolver = choiseResolver(wrapper, requestParameter);
                if(resolver != null){
                    boolean ifSetParam = resolver.resolveArgument(requestParam, wrapper, requestParameter);
                    if(!ifSetParam){
                        //不写道param
                        continue;
                    }
                }

                this.wrapRequestParam(requestParameter, requestParam);
            }
        }
        return invoker.invoke(invocation);
    }

    private void wrapRequestParam(RequestParameter requestParameter, HttpApiRequestParam requestParam){
        //封装请求参数
        ParameterWrapper parameterWrapper = requestParameter.getParameterWrapper();
        Object arg = requestParameter.getParameter();
        if(parameterWrapper.hasAnnotation(FormParam.class)){
            requestParam.addFormParam(parameterWrapper.getParamName(), arg);
        }else if(parameterWrapper.hasAnnotation(PostParam.class)){
            requestParam.addPostParam(parameterWrapper.getParamName(), String.valueOf(arg));
        }else if(parameterWrapper.hasAnnotation(RequestBody.class)
                && arg.getClass().isArray()
                && arg.getClass().getComponentType().equals(byte.class)){
            requestParam.setRequestBody((byte[]) arg);
        }else{
            requestParam.addGetParam(parameterWrapper.getParamName(), String.valueOf(arg));
        }
    }

    /**
     * 筛选并执行resolver解析参数
     * @param wrapper
     * @param requestParameter
     * @return
     */
    private MethodParameterResolver choiseResolver(HttpApiMethodWrapper wrapper, RequestParameter requestParameter){
        ParameterWrapper parameterWrapper = requestParameter.getParameterWrapper();
        //遍历已注册的入参处理类
        for(MethodParameterResolver parameterResolver : configuration.getParameterResolvers()){
            if(parameterResolver.supperts(wrapper, requestParameter)){
                //如果支持则注册到缓存中
                return parameterResolver;
            }
        }
        return null;
    }

    @Override
    public int getOrder() {
        return 8000;
    }
}
