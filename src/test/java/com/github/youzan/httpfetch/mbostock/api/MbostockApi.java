package com.github.youzan.httpfetch.mbostock.api;

import com.github.youzan.httpfetch.HttpApi;
import com.github.youzan.httpfetch.Header;
import com.github.youzan.httpfetch.mbostock.vo.MiserablesVo;
import com.github.youzan.httpfetch.mbostock.vo.NodesVo;
import com.github.youzan.httpfetch.mbostock.vo.UsCongressResponseVo;
import com.github.youzan.httpfetch.parse.json.JsonPath;
import com.github.youzan.httpfetch.resolver.URL;

import java.util.List;
import java.util.Map;

/**
 * Created by daiqiang on 17/3/14.
 */
public interface MbostockApi {

    @HttpApi(timeout = 1000, readTimeout = 10000, headers = {@Header(key="user-agent", value = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")})
    UsCongressResponseVo getUsCongress();

    @HttpApi(timeout = 1000, readTimeout = 10000, headers = {@Header(key="user-agent", value = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")})
    UsCongressResponseVo getUsCongress(@URL String url);

    @HttpApi(timeout = 1000, readTimeout = 2000, headers = {@Header(key="user-agent", value = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")})
    MiserablesVo miserables(@URL String url);

    @HttpApi(timeout = 1000, readTimeout = 2000, jsonPath = @JsonPath("$.nodes"),
            headers = {@Header(key="user-agent", value = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")})
    List<NodesVo> miserablesToArrayChildren(@URL String url);

    @HttpApi(timeout = 1000, readTimeout = 2000, jsonPath = @JsonPath("$.nodes[0].id"),
            headers = {@Header(key="user-agent", value = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")})
    String miserablesToStringChildren(@URL String url);

    @HttpApi(timeout = 1000, readTimeout = 2000,
            url = "https://bl.ocks.org/mbostock/raw/4600693/miserables.json",
            headers = {@Header(key="user-agent", value = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")})
    MiserablesVo miserables();

}
