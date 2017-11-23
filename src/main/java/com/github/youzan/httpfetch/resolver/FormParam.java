package com.github.youzan.httpfetch.resolver;

import java.lang.annotation.*;

/**
 * 文件参数
 * Created by daiqiang on 16/12/21.
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FormParam {

    String value() default "";

}
