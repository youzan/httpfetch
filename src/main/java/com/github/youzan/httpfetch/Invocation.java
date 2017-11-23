package com.github.youzan.httpfetch;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daiqiang on 17/6/13.
 */
public class Invocation {

    private HttpApiRequestParam requestParam;

    private HttpApiMethodWrapper wrapper;

    /**
     * 参数封装bean
     */
    private List<RequestParameter> parameters = new ArrayList<>();

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

    public List<RequestParameter> getParameters() {
        return parameters;
    }

    public void addParameters(RequestParameter parameter) {
        this.parameters.add(parameter);
    }

}
