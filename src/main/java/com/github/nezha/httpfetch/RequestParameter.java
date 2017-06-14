package com.github.nezha.httpfetch;

/**
 * Created by daiqiang on 17/6/14.
 * 封装参数,及其相关信息
 */
public class RequestParameter {

    private ParameterWrapper parameterWrapper;

    private Object parameter;

    public RequestParameter(ParameterWrapper parameterWrapper, Object parameter){
        this.parameterWrapper = parameterWrapper;
        this.parameter = parameter;
    }

    public ParameterWrapper getParameterWrapper() {
        return parameterWrapper;
    }

    public Object getParameter() {
        return parameter;
    }

    public void setParameter(Object parameter) {
        this.parameter = parameter;
    }
}
