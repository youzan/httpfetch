package com.github.nezha.httpfetch.chains;

import com.github.nezha.httpfetch.*;
import com.github.nezha.httpfetch.convertor.ResponseGeneratorConvertor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by daiqiang on 17/6/13.
 */
public class MethodWrapperChain implements HttpApiChain {

    private HttpApiConfiguration configuration;

    public MethodWrapperChain(HttpApiConfiguration configuration){
        this.configuration = configuration;
    }

    private Logger LOGGER = LoggerFactory.getLogger(MethodWrapperChain.class);

    private Map<Method, HttpApiMethodWrapper> methodsCache = new HashMap<>();

    @Override
    public HttpResult doChain(HttpApiInvoker invoker, Invocation invocation) {
        Method method = invocation.getMethod();
        //未缓存 重新封装
        HttpApi httpApiAnno = method.getAnnotation(HttpApi.class);
        if (httpApiAnno == null) {
            //没有httpApi注解
            throw new RuntimeException("系统异常,httpapi注解不存在!");
        }
        HttpApiMethodWrapper wrapper = getOrNewWrapper(invocation, httpApiAnno);
        invocation.setWrapper(wrapper);
        //设置encoding
        invocation.getRequestParam().setEncoding(wrapper.getEncoding());
        invocation.getRequestParam().addHeaders(wrapper.getHeaders());

        return invoker.invoke(invocation);
    }

    public HttpApiMethodWrapper getOrNewWrapper(Invocation invocation, HttpApi httpApiAnno){
        HttpApiMethodWrapper wrapper;
        Method method = invocation.getMethod();
        if (methodsCache.containsKey(method)) {
            wrapper = methodsCache.get(method);
        } else {
            wrapper = new HttpApiMethodWrapper();
            wrapper.setMethod(httpApiAnno.method());
            wrapper.setReturnCls(method.getReturnType());
            wrapper.setAnnotations(method.getAnnotations());
            wrapper.setEncoding(httpApiAnno.encoding());

            //获取参数名称
            MethodParameter[] parameters = wrapperPameters(method);
            wrapper.setParameters(parameters);

            //获取头参数
            Map<String, String> headers = getHeaders(httpApiAnno);
            wrapper.setHeaders(headers);

            //查询指定的结果转换类获取结果转换服务类
            ResponseGeneratorConvertor generatorService = null;
            if (!CommonUtils.isStringEmpty(httpApiAnno.generator())) {
                for (ResponseGeneratorConvertor handler : configuration.getConvertors()) {
                    if (httpApiAnno instanceof ResponseGeneratorConvertor){
                        generatorService = handler;
                        break;
                    }
                }
            }

            wrapper.setGeneratorService(generatorService);

            //去函数的返回类
            wrapper.setResponseCls(method.getReturnType());

            if (httpApiAnno.timeout() > 0) {
                wrapper.setTimeout(httpApiAnno.timeout());
            }
            if (httpApiAnno.readTimeout() > 0) {
                wrapper.setReadTimeout(httpApiAnno.readTimeout());
            }

            methodsCache.put(method, wrapper);
        }
        return wrapper;
    }

    /**
     * 从methods中读取
     * @param method
     * @return
     */
    private MethodParameter[] wrapperPameters(Method method){
        Annotation[][] annotationArray = method.getParameterAnnotations();
        if(annotationArray == null || annotationArray.length == 0){
            LOGGER.info("该函数没有参数！method [{}]", method.getName());
            return new MethodParameter[]{};
        }
        String[] paramNames = new String[annotationArray.length];
        for(int i=0;i<annotationArray.length;i++){
            //校验里面是否有param注解
            Annotation[] annotations = annotationArray[i];
            QueryParam param = null;
            for(Annotation annotation : annotations){
                if(QueryParam.class.isAssignableFrom(annotation.annotationType())){
                    param = (QueryParam) annotation;
                    break;
                }
            }
            if(param == null){
                paramNames[i] = "";
            }else{
                paramNames[i] = param.value();
            }
        }

        MethodParameter[] parameters = new MethodParameter[paramNames.length];
        for(int i=0;i<paramNames.length;i++){
            parameters[i] = wrapParameter(paramNames[i], i, method, annotationArray[i]);
        }
        return parameters;
    }

    private Map<String, String> getHeaders(HttpApi anno){
        Map<String, String> headers = new HashMap<>();
        Header[] headersAnno = anno.headers();
        if(!CommonUtils.isArrayEmpty(headersAnno)){
            for(Header header : headersAnno){
                if(header != null){
                    headers.put(header.key(), header.value());
                }
            }
        }
        return headers;
    }

    /**
     * 封装参数
     * @param paramName
     * @param index
     * @param method
     * @param annotations
     * @return
     */
    private MethodParameter wrapParameter(String paramName, int index, Method method, Annotation[] annotations){
        Class<?> parameterType = method.getParameterTypes()[index];
        method.getParameterAnnotations();
        MethodParameter parameter = new MethodParameter(method, index, parameterType, paramName);
        parameter.setGenericParameterType(method.getGenericParameterTypes()[index]);
        parameter.setParameterAnnotations(annotations);
        return parameter;
    }

}