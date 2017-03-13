package com.github.nezha.httpfetch;

import java.lang.annotation.*;

/**
 * Created by daiqiang on 16/12/6.
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Header {

    String key();

    String value();

}
