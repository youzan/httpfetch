package com.github.nezha.httpfetch.chains;

import com.github.nezha.httpfetch.*;
import org.springframework.core.Ordered;

/**
 * Created by daiqiang on 16/12/8.
 */
public interface HttpApiChain extends Ordered {

    /**
     * 调用链 调用
     */
    HttpResult doChain(HttpApiInvoker invoker, Invocation invocation);

}
