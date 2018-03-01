package com.github.youzan.httpfetch.convertor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.github.youzan.httpfetch.ByteConvertorUtil;
import com.github.youzan.httpfetch.CommonUtils;
import com.github.youzan.httpfetch.HttpApiMethodWrapper;
import com.github.youzan.httpfetch.HttpApiRequestParam;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 集成Fastjson 的JSONPath，直接通过表达式获取
 */
public class JsonPathConvertor implements ResponseGeneratorConvertor {

    @Override
    public boolean supports(Method method, HttpApiMethodWrapper wrapper, HttpApiRequestParam param, Class<?> responseCls) {
        //校验是否需要使用JSONPath的结果解析
        String path = wrapper.getJsonPath().value();
        if (CommonUtils.isStringEmpty(path)) {
            return false;
        }
        return true;
    }

    @Override
    public Object generate(Method method, HttpApiMethodWrapper wrapper, HttpApiRequestParam param, byte[] response, Class<?> responseCls) {

        String path = wrapper.getJsonPath().value();

        if (!CommonUtils.isStringEmpty(path)) {
            String httpResult = new String(response);
            //通过jsonpath 获取对应路径的值
            Object target = JSONPath.eval(JSON.parseObject(httpResult), path);
            if(target == null){
                return null;
            }
            //重新转换成string，并解析成指定结果
            String childrenString = CommonUtils.formatObjectToJSONString(target);
            return ByteConvertorUtil.byteToObj(method, wrapper, param, childrenString.getBytes(), responseCls);
        }

        return response;
    }

}
