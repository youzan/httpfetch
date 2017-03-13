package com.github.nezha.httpfetch.resolver;

import com.alibaba.fastjson.JSONObject;
import com.github.nezha.httpfetch.HttpApiMethodWrapper;
import com.github.nezha.httpfetch.HttpApiRequestParam;
import com.github.nezha.httpfetch.MethodParameter;
import org.apache.commons.lang3.StringUtils;
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
    public boolean supperts(HttpApiMethodWrapper wrapper, MethodParameter parameter) {
        //校验是否需要转换成byte[],供post请求体读取
        if("POST".equals(wrapper.getMethod()) && parameter.hasAnnotation(HttpApiReqeustBody.class)){
            return true;
        }
        return false;
    }

    @Override
    public void resolveArgument(HttpApiRequestParam param, MethodParameter methodParameter, HttpApiMethodWrapper wrapper, Object arg) {
        //从param中取参数值
        Class<?> parameterCls = methodParameter.getParameterType();
        if(parameterCls == null){
            return;
        }
        String value;
        if(String.class.isAssignableFrom(parameterCls)){
            value = arg.toString();
        }else if(parameterCls.isPrimitive()){
            value = String.valueOf(arg);
        }else{
            value = JSONObject.toJSONString(arg);
        }
        if(StringUtils.isNotEmpty(value)){
            byte[] body;
            try {
                body = value.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                String msg = String.format("解析成二进制时出错! value [%s]", value);
                LOGGER.error(msg, e);
                throw new RuntimeException(msg, e);
            }
            param.setRequestBody(body);
        }
        //从param中删掉
        param.removeParam(methodParameter.getParamName());
    }
}
