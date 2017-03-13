package com.github.nezha.httpfetch.resolver;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.github.nezha.httpfetch.HttpApiMethodWrapper;
import com.github.nezha.httpfetch.HttpApiRequestParam;
import com.github.nezha.httpfetch.MethodParameter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将简单bean转换成http参数的Resolver,仅处理bean中第一层的变量
 * @author 11047530
 *
 */
public class BeanMethodParameterResolver implements MethodParameterResolver {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(BeanMethodParameterResolver.class);

	private Map<Class<?>, List<String>> beanGetterNamesCache = new HashMap<Class<?>, List<String>>();

	private Map<Class<?>, List<String>> paramNamesCache = new HashMap<Class<?>, List<String>>();
	
	@Override
	public boolean supperts(HttpApiMethodWrapper wrapper, MethodParameter parameter) {
		return parameter.hasAnnotation(BeanParam.class);
	}

	@Override
	public void resolveArgument(HttpApiRequestParam param,
			MethodParameter methodParameter, HttpApiMethodWrapper wrapper,
			Object arg) {
		//将bean转换成map
		Class<?> cls = arg.getClass();
		List<String> getterNames;
		List<String> paramNames;
		if(!beanGetterNamesCache.containsKey(cls)){
	        getterNames = new ArrayList<>();
			paramNames = new ArrayList<>();
			Field[] fields = cls.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				String name = fields[i].getName();
				if ("class".equals(name)) {
					continue;
				}
				//取getter
				if (PropertyUtils.isReadable(arg, name)) {
					//取param name
					//TODO 后期如果可以的话转成Param
					JSONField jsonField = fields[i].getAnnotation(JSONField.class);
					if(jsonField != null && StringUtils.isNotEmpty(jsonField.name())){
						paramNames.add(jsonField.name());
					}else{
						paramNames.add(name);
					}
					//取getter
					getterNames.add(name);
				}
			}
	        beanGetterNamesCache.put(cls, getterNames);
			paramNamesCache.put(cls, paramNames);
		}else{
			getterNames = beanGetterNamesCache.get(cls);
			paramNames = paramNamesCache.get(cls);
		}
		if(getterNames != null){
			for (int i = 0; i < getterNames.size(); i++) {
				String name = getterNames.get(i);
				try {
					String paramName = paramNames.get(i);
					Object value = PropertyUtils.getSimpleProperty(arg, name);
					if(value != null){
						Class<?> parameterCls = value.getClass();
						if(String.class.isAssignableFrom(parameterCls)){
							param.addParam(paramName, value.toString());
						}else if(isPrimitive(parameterCls)){
							param.addParam(paramName, String.valueOf(value));
						}else if(parameterCls.isEnum()){
							param.addParam(paramName, value.toString());
						}else{
							param.addParam(paramName, JSONObject.toJSONString(value));
						}
					}
				} catch (Exception e) {
					LOGGER.error("值填充时出错！  paramBeanCls [{}] name [{}]", arg.getClass(), name, e);
				}
			}
		}
	}

	/**
	 * 校验是否简单类型
	 *
	 * @param clazz
	 * @return
	 */
	public boolean isPrimitive(Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("参数有误!");
		}
		if (clazz.isPrimitive()) {
			return true;
		}
		if (clazz == Boolean.class
				|| clazz == Character.class
				|| clazz == Byte.class
				|| clazz == Short.class
				|| clazz == Integer.class
				|| clazz == Long.class
				|| clazz == Float.class
				|| clazz == Double.class
				|| clazz == Void.class
				) {
			return true;
		}
		return false;
	}
}
