package com.github.youzan.httpfetch.mbostock.api;

import com.github.youzan.httpfetch.HttpApi;
import com.github.youzan.httpfetch.Header;
import com.github.youzan.httpfetch.mbostock.vo.UsCongressResponseVo;
import com.github.youzan.httpfetch.resolver.URL;

/**
 * Created by daiqiang on 17/3/14.
 */
public interface MbostockApi {

    @HttpApi(timeout = 1000, headers = {@Header(key="user-agent", value = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")})
    UsCongressResponseVo getUsCongress();

    @HttpApi(timeout = 1000, headers = {@Header(key="user-agent", value = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")})
    UsCongressResponseVo getUsCongress(@URL String url);

}
