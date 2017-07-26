package com.github.nezha.httpfetch.mbostock.api;

import com.github.nezha.httpfetch.BaseTest;
import com.github.nezha.httpfetch.mbostock.vo.UsCongressResponseVo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by daiqiang on 17/6/8.
 */
public class MbostockApiTest extends BaseTest {

    @Autowired
    private MbostockApi mbostockApi;

    @Test
    public void test(){
        UsCongressResponseVo responseVo = mbostockApi.getUsCongress();
        System.out.println("type=="+responseVo.getType());
        System.out.println("arcs->size=="+responseVo.getArcs().size());
        System.out.println("objects->districts->bbox->size=="+responseVo.getObjects().getDistricts().getBbox().size());
        System.out.println("objects->districts->type=="+responseVo.getObjects().getDistricts().getType());
        System.out.println("objects->districts->geometries->size=="+responseVo.getObjects().getDistricts().getGeometries().size());
        System.out.println("transform->scale=="+responseVo.getTransform().getScale());
        System.out.println("transform->translate=="+responseVo.getTransform().getTranslate());
    }

}
