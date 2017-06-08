package com.github.nezha.httpfetch;

import com.github.nezha.httpfetch.resolver.MethodParameterResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class MethodParameter {

	/**
	 * api函数对象
	 */
	private final Method method;
	/**
	 * 参数序号
	 */
	private final int parameterIndex;
	/**
	 * 接口入参类型
	 */
	private Class<?> parameterType;
	/**
	 * 
	 */
	private Type genericParameterType;
	/**
	 * 参数注解
	 */
	private Annotation[] parameterAnnotations;

	/**
	 * 参数处理类，缓存
	 */
	private MethodParameterResolver parameterResolver;

	/**
	 * http参数名
	 */
	private String paramName;

	public MethodParameter(Method method, int parameterIndex, 
			Class<?> parameterType, String paramName) {
		this.method = method;
		this.parameterIndex = parameterIndex;
		this.parameterType = parameterType;
		this.paramName = paramName;
	}

	/**
	 * 校验是否包含该注解
	 * @param targetAnno
	 * @return
	 */
	public boolean hasAnnotation(Class<?> targetAnno){
		Annotation[] annos = this.getParameterAnnotations();
		if(!CommonUtils.isArrayEmpty(annos)){
			for(Annotation anno : annos){
				if(targetAnno.isInstance(anno)){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 校验是否包含该注解
	 * @param targetAnno
	 * @return
	 */
	public <T> T getAnnotation(Class<T> targetAnno){
		Annotation[] annos = this.getParameterAnnotations();
		if(!CommonUtils.isArrayEmpty(annos)){
			for(Annotation anno : annos){
				if(targetAnno.isInstance(anno)){
					return (T) anno;
				}
			}
		}
		return null;
	}
	
	public Class<?> getParameterType() {
		return parameterType;
	}

	public void setParameterType(Class<?> parameterType) {
		this.parameterType = parameterType;
	}

	public Type getGenericParameterType() {
		return genericParameterType;
	}

	public void setGenericParameterType(Type genericParameterType) {
		this.genericParameterType = genericParameterType;
	}

	public Annotation[] getParameterAnnotations() {
		return parameterAnnotations;
	}

	public void setParameterAnnotations(Annotation[] parameterAnnotations) {
		this.parameterAnnotations = parameterAnnotations;
	}

	public MethodParameterResolver getParameterResolver() {
		return parameterResolver;
	}

	public void setParameterResolver(MethodParameterResolver parameterResolver) {
		this.parameterResolver = parameterResolver;
	}

	public Method getMethod() {
		return method;
	}

	public int getParameterIndex() {
		return parameterIndex;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	
}
