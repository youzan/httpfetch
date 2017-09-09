package com.github.nezha.httpfetch.bookworm.api;

import com.alibaba.fastjson.JSON;
import com.github.nezha.httpfetch.BaseTest;
import com.github.nezha.httpfetch.bookworm.vo.UploadFileRequestVo;
import com.github.nezha.httpfetch.bookworm.vo.UploadFileResponseVo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.net.URL;

/**
 * Created by daiqiang on 17/6/14.
 */
public class BookWormHttpApiTest extends BaseTest {

    @Autowired
    BookWormHttpApi bookWormHttpApi;

    @Test
    public void testUploadFile() throws Exception {
        URL url = BookWormHttpApiTest.class.getClassLoader().getResource("httpapi.xml");
        File file = new File(url.toURI());
        UploadFileResponseVo responseVo = bookWormHttpApi.uploadFile(file, "name", "nValue");
        System.out.println(JSON.toJSONString(responseVo));

        responseVo = bookWormHttpApi.uploadFile(new URL("http://onlz2qizd.bkt.clouddn.com/800_800.png"), "name", "nValue");
        System.out.println(JSON.toJSONString(responseVo));
    }

    @Test
    public void testBeanParam() throws Exception {
        URL url = BookWormHttpApiTest.class.getClassLoader().getResource("httpapi.xml");
        File file = new File(url.toURI());

        UploadFileRequestVo requestVo = new UploadFileRequestVo();
        requestVo.setFile(file);
        requestVo.setName("name");
        requestVo.setnValue("nValue");

        UploadFileResponseVo responseVo = bookWormHttpApi.uploadFile(requestVo);
        System.out.println(JSON.toJSONString(responseVo));
    }

    @Test
    public void testCheckHeader() throws Exception {
        System.out.println(bookWormHttpApi.checkHeader());
    }

}