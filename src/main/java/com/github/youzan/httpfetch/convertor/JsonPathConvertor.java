package com.github.youzan.httpfetch.convertor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.youzan.httpfetch.ByteConvertorUtil;
import com.github.youzan.httpfetch.HttpApiMethodWrapper;
import com.github.youzan.httpfetch.HttpApiRequestParam;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;


public class JsonPathConvertor implements ResponseGeneratorConvertor {
    @Override
    public boolean supports(Method method, HttpApiMethodWrapper wrapper, HttpApiRequestParam param, Class<?> responseCls) {
        String path = wrapper.getJsonPath().path();
        if (StringUtils.hasText(path)) {
            return true;
        }
        return false;
    }

    @Override
    public Object generate(Method method, HttpApiMethodWrapper wrapper, HttpApiRequestParam param, byte[] response, Class<?> responseCls) {

        String path = wrapper.getJsonPath().path();

        if (StringUtils.hasText(path)) {
            String httpResult = new String(response);
            Object target = parseThenGetTarget(httpResult, path);
            return ByteConvertorUtil.byteToObj(method, wrapper, param, JSON.toJSONBytes(target), responseCls);
        }

        return response;
    }

    private Object parseThenGetTarget(String httpResult, String path) {

        JSONObject rstJsonObject = JSON.parseObject(httpResult);
        JsonPathConvertor.Path pathObj = new JsonPathConvertor.Path(path);
        return getTarget(rstJsonObject, pathObj);

    }

    private Object getTarget(Object data, JsonPathConvertor.Path path) {

        String currentKey = path.getCurrentKey();

        if (!path.hasNext()) {
            return data;
        } else {
            if (data instanceof JSONArray) {
                // 如果是jsonArray，取第一个子节点，并且path的指针不移动，以便于递归时取jsonArray第一个子节点的key对应的值
                JSONArray rstArray = (JSONArray) data;
                return getTarget(rstArray.get(0), path);
            } else {
                // 如果是jsonObject，指针往后移
                path.moveToNext();
                JSONObject rst = (JSONObject) data;
                return getTarget(rst.get(currentKey), path);
            }

        }

    }


    public static final class Path {

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        private String path;

        public String getCurrentKey() {
            return currentKey;
        }

        public void setCurrentKey(String currentKey) {
            this.currentKey = currentKey;
        }

        public Path(String originPath) {
            this.path = originPath;
            this.moveToNext();
        }

        private String currentKey;

        public void moveToNext() {

            int index = path.indexOf(".");
            if (index > 0) {
                this.currentKey = path.substring(0, index);
                this.path = path.substring(index + 1);
            } else {
                this.currentKey = this.path;
                this.path = "";
            }

        }

        public boolean hasNext() {
            // 当前currentKey与path相等，表明已经没有后续需要取的子节点
            if (this.path.equals(this.currentKey)) {
                return false;
            } else {
                return true;
            }
        }

    }
}
