package com.github.nezha.httpfetch;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpApiParam {

	String value();
	
}
