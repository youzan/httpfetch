package com.github.nezha.httpfetch;

import com.alibaba.fastjson.JSON;
import com.github.nezha.httpfetch.convertor.ResponseGeneratorConvertor;
import com.github.nezha.httpfetch.interceptor.HttpApiInterceptor;
import com.github.nezha.httpfetch.resolver.MethodParameterResolver;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by daiqiang on 16/12/6.
 */
public class HttpApiService {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpApiService.class);

    private static HttpApiServiceWrapper serviceWrapper = null;

    private final static Map<Method, HttpApiMethodWrapper> methodsCache = new HashMap<Method, HttpApiMethodWrapper>();

    private final static Map<Class<?>, Object> serviceCache = new ConcurrentHashMap<>();

    static {

        serviceWrapper = new HttpApiServiceWrapper();

        XmlReader xmlReader = new XmlReader();
        xmlReader.read("httpapi.xml");

        serviceWrapper.addReader(xmlReader);
        serviceWrapper.init();
    }

    public static <T> T getOrCreateService(Class<T> serviceCls){
        Object service;
        if(!serviceCache.containsKey(serviceCls)){
            try {
                service = createService(serviceCls);
            } catch (Exception e) {
                String msg = String.format("服务创建失败! serviceCls [%s]", serviceCls);
                LOGGER.error(msg, e);
                throw new RuntimeException(msg);
            }
            serviceCache.put(serviceCls, service);
        }else{
            service = serviceCache.get(serviceCls);
        }
        return (T) service;
    }

    /**
     * 创建代理服务,对代理类和头参数等进行封装
     * @param serviceCls 服务接口类
     * @return
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private static Object createService(final Class<?> serviceCls) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ProxyFactory factory = new ProxyFactory();
        if(serviceCls.isInterface()){
            factory.setInterfaces(new Class[] {serviceCls});
        }else{
            String msg = String.format("类[%s]不是接口!", serviceCls);
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
        return factory.create(null, null, new HttpApiMethodHandler(serviceCls));
    }

    static class HttpApiMethodHandler implements MethodHandler {

        private Class<?> serviceCls;

        public HttpApiMethodHandler(Class<?> serviceCls) {
            this.serviceCls = serviceCls;
        }

        public Object invoke(Object target, Method method, Method arg2, Object[] args) throws Throwable {
            HttpApiMethodWrapper wrapper;
            if (methodsCache.containsKey(method)) {
                wrapper = methodsCache.get(method);
            } else {
                //未缓存 重新封装
                HttpApi httpApiAnno = method.getAnnotation(HttpApi.class);
                if (httpApiAnno == null) {
                    //不需要Http接口代理
                    return method.invoke(serviceCls, args);
                }
                wrapper = new HttpApiMethodWrapper();
                wrapper.setMethod(httpApiAnno.method());
                wrapper.setReturnCls(method.getReturnType());
                wrapper.setResponseCls(httpApiAnno.responseCls());
                wrapper.setAnnotations(method.getAnnotations());
                wrapper.setEncoding(httpApiAnno.encoding());

                //通过code匹配url
                wrapper.setUrl(getUrl(serviceCls, method));

                //获取参数名称
                MethodParameter[] parameters = wrapperPameters(method);
                wrapper.setParameters(parameters);

                //获取头参数
                Map<String, String> headers = getHeaders(httpApiAnno);
                wrapper.setHeaders(headers);

                //查询指定的结果转换类获取结果转换服务类
                ResponseGeneratorConvertor generatorService = null;
                if (StringUtils.isNotEmpty(httpApiAnno.generator())) {
                    for (ResponseGeneratorConvertor handler : serviceWrapper.getHandlers()) {
                        if (httpApiAnno.generator().equals(
                                handler.getClass().getSimpleName())) {
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

            //封装成请求参数
            HttpApiRequestParam param = new HttpApiRequestParam(wrapper.getUrl());

            //设置该函数默认encoding
            param.setEncoding(wrapper.getEncoding());

            executePreHandler(wrapper, args, param);

            generateParameter(wrapper, args, param);

            //取header
            Map<String, String> headers;
            if(param.getHeaders().isEmpty()){
                headers = wrapper.getHeaders();
            }else {
                headers = new HashMap<>(wrapper.getHeaders());
                headers.putAll(param.getHeaders());
            }

            byte[] responseBytes = null;
            try{
                executePreRequest(wrapper, args, param);
                if ("POST".equals(wrapper.getMethod())) {
                    responseBytes = HttpUtil.post(param.getUrl(), param.getParam(), param.getFileParam(),
                            param.getRequestBody(), headers, param.getEncoding(),
                            wrapper.getTimeout(), wrapper.getReadTimeout());
                } else {
                    responseBytes = HttpUtil.get(param.getUrl(), param.getParam(), headers, wrapper.getTimeout(), wrapper.getReadTimeout());
                }

                //请求后置处理
                responseBytes = executeAfterRequest(wrapper, args, param, responseBytes);

                //查询实现类
                Object response = generateResponse(method, responseBytes, wrapper);
                response = wrapper.getReturnCls().cast(response);

                //返回结果前处理
                response = executeBeforeReturn(wrapper, args, param, response);
                return response;
            }catch (Exception e){
                LOGGER.error("请求调用时发生异常! method [{}] param [{}]", method, JSON.toJSONString(param), e);
                Object response = executeCatchError(wrapper, args, param, responseBytes, e);
                if(response == null){
                    //meiyou,抛出异常
                    throw e;
                }else{
                    return response;
                }
            }
        }


        private Map<String, String> getHeaders(HttpApi anno){
            Map<String, String> headers = new HashMap<>();
            Header[] headersAnno = anno.headers();
            if(ArrayUtils.isNotEmpty(headersAnno)){
                for(Header header : headersAnno){
                    if(header != null){
                        headers.put(header.key(), header.value());
                    }
                }
            }
            return headers;
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
                Param param = null;
                for(Annotation annotation : annotations){
                    if(Param.class.isAssignableFrom(annotation.annotationType())){
                        param = (Param) annotation;
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

        /**
         * 执行拦截器预处理
         * @param wrapper
         * @param args
         * @param param
         * @return
         */
        private void executePreHandler(HttpApiMethodWrapper wrapper, Object[] args, HttpApiRequestParam param) {
            if(!CommonUtils.isCollectionEmpty(serviceWrapper.getInterceptors())){
                for (HttpApiInterceptor interceptor: serviceWrapper.getInterceptors()) {
                    if(interceptor != null){
                        interceptor.preHandle(param, wrapper, args);
                    }
                }
            }
        }

        /**
         * 执行拦截器请求执行前的处理
         * @param wrapper
         * @param args
         * @param param
         * @return
         */
        private void executePreRequest(HttpApiMethodWrapper wrapper, Object[] args, HttpApiRequestParam param) {
            if(!CommonUtils.isCollectionEmpty(serviceWrapper.getInterceptors())){
                for (HttpApiInterceptor interceptor: serviceWrapper.getInterceptors()) {
                    if(interceptor != null){
                        interceptor.preRequest(param, wrapper, args);
                    }
                }
            }
        }

        /**
         * 执行拦截器请求执行后,并封装完结果后的处理
         * @param wrapper
         * @param args
         * @param param
         * @return
         */
        private byte[] executeAfterRequest(HttpApiMethodWrapper wrapper, Object[] args,
                                         HttpApiRequestParam param, byte[] responseBytes) {
            if(!CommonUtils.isCollectionEmpty(serviceWrapper.getInterceptors())){
                for (HttpApiInterceptor interceptor: serviceWrapper.getInterceptors()) {
                    if(interceptor != null){
                        responseBytes = interceptor.afterRequest(param, wrapper, args, responseBytes);
                    }
                }
            }
            return responseBytes;
        }

        /**
         * 返回结果前处理
         * @param wrapper
         * @param args
         * @param param
         * @param response
         */
        private Object executeBeforeReturn(HttpApiMethodWrapper wrapper, Object[] args,
                                         HttpApiRequestParam param, Object response) {
            if(!CommonUtils.isCollectionEmpty(serviceWrapper.getInterceptors())){
                for (HttpApiInterceptor interceptor: serviceWrapper.getInterceptors()) {
                    if(interceptor != null){
                        response = interceptor.beforeReturn(param, wrapper, args, response);
                    }
                }
            }
            return response;
        }

        /**
         * 执行拦截器请求执行后,并封装完结果后的处理
         * @param wrapper
         * @param args
         * @param param
         * @return 捕捉异常后的返回值
         */
        private Object executeCatchError(HttpApiMethodWrapper wrapper, Object[] args,
                                       HttpApiRequestParam param, byte[] responseBytes, Exception ex) {
            if(!CommonUtils.isCollectionEmpty(serviceWrapper.getInterceptors())){
                for (HttpApiInterceptor interceptor: serviceWrapper.getInterceptors()) {
                    if(interceptor != null){
                        Object response = interceptor.catchError(param, wrapper, args, responseBytes, ex);
                        if(response != null){
                            return response;
                        }
                    }
                }
            }
            return null;
        }

        /**
         * 将入参值解析成HTTP请求参数
         * @param wrapper
         * @param args
         * @return
         */
        private void generateParameter(HttpApiMethodWrapper wrapper, Object[] args, HttpApiRequestParam param){
            //封装成请求参数
            MethodParameter[] parameters = wrapper.getParameters();
            if(parameters != null){
                for(int i=0;i<parameters.length;i++){
                    MethodParameter parameter = parameters[i];
                    MethodParameterResolver parameterResolversCache = parameter.getParameterResolver();
                    if(parameterResolversCache == null){
                        //遍历已注册的入参处理类
                        for(MethodParameterResolver parameterResolver : serviceWrapper.getParameterResolvers()){
                            if(parameterResolver.supperts(wrapper, parameter)){
                                //如果支持则注册到缓存中
                                parameterResolversCache = parameterResolver;
                                break;
                            }
                        }

                        parameter.setParameterResolver(parameterResolversCache);
                    }

                    if(parameterResolversCache != null){
                        parameterResolversCache.resolveArgument(param, parameters[i], wrapper, args[i]);
                    }
                }
            }
        }

        private Object generateResponse(Method method, byte[] response, HttpApiMethodWrapper wrapper){
            ResponseGeneratorConvertor service = wrapper.getGeneratorService();
            Class<?> responseCls = wrapper.getResponseCls();
            if(service != null){
                return wrapper.getGeneratorService().generate(method, wrapper, response, responseCls);
            }else{
                if(serviceWrapper.getHandlers() != null){
                    for(ResponseGeneratorConvertor generatorService : serviceWrapper.getHandlers()){
                        if(generatorService != null && generatorService.supports(method, wrapper, responseCls)){
                            return generatorService.generate(method, wrapper, response, responseCls);
                        }
                    }
                }
            }
            LOGGER.info("未找到可以用的结果处理类！method [%s] response [%s]", method.getName(), new String(response));
            return null;
        }

        private String getUrl(Class<?> serviceCls, Method method){
            HttpApi httpApi = method.getAnnotation(HttpApi.class);
            String code = httpApi.operateCode();
            if(StringUtils.isEmpty(code)){
                //如果为空，则使用默认的操作码
                String beanName = decapitalize(serviceCls.getSimpleName());
                String methodName = method.getName();
                code = beanName + "." + methodName;
            }
            Object urlObj = serviceWrapper.getUrlAlias().get(code);
            if(urlObj == null){
                String msg = String.format("url未找到! code [%s]", code);
                LOGGER.error(msg);
                throw new RuntimeException(msg);
            }
            return urlObj.toString();
        }

        public String decapitalize(String name) {
            if (name == null || name.length() == 0) {
                return name;
            }
            if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) &&
                    Character.isUpperCase(name.charAt(0))){
                return name;
            }
            char chars[] = name.toCharArray();
            chars[0] = Character.toLowerCase(chars[0]);
            return new String(chars);
        }
    }
}
