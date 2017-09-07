package com.github.nezha.httpfetch.resolver;

import com.alibaba.fastjson.JSONObject;
import com.github.nezha.httpfetch.CommonUtils;

/**
 * Created by daiqiang on 17/9/7.
 */
public class ParameterUtils {

    public static String parseParameter(Object value){
        if(value == null){
            return "";
        }
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
