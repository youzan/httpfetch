package com.github.youzan.httpfetch.chains;

import com.github.youzan.httpfetch.*;
import com.github.youzan.httpfetch.*;
import com.github.youzan.httpfetch.convertor.ResponseGeneratorConvertor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by daiqiang on 17/6/13.
 */
public class GenerateResponseChain implements HttpApiChain {

    private HttpApiConfiguration configuration;

    public GenerateResponseChain(HttpApiConfiguration configuration){
        this.configuration = configuration;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateResponseChain.class);

    @Override
    public HttpResult doChain(HttpApiInvoker invoker, Invocation invocation) {
        HttpResult result = invoker.invoke(invocation);
        if(!result.hasException()){
            byte[] object = (byte[]) result.getData();
            Object returnObject = generateResponse(invocation.getMethod(), object, invocation.getRequestParam(), invocation.getWrapper());
            result.setData(returnObject);
        }
        return result;
    }

    private Object generateResponse(Method method, byte[] response, HttpApiRequestParam requestParam, HttpApiMethodWrapper wrapper){
        ResponseGeneratorConvertor service = wrapper.getGeneratorService();
        Class<?> responseCls = wrapper.getResponseCls();
        if(service != null){
            return service.generate(method, wrapper, requestParam, response, responseCls);
        }else{
            List<ResponseGeneratorConvertor> convertors = configuration.getConvertors();
            if(convertors != null){
                for(ResponseGeneratorConvertor convertor : convertors){
                    if(convertor != null && convertor.supports(method, wrapper, requestParam, responseCls)){
                        return convertor.generate(method, wrapper, requestParam, response, responseCls);
                    }
                }
            }
        }
        LOGGER.info("未找到可以用的结果处理类！method [%s] response [%s]", method.getName(), new String(response));
        return null;
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 1000;
    }
}
