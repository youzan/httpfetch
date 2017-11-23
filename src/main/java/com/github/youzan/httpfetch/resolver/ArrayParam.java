package com.github.youzan.httpfetch.resolver;

import java.lang.annotation.*;

/**
 * Created by daiqiang on 17/3/27.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ArrayParam {
}
