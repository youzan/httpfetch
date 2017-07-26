package com.github.nezha.httpfetch.resolver;

import java.lang.annotation.*;

/**
 * Created by daiqiang on 17/6/16.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GetParam {
}
