package com.github.nezha.httpfetch.resolver;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.github.nezha.httpfetch.CommonUtils;
import com.github.nezha.httpfetch.HttpApiMethodWrapper;
import com.github.nezha.httpfetch.HttpApiRequestParam;
import com.github.nezha.httpfetch.MethodParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 将简单bean转换成http参数的Resolver,仅处理bean中第一层的变量
 * @author 11047530
 *
 */
public class BeanMethodParameterResolver implements MethodParameterResolver {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(BeanMethodParameterResolver.class);

	private Map<Class<?>, List<Field>> beanGetterNamesCache = new HashMap<>();

	private Map<Class<?>, List<String>> paramNamesCache = new HashMap<>();
	
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
		List<Field> getterFields;
		List<String> paramNames;
		if(!beanGetterNamesCache.containsKey(cls)){
	        getterFields = new ArrayList<>();
			paramNames = new ArrayList<>();
			Set<Field> fields = CommonUtils.getAllFieldWithSuper(cls);
			Iterator<Field> fieldIterator = fields.iterator();
			while(fieldIterator.hasNext()){
				Field field = fieldIterator.next();
				String name = field.getName();
				if ("class".equals(name)) {
					continue;
				}
				//取getter
				//取param name
				//TODO 后期如果可以的话转成Param
				JSONField jsonField = field.getAnnotation(JSONField.class);
				if(jsonField != null && !CommonUtils.isStringEmpty(jsonField.name())){
					paramNames.add(jsonField.name());
				}else{
					paramNames.add(name);
				}
				//取getter
				field.setAccessible(true);
				getterFields.add(field);
			}
	        beanGetterNamesCache.put(cls, getterFields);
			paramNamesCache.put(cls, paramNames);
		}else{
			getterFields = beanGetterNamesCache.get(cls);
			paramNames = paramNamesCache.get(cls);
		}
		if(getterFields != null){
			for (int i = 0; i < getterFields.size(); i++) {
				Field field = getterFields.get(i);
				String name = field.getName();
				try {
					String paramName = paramNames.get(i);
					Object value = field.get(arg);
					if(value != null){
						Class<?> parameterCls = value.getClass();

						if(field.getAnnotation(ArrayParam.class) != null && Collection.class.isAssignableFrom(parameterCls)){
							//数组
							StringBuffer url = new StringBuffer(param.getUrl());
							if(param.getUrl().indexOf("?") < 0 ){
								url.append("?");
							}
							Collection collection = (Collection)value;
							for(Object e : collection){
								if(e != null){
									url.append("&");
									url.append(paramName);
									url.append("[]=");
									url.append(this.parseParameter(e));
								}
							}
							param.setUrl(url.toString());
						}else{
							PostParam postParam = methodParameter.getAnnotation(PostParam.class);
							FormParam formParam = methodParameter.getAnnotation(FormParam.class);
							if(formParam != null){
								if(value instanceof File){
									param.addFormParam(paramName, value);
								} else {
									param.addFormParam(paramName, this.parseParameter(value));
								}
							} else if(postParam != null){
								param.addPostParam(paramName, this.parseParameter(value));
							}else{
								param.addGetParam(paramName, this.parseParameter(value));
							}
						}
					}
				} catch (Exception e) {
					LOGGER.error("值填充时出错！  paramBeanCls [{}] name [{}]", arg.getClass(), name, e);
				}
			}
		}
	}

	private String parseParameter(Object value){
		Class parameterCls = value.getClass();
		if(String.class.isAssignableFrom(parameterCls)){
			return value.toString();
		}else if(CommonUtils.isPrimitive(parameterCls)){
			return String.valueOf(value);
		}else if(parameterCls.isEnum()){
			return value.toString();
		}else {
			return JSONObject.toJSONString(value);
		}
	}
	
}
