package com.github.nezha.httpfetch.chains;

import com.alibaba.fastjson.annotation.JSONField;
import com.github.nezha.httpfetch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by daiqiang on 17/6/14.
 * 解析beanparam注解
 */
public class BeanParamParseChain implements HttpApiChain {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanParamParseChain.class);

    private Map<Class<?>, List<Field>> beanGetterNamesCache = new HashMap<>();

    private Map<Class<?>, List<String>> paramNamesCache = new HashMap<>();

    @Override
    public HttpResult doChain(HttpApiInvoker invoker, Invocation invocation) {
        List<RequestParameter> originParameters = invocation.getParameters();
        if(!CommonUtils.isCollectionEmpty(originParameters)){
            checkAndParseBeanParamPameters(originParameters);
        }
        return invoker.invoke(invocation);
    }

    /**
     * 校验是否包含beanParam注解对象, 如果需要则解析该对象
     * @param originParameters
     */
    private void checkAndParseBeanParamPameters(List<RequestParameter> originParameters){
        List<RequestParameter> hasParsedParamerers = new ArrayList<>();

        Iterator<RequestParameter> requestParameterIterator = originParameters.iterator();
        while(requestParameterIterator.hasNext()){
            RequestParameter requestParameter = requestParameterIterator.next();
            ParameterWrapper parameterWrapper = requestParameter.getParameterWrapper();

            //校验是否 beanparam标注
            if(parameterWrapper.hasAnnotation(BeanParam.class)){
                //原有的bean param 可以移除掉了
                requestParameterIterator.remove();

                //解析单个bean param标注对象
                List<RequestParameter> single = parseSingleBeanParam(parameterWrapper, requestParameter.getParameter());
                if(single != null){
                    hasParsedParamerers.addAll(single);
                }
            }
        }

        //添加到 requestParameter中
        originParameters.addAll(hasParsedParamerers);
    }

    /**
     * 分装单个bean param标注对象
     * @param parameterWrapper
     * @param parameter
     * @return
     */
    private List<RequestParameter> parseSingleBeanParam(ParameterWrapper parameterWrapper, Object parameter){

        //有注解则解析
        Class<?> cls = parameter.getClass();
        tryParseAndCacheFields(parameter.getClass());

        List<Field> getterFields = beanGetterNamesCache.get(cls);
        List<String> paramNames = paramNamesCache.get(cls);

        if(getterFields != null){
            List<RequestParameter> beanRequestParameters = new ArrayList<>();
            for (int i = 0; i < getterFields.size(); i++) {
                Field field = getterFields.get(i);
                String paramName = paramNames.get(i);
                RequestParameter requestParameter = wrapRequestParameter(field, paramName, parameterWrapper, parameter);
                if(requestParameter != null){
                    beanRequestParameters.add(requestParameter);
                }
            }
            return beanRequestParameters;
        }
        return null;
    }

    /**
     * 解析和封装单个requestParameter
     * @param field
     * @param paramName
     * @param parameterWrapper
     * @param parameter
     * @return
     */
    private RequestParameter wrapRequestParameter(Field field, String paramName, ParameterWrapper parameterWrapper, Object parameter){
        try {
            Object value = field.get(parameter);
            if(value != null){

                //获取bean中field的注解
                Annotation[] paramAnnos = field.getAnnotations();
                //获取method中的parameter注解
                Annotation[] methodParamAnnos = parameterWrapper.getParameterAnnotations();
                //合并注解
                Annotation[] mergeAnnos = CommonUtils.concatArray(paramAnnos, methodParamAnnos);

                ParameterWrapper fieldParameterWrapper = new ParameterWrapper(
                        field.getType(), paramName,
                        field.getGenericType(), mergeAnnos);

                return new RequestParameter(fieldParameterWrapper, value);
            }
        } catch (Exception e) {
            LOGGER.error("解析bean param时出错！  paramBeanCls [{}] name [{}]", parameter.getClass(), paramName, e);
            throw new RuntimeException("解析bean param时出错！");
        }
        return null;
    }

    /**
     * 尝试解析和缓存 解析出的field和name
     * @param cls
     */
    private void tryParseAndCacheFields(Class<?> cls){
        if(!beanGetterNamesCache.containsKey(cls)){
            List<Field> getterFields = new ArrayList<>();
            List<String> paramNames = new ArrayList<>();
            Set<Field> fields = CommonUtils.getAllFieldWithSuper(cls);
            Iterator<Field> fieldIterator = fields.iterator();
            while(fieldIterator.hasNext()){
                Field field = fieldIterator.next();
                String name = field.getName();
                if ("class".equals(name)) {
                    continue;
                }
                //取param name
                JSONField jsonField = field.getAnnotation(JSONField.class);
                if(jsonField != null && !CommonUtils.isStringEmpty(jsonField.name())){
                    paramNames.add(jsonField.name());
                }else{
                    paramNames.add(name);
                }
                //缓存field
                field.setAccessible(true);
                getterFields.add(field);
            }
            beanGetterNamesCache.put(cls, getterFields);
            paramNamesCache.put(cls, paramNames);
        }
    }

    @Override
    public int getOrder() {
        return 10000;
    }

}
