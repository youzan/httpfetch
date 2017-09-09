package com.github.nezha.httpfetch.mbostock.api;

import com.github.nezha.httpfetch.HttpApiConfiguration;
import com.github.nezha.httpfetch.HttpApiService;
import com.github.nezha.httpfetch.SourceReader;
import com.github.nezha.httpfetch.XmlReader;
import com.github.nezha.httpfetch.mbostock.vo.UsCongressResponseVo;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by daiqiang on 17/6/13.
 */
public class MbostockApiUseXmlTest {

    @Test
    public void test() {
        SourceReader xmlReader = new XmlReader(Arrays.asList("httpapi.xml"));

        HttpApiConfiguration configuration = new HttpApiConfiguration();
        configuration.setSourceReaders(Arrays.asList(xmlReader));
        configuration.init();

        HttpApiService service = new HttpApiService(configuration);
        service.init();

        MbostockApi mbostockApi = service.getOrCreateService(MbostockApi.class);

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
