package com.github.nezha.httpfetch.mbostock.api;

import com.alibaba.fastjson.JSON;
import com.github.nezha.httpfetch.BaseTest;
import com.github.nezha.httpfetch.mbostock.vo.UsCongressResponseVo;
import com.github.nezha.httpfetch.spring.HttpApiBean;
import org.junit.Test;

/**
 * Created by daiqiang on 17/6/8.
 */
public class MbostockApiTest extends BaseTest {

    @HttpApiBean
    private MbostockApi mbostockApi;

    @Test
    public void test(){
        UsCongressResponseVo responseVo = mbostockApi.getUsCongress();
        System.out.println(responseVo.getType());
        System.out.println(responseVo.getArcs().size());
        System.out.println(responseVo.getObjects().getDistricts().getBbox().size());
        System.out.println(responseVo.getObjects().getDistricts().getType());
        System.out.println(responseVo.getObjects().getDistricts().getGeometries().size());
        System.out.println(responseVo.getTransform().getScale());
        System.out.println(responseVo.getTransform().getTranslate());
    }

}
