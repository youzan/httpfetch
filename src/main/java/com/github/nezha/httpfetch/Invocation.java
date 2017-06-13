package com.github.nezha.httpfetch;

import java.lang.reflect.Method;

/**
 * Created by daiqiang on 17/6/13.
 */
public class Invocation {

    private HttpApiRequestParam requestParam;

    private HttpApiMethodWrapper wrapper;

    private Object[] args;

    private Method method;

    private Class<?> serviceCls;

    public HttpApiRequestParam getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(HttpApiRequestParam requestParam) {
        this.requestParam = requestParam;
    }

    public HttpApiMethodWrapper getWrapper() {
        return wrapper;
    }

    public void setWrapper(HttpApiMethodWrapper wrapper) {
        this.wrapper = wrapper;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getServiceCls() {
        return serviceCls;
    }

    public void setServiceCls(Class<?> serviceCls) {
        this.serviceCls = serviceCls;
    }

}
