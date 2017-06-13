package com.github.nezha.httpfetch.chains;

import java.lang.annotation.*;

/**
 * 被标记的参数，直接将对象属性转换成map
 * @author 11047530
 *
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BeanParam {
}