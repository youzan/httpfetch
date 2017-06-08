package com.github.nezha.httpfetch.spring;

import java.lang.annotation.*;

/**
 * Created by daiqiang on 17/5/25.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpApiBean {
}
