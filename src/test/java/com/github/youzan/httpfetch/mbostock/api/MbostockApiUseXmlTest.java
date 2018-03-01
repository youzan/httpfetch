package com.github.youzan.httpfetch.mbostock.api;

import com.github.youzan.httpfetch.HttpApiConfiguration;
import com.github.youzan.httpfetch.HttpApiService;
import com.github.youzan.httpfetch.SourceReader;
import com.github.youzan.httpfetch.XmlReader;
import com.github.youzan.httpfetch.mbostock.vo.MiserablesVo;
import com.github.youzan.httpfetch.mbostock.vo.NodesVo;
import com.github.youzan.httpfetch.mbostock.vo.UsCongressResponseVo;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by daiqiang on 17/6/13.
 */
public class MbostockApiUseXmlTest {

    @Test
    public void test() {
        SourceReader xmlReader = new XmlReader(Arrays.asList("httpapi.xml"));

        HttpApiConfiguration configuration = new HttpApiConfiguration();
        configuration.setSourceReaders(Arrays.asList(xmlReader));

        HttpApiService service = new HttpApiService(configuration);
        service.init();

        MbostockApi mbostockApi = service.getOrCreateService(MbostockApi.class);

        String url = "https://bl.ocks.org/mbostock/raw/4600693/miserables.json";
        MiserablesVo responseVo = mbostockApi.miserables(url);
        System.out.println("links=="+responseVo.getLinks());
        System.out.println("links->size=="+responseVo.getLinks().size());
    }

    @Test
    public void test_no_xml() {
        HttpApiService service = new HttpApiService(new HttpApiConfiguration());
        service.init();

        MbostockApi mbostockApi = service.getOrCreateService(MbostockApi.class);

        String url = "https://bl.ocks.org/mbostock/raw/4600693/miserables.json";
        MiserablesVo responseVo = mbostockApi.miserables(url);
        System.out.println("links=="+responseVo.getLinks());
        System.out.println("links->size=="+responseVo.getLinks().size());
    }

    @Test
    public void test_json_path() {
        HttpApiService service = new HttpApiService(new HttpApiConfiguration());
        service.init();

        MbostockApi mbostockApi = service.getOrCreateService(MbostockApi.class);

        String url = "https://bl.ocks.org/mbostock/raw/4600693/miserables.json";
        MiserablesVo responseVo = mbostockApi.miserables(url);

        String nodesId = mbostockApi.miserablesToStringChildren(url);
        System.out.println(nodesId);
        assertEquals(responseVo.getNodes().get(0).getId(), nodesId);

        List<NodesVo> nodesVos = mbostockApi.miserablesToArrayChildren(url);
        assertEquals(responseVo.getNodes().get(0).getId(), nodesVos.get(0).getId());
    }

}
