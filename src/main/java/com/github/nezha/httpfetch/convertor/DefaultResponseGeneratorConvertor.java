package com.github.nezha.httpfetch.convertor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.github.nezha.httpfetch.HttpApiMethodWrapper;
import com.github.nezha.httpfetch.HttpApiRequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;


/**
 * 默认的参数转换服务
 * @author 11047530
 *
 */
public class DefaultResponseGeneratorConvertor implements ResponseGeneratorConvertor {

    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultResponseGeneratorConvertor.class);

    @Override
    public Object generate(Method method, HttpApiMethodWrapper wrapper, HttpApiRequestParam requestParam, byte[] response, Class<?> responseCls) {
        Class<?> returnType = method.getReturnType();
        String value = null;
        try {
            value = new String(response, "UTF-8");
            LOGGER.debug("请求结果! value [{}]", value);
            if(returnType.isPrimitive()){
                //简单类型转换
                if(Boolean.class.isAssignableFrom(returnType)){
                    return Boolean.valueOf(value);
                }else if(Character.class.isAssignableFrom(returnType)){
                    return value.charAt(0);
                }else if(Byte.class.isAssignableFrom(returnType)){
                    return Byte.valueOf(value);
                }else if(Short.class.isAssignableFrom(returnType)){
                    return Short.valueOf(value);
                }else if(Integer.class.isAssignableFrom(returnType)){
                    return Integer.valueOf(value);
                }else if(Long.class.isAssignableFrom(returnType)){
                    return Long.valueOf(value);
                }else if(Float.class.isAssignableFrom(returnType)){
                    return Float.valueOf(value);
                }else if(Double.class.isAssignableFrom(returnType)){
                    return Double.valueOf(value);
                }
                //Void
                return null;
            }else if(String.class.isAssignableFrom(returnType)){
                //String
                return value;
            }else if(Collection.class.isAssignableFrom(returnType)){
                if(method.getGenericReturnType() == method.getReturnType()){
                    //如果相同，表示没有泛型,直接返回集合
                    return JSONArray.parseArray(value);
                }else{
                    Class<?> genericReturnType = (Class<?>) ((ParameterizedType) method.getGenericReturnType())
                            .getActualTypeArguments()[0];
                    if(Map.class.isAssignableFrom(genericReturnType)){
                        //如果是map直接 转换
                        return JSONArray.parseArray(value);
                    }else{
                        //更具结果类转换
                        return JSONArray.parseArray(value, genericReturnType);
                    }
                }
            }else if(Map.class.isAssignableFrom(returnType)){
                return JSONObject.parseObject(value);
            }else{
                return JSONObject.parseObject(value, new DspResponseTypeReference(method.getGenericReturnType()));
            }
        } catch (Exception e) {
            String msg = "读取请求结果时出错！";
            LOGGER.error(msg+" value [{}]", value, e);
            throw new RuntimeException(msg, e);
        }
    }

    @Override
    public boolean supports(Method method, HttpApiMethodWrapper wrapper, HttpApiRequestParam requestParam, Class<?> responseCls) {
        return true;
    }

    public static class DspResponseTypeReference<T> extends TypeReference<T> {
        private Type type;

        public DspResponseTypeReference(Type type){
            this.type = type;
        }

        public Type getType() {
            return this.type;
        }
    }
}
