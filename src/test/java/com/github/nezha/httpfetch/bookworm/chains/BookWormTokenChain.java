package com.github.nezha.httpfetch.bookworm.chains;

import com.github.nezha.httpfetch.HttpApiInvoker;
import com.github.nezha.httpfetch.HttpApiRequestParam;
import com.github.nezha.httpfetch.HttpResult;
import com.github.nezha.httpfetch.Invocation;
import com.github.nezha.httpfetch.chains.HttpApiChain;

/**
 * Created by daiqiang on 17/6/14.
 */
public class BookWormTokenChain implements HttpApiChain {
    @Override
    public HttpResult doChain(HttpApiInvoker invoker, Invocation invocation) {
        if(invocation.getWrapper().hasAnnotation(BookWormApi.class)){
            //填充token
            HttpApiRequestParam requestParam = invocation.getRequestParam();
            requestParam.addHeaders("Cookie", "token=XXXXXXX");
        }
        return invoker.invoke(invocation);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
