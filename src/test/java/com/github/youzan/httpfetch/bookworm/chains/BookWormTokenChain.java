package com.github.youzan.httpfetch.bookworm.chains;

import com.github.youzan.httpfetch.HttpApiInvoker;
import com.github.youzan.httpfetch.HttpApiRequestParam;
import com.github.youzan.httpfetch.HttpResult;
import com.github.youzan.httpfetch.Invocation;
import com.github.youzan.httpfetch.chains.HttpApiChain;

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
