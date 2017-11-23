package com.github.youzan.httpfetch.chains;

import com.github.youzan.httpfetch.*;
import com.github.youzan.httpfetch.HttpApiInvoker;
import com.github.youzan.httpfetch.HttpResult;
import com.github.youzan.httpfetch.Invocation;
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
