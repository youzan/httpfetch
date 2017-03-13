package com.github.nezha.httpfetch.resolver;

import java.lang.annotation.*;

/**
 * 文件参数
 * Created by daiqiang on 16/12/21.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FileParam {

    String value();

}
