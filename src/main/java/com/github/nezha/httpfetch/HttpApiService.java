package com.github.nezha.httpfetch;

import com.github.nezha.httpfetch.chains.*;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by daiqiang on 16/12/6.
 */
public class HttpApiService {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpApiService.class);

    private HttpApiConfiguration configuration;

    private HttpApiInvoker startInvoker = null;

    private Map<Class<?>, Object> serviceCache = new ConcurrentHashMap<>();

    public HttpApiService(HttpApiConfiguration configuration){
        this.configuration = configuration;
    }

    @PostConstruct
    public void init(){
        //创建调用链
        List<HttpApiChain> chains = new ArrayList<>();
        chains.add(new MethodWrapperChain(configuration));
        chains.add(new UrlWrapperChain(configuration));
        chains.add(new ParameterResolverChain(configuration));
        chains.add(new GenerateResponseChain(configuration));
        chains.add(new ExecuteRequestChain());

        chains.addAll(configuration.getChains());

        //排序
        Collections.sort(chains, new Comparator<HttpApiChain>() {
            @Override
            public int compare(HttpApiChain o1, HttpApiChain o2) {
                return o1.getOrder() - o2.getOrder();
            }
        });

        HttpApiInvoker last = null;
        for(int i= chains.size()-1;i>=0;i--){
            final HttpApiChain chain = chains.get(i);
            final HttpApiInvoker next = last;
            last = new HttpApiInvoker(){
                @Override
                public HttpResult invoke(Invocation invocation) {
                    return chain.doChain(next, invocation);
                }
            };
        }
        startInvoker = last;
    }

    public <T> T getOrCreateService(Class<T> serviceCls){
        Object service;
        if(!serviceCache.containsKey(serviceCls)){
            try {
                service = createService(serviceCls);
            } catch (Exception e) {
                String msg = String.format("服务创建失败! serviceCls ["+serviceCls+"]");
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
    private Object createService(final Class<?> serviceCls) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
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

    class HttpApiMethodHandler implements MethodHandler {

        private Class<?> serviceCls;

        public HttpApiMethodHandler(Class<?> serviceCls) {
            this.serviceCls = serviceCls;
        }

        public Object invoke(Object target, Method method, Method arg2, Object[] args) throws Throwable {
            Invocation invocation = new Invocation();
            invocation.setRequestParam(new HttpApiRequestParam());
            invocation.setArgs(args);
            invocation.setMethod(method);
            invocation.setServiceCls(serviceCls);
            HttpResult httpResult = startInvoker.invoke(invocation);
            return httpResult.getData();
        }
    }

}
