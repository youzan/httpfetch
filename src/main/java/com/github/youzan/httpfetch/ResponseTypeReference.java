package com.github.youzan.httpfetch;

import com.alibaba.fastjson.TypeReference;

import java.lang.reflect.Type;

/**
 * Created by daiqiang on 17/6/28.
 */
public class ResponseTypeReference<T> extends TypeReference<T> {
    private Type type;

    public ResponseTypeReference(Type type){
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }
}
