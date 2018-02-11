package com.github.youzan.httpfetch.mbostock.api;

import com.github.youzan.httpfetch.BaseTest;
import com.github.youzan.httpfetch.mbostock.vo.MiserablesVo;
import com.github.youzan.httpfetch.mbostock.vo.UsCongressResponseVo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by daiqiang on 17/6/8.
 */
public class MbostockApiTest extends BaseTest {

    @Autowired
    private MbostockApi mbostockApi;

    @Test
    public void test(){
        MiserablesVo responseVo = mbostockApi.miserables();
        System.out.println("links=="+responseVo.getLinks());
        System.out.println("links->size=="+responseVo.getLinks().size());
    }

    @Test
    public void test_url_param(){
        String url = "https://bl.ocks.org/mbostock/raw/4600693/miserables.json";
        MiserablesVo responseVo = mbostockApi.miserables(url);
        System.out.println("links=="+responseVo.getLinks());
        System.out.println("links->size=="+responseVo.getLinks().size());
    }

}
