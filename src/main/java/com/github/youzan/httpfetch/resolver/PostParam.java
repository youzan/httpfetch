package com.github.youzan.httpfetch.resolver;

import java.lang.annotation.*;

/**
 * Created by daiqiang on 17/5/25.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PostParam {

    String value() default "";

}
