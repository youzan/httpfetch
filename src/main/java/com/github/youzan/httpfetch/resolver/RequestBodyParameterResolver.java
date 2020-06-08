package com.github.youzan.httpfetch.resolver;

import com.alibaba.fastjson.JSONObject;
import com.github.youzan.httpfetch.*;
import com.github.youzan.httpfetch.CommonUtils;
import com.github.youzan.httpfetch.HttpApiMethodWrapper;
import com.github.youzan.httpfetch.HttpApiRequestParam;
import com.github.youzan.httpfetch.RequestParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * Created by daiqiang on 16/12/6.
 * 解析字符串输出到body
 */
public class RequestBodyParameterResolver implements MethodParameterResolver {

    private final static Logger LOGGER = LoggerFactory.getLogger(RequestBodyParameterResolver.class);

    @Override
    public boolean supperts(HttpApiMethodWrapper wrapper, RequestParameter requestParameter) {
        //校验是否需要转换成byte[],供post请求体读取
        boolean isPostRequest = "POST".equals(wrapper.getMethod());
        boolean hasRequestBodyAnno = requestParameter.getParameterWrapper().hasAnnotation(RequestBody.class);
        if(isPostRequest && hasRequestBodyAnno){
            return true;
        }
        return false;
    }

    @Override
    public boolean resolveArgument(HttpApiRequestParam param, HttpApiMethodWrapper wrapper, RequestParameter requestParameter) {
        //从param中取参数值
        Class<?> parameterCls = requestParameter.getParameterWrapper().getParameterType();
        if(parameterCls == null){
            return false;
        }
        Object arg = requestParameter.getParameter();
        byte[] bytes = parseBytes(parameterCls, arg);
        if(bytes != null){
            param.setRequestBody(bytes);
        }
        return false;
    }

    private byte[] parseBytes(Class<?> parameterCls, Object arg){
        if(parameterCls.isArray()
                && byte.class.isAssignableFrom(parameterCls.getComponentType())){
            return (byte[])arg;
        }
        String value;
        if(String.class.isAssignableFrom(parameterCls)){
            value = arg.toString();
        }else if(parameterCls.isPrimitive()){
            value = String.valueOf(arg);
        }else{
            value = JSONObject.toJSONString(arg);
        }
        if(!CommonUtils.isStringEmpty(value)){
            byte[] body;
            try {
                body = value.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                String msg = String.format("解析成二进制时出错! value [%s]", value);
                LOGGER.error(msg, e);
                throw new RuntimeException(msg, e);
            }
            return body;
        }
        return null;
    }

}
