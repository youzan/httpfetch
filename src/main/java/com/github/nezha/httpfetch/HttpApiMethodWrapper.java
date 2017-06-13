package com.github.nezha.httpfetch;

import com.github.nezha.httpfetch.convertor.ResponseGeneratorConvertor;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * HttpApi接口封装类
 * @author 11047530
 *
 */
public class HttpApiMethodWrapper {

	/**
	 * 参数封装bean
	 */
	private MethodParameter[] parameters;
	
	/**
	 * 响应对象处理类
	 */
	private ResponseGeneratorConvertor generatorService;
	
	/**
	 * api接口响应封装类/可以为空，则以返回类处理
	 */
	private Class<?> responseCls;
	
	/**
	 * http请求方法POST\GET
	 */
	private String method;

    private Map<String,String> headers;
	
	/**
	 * api接口类\抽象类  函数的返回类
	 */
	private Class<?> returnCls;

	private Annotation[] annotations;

	private Integer timeout;

	private Integer readTimeout;

	private String encoding;

	public MethodParameter[] getParameters() {
		return parameters;
	}

	public void setParameters(MethodParameter[] parameters) {
		this.parameters = parameters;
	}

	public ResponseGeneratorConvertor getGeneratorService() {
		return generatorService;
	}

	public void setGeneratorService(ResponseGeneratorConvertor generatorService) {
		this.generatorService = generatorService;
	}

	public Class<?> getResponseCls() {
		return responseCls;
	}

	public void setResponseCls(Class<?> responseCls) {
		this.responseCls = responseCls;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Class<?> getReturnCls() {
		return returnCls;
	}

	public void setReturnCls(Class<?> returnCls) {
		this.returnCls = returnCls;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public Integer getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(Integer readTimeout) {
		this.readTimeout = readTimeout;
	}

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

	public boolean hasAnnotation(Class<?> annotation){
		if(annotations != null){
			for (Annotation a : annotations) {
				if(annotation.isInstance(a)){
					return true;
				}
			}
		}
		return false;
	}

	public Annotation[] getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Annotation[] annotations) {
		this.annotations = annotations;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
