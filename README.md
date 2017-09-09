<p align="center">
    <image src="http://onlz2qizd.bkt.clouddn.com/logo" width="115" height="100"></image>
</p>
<p align="center">让http请求的调用更优雅</p>


## 概述
当我们提到java调用http请求时，我们想到的是HttpClient或是内置的HttpUrlConnention。
然后会写下如下一串的代码访问http接口：
``` bash
        HttpClient client = new HttpClient();
        client.getHostConfiguration().setProxy("127.0.0.1", 8888);
        client.getHostConfiguration().setHost("bl.ocks.org", 80, "http");
        GetMethod getMethod = new GetMethod("/mbostock/raw/4090846/us-congress-113.json");
        client.executeMethod(getMethod);
        //打印服务器返回的状态
        System.out.println(getMethod.getStatusLine().getStatusCode());
        if(getMethod.getStatusLine().getStatusCode() == 200){
            //打印结果页面
            String response = new String(getMethod.getResponseBodyAsString().getBytes("8859_1"));

            //打印返回的信息
            System.out.println(response);
        }
        getMethod.releaseConnection();
 
```
 
 可是我们是不是有一种更优雅的方式呢？类似于MyBatis，通过一定的配置，然后在调用的时候只需要执行一个接口便可以完成上述代码的工作。
 
 这边是HttpFetch的初衷，让http的调用更优雅。
 

## 下载
``` bash
git clone https://github.com/wumoyu850921/httpfetch.git
```

## 对象
* ParameterResolver：api参数解析类，自带的可以对数组、bean、简单类型等参数进行解析并封装成Get、Post、Form等类型请求的参数。也可以通过Url注解灵活定义api接口的请求地址。
* Convertor：返回数据封装类，自带的仅支持简单类型和JSON类型的数据进行封装。通过扩展可以实现更多的转换方式。
* Chain： 责任链模式，一层层对请求进行加工和处理。里面比较重要的是ParameterResolverChain、GenerateResponseChain和ExecuteRequestChain。ParameterResolverChain负责对参数进行处理，GenerateResponseChain负责对返回结果进行处理，ExecuteRequestChain负责最后的请求发送。
* ResourceReader: 配置信息读取类，负责对各组件单元的读取并最终传给HttpApiConfiguration类。
* HttpApiConfiguration： 负责对ResourceReader读取后的配置信息进行封装，然后将配置信息传给HttpApiService类。
* HttpApiService： 负责最后的代理类生成和缓存；

## 框架
* 初始化过程
   
   初始化过程可以选择spring和xml两种。spring的方式直接将生成的代理类注册到BeanDefinitionRegistry（可见HttpApiClassPathBeanDefinitionScanner源码），xml方式可以在没有spring组件的情况下独立运行（见单测MbostockApiUseXmlTest）。两种方式都可以完成Chain、ParamterResolver和Convertor注册。
![初始化过程](http://onlz2qizd.bkt.clouddn.com/http-fetch2.jpeg)

* 请求处理流程
   请求者发起请求时，会通过配置的各个Chain单元，一步一步的处理和封装参数并发送最终的Http请求，最后将返回的值进行封装。
![请求处理流程](http://onlz2qizd.bkt.clouddn.com/http-fetch.jpeg?a=4#1)

## 使用

### Maven
``` xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>VERSION_CODE</version>
</dependency>
```

### 非spring调用
1.创建http-api.xml配置文件：
``` xml
<?xml version="1.0" encoding="UTF-8"?>
<setting>

    <!-- 请求处理链 -->
    <chains>
    </chains>

    <!-- 参数处理类 -->
    <argumentResolvers>
    </argumentResolvers>

    <!-- 结果处理类 -->
    <resultConvertors>
    </resultConvertors>

    <!-- api和url映射关系 -->
    <aliases>
        <alias key="mbostockApi.getUsCongress" value="https://bl.ocks.org/mbostock/raw/4090846/us-congress-113.json" />
    </aliases>

</setting>
``` 
2.编写MbostockApi接口类：
``` java
package com.github.nezha.httpfetch.mbostock.api;

import com.github.nezha.httpfetch.HttpApi;
import com.github.nezha.httpfetch.Header;
import com.github.nezha.httpfetch.mbostock.vo.UsCongressResponseVo;

/**
 * Created by daiqiang on 17/3/14.
 */
public interface MbostockApi {

    @HttpApi(timeout = 1000, headers = {@Header(key="user-agent", value = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")})
    UsCongressResponseVo getUsCongress();

}
``` 

3.编写测试类：
``` java
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
```

以上就是非spring方式的调用

### spring方式的调用
1.创建application-httpapi.xml文件：
``` xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	   xsi:schemaLocation="
		    http://www.springframework.org/schema/beans
		    http://www.springframework.org/schema/beans/spring-beans.xsd
	    ">

	<bean id="springReader" class="com.github.nezha.httpfetch.spring.SpringReader" >
	    <!-- 请求处理链 -->
		<property name="chains" >
			<list>
				<bean class="com.github.nezha.httpfetch.bookworm.chains.BookWormTokenChain" />
			</list>
		</property>
		<!-- 参数处理类 -->
		<property name="parameterResolvers">
			<list>
			</list>
		</property>
		<!-- 结果处理类 -->
		<property name="convertors">
			<list>
			</list>
		</property>
		<!-- api和url映射关系 -->
		<property name="urlAlias">
			<map>
				<entry key="mbostockApi.getUsCongress" value="${mock.host}/mbostock/raw/4090846/us-congress-113.json" />
			</map>
		</property>
	</bean>

	<bean id="httpApiConfiguration" class="com.github.nezha.httpfetch.HttpApiConfiguration" init-method="init">
		<property name="sourceReaders">
			<list>
				<ref bean="springReader" />
			</list>
		</property>
	</bean>

	<bean id="httpApiService" class="com.github.nezha.httpfetch.HttpApiService" init-method="init">
		<constructor-arg index="0" ref="httpApiConfiguration" />
	</bean>
    
    <!-- http api代理注册 -->
	<bean class="com.github.nezha.httpfetch.spring.HttpApiScannerConfigurer">
		<property name="basePackage" value="com.github.nezha.httpfetch.bookworm.api,com.github.nezha.httpfetch.mbostock.api,com.github.nezha.httpfetch.youzan.api" />
	</bean>

</beans>
```
2.编写MbostockApi接口类：
``` java
package com.github.nezha.httpfetch.mbostock.api;

import com.github.nezha.httpfetch.HttpApi;
import com.github.nezha.httpfetch.Header;
import com.github.nezha.httpfetch.mbostock.vo.UsCongressResponseVo;

/**
 * Created by daiqiang on 17/3/14.
 */
public interface MbostockApi {

    @HttpApi(timeout = 1000, headers = {@Header(key="user-agent", value = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")})
    UsCongressResponseVo getUsCongress();

}
``` 
3.编写测试类：
``` java
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
``` 

### URL映射
url的映射使用了三种方式：

1.使用xml进行配置：
``` xml
    <aliases>
        <alias key="mbostockApi.getUsCongress" value="https://bl.ocks.org/mbostock/raw/4090846/us-congress-113.json" />
    </aliases>
```
2.使用注解方式：
``` java
package com.github.nezha.httpfetch.bookworm.api;

import com.github.nezha.httpfetch.Header;
import com.github.nezha.httpfetch.HttpApi;
import com.github.nezha.httpfetch.resolver.RequestBody;

import java.util.Map;

/**
 * Created by daiqiang on 17/6/16.
 */
public interface AlarmJobApi {

    @HttpApi(method = "POST", headers = @Header(key = "Content-type", value = "application/json"), timeout = 2000, url = "http://alert.s.qima-inc.com/api/v1/alert")
    String alert(@RequestBody Map<String, Object> param);

}
```
3.使用参数方式传入：
``` java
    @HttpApi(timeout = 1000, headers = {@Header(key="user-agent", value = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")})
    UsCongressResponseVo getUsCongress(@URL String url);
```
测试类：
``` java
    public void test_url_param(){
        String url = "https://bl.ocks.org/mbostock/raw/4090846/us-congress-113.json";
        UsCongressResponseVo responseVo = mbostockApi.getUsCongress(url);
        System.out.println("type=="+responseVo.getType());
        System.out.println("arcs->size=="+responseVo.getArcs().size());
        System.out.println("objects->districts->bbox->size=="+responseVo.getObjects().getDistricts().getBbox().size());
        System.out.println("objects->districts->type=="+responseVo.getObjects().getDistricts().getType());
        System.out.println("objects->districts->geometries->size=="+responseVo.getObjects().getDistricts().getGeometries().size());
        System.out.println("transform->scale=="+responseVo.getTransform().getScale());
        System.out.println("transform->translate=="+responseVo.getTransform().getTranslate());
    }
```

### 参数封装
1.Get请求参数：
使用QueryParam注解标记，并填写参数的名称
``` java
    @HttpApi(timeout = 2000, url = "http://bookworm365.com/uploadImage")
    @BookWormApi
    UploadFileResponseVo uploadFile(@QueryParam("name") String name,
                                    @QueryParam("n_value") String nValue);
```

2.Post请求参数：
使用PostParam注解标记，并填写参数的名称
``` java
Map audit(@PostParam("advertisementId") Integer advertisementId);
```

3.Form请求参数：
使用FormParam注解标记，并填写参数的名称
``` java
    @HttpApi(timeout = 2000, url = "http://bookworm365.com/uploadImage")
    @BookWormApi
    UploadFileResponseVo uploadFile(@FormParam("file") File file,
                                    @QueryParam("name") String name,
                                    @QueryParam("n_value") String nValue);
``` 

4.BeanParam注解使用：
当我们传递一个bean做为参数，但是希望对这个bean进行解析然后作为http请求参数时，我们可以使用BeanParam注解。
``` java
    @HttpApi(timeout = 2000, url = "http://bookworm365.com/uploadImage")
    @BookWormApi
    UploadFileResponseVo uploadFile(@BeanParam @QueryParam UploadFileRequestVo requestVo);
``` 
``` java
package com.github.nezha.httpfetch.bookworm.vo;

import com.alibaba.fastjson.annotation.JSONField;
import java.io.File;

public class UploadFileRequestVo {
    @JSONField(name = "file")
    private File file;
    private String name;
    @JSONField(name="n_value")
    private String nValue;
    public File getFile() {return file;}
    public void setFile(File file) {this.file = file;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getnValue() {return nValue;}
    public void setnValue(String nValue) {this.nValue = nValue;}
}
```
http的请求最终为：http://bookworm365.com/uploadImage?file=XXX&name=XXX&n_value=XXX

5.RequestBody注解使用：
当你需要传递消息体给服务器是，可以通过该注解。
例如我们想要传递一个application\json的请求：
``` java
    @HttpApi(method = "POST",timeout = 2000,headers = {@Header(key = "Content-type", value = "application/json;charset=UTF-8")})
    @WechatApi
    WechatBaseResponseVo<AddCustomAudiencesResponseVo> add(@RequestBody AddCustomAudiencesRequestVo requestVo);
```

### 结果封装
结果封装默认支持简单类型和JSON两种：
1.简单类型，如果返回值是String、int、long等，api的返回对象可以直接指定对应类：
``` java
    @HttpApi(timeout = 2000, url = "http://bookworm365.com/checkHeader")
    @BookWormApi
    String checkHeader();
```
2.JSON，如果返回值是一个json字符串，可以直接编写对应的bean作为返回类，内部使用fastjson进行反序列化：
``` java
    @HttpApi(timeout = 1000, headers = {@Header(key="user-agent", value = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")})
    UsCongressResponseVo getUsCongress();
```

``` java
package com.github.nezha.httpfetch.mbostock.vo;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.List;

public class UsCongressResponseVo {
    @JSONField(name="type")
    private String type;
    @JSONField(name="objects")
    private ObjectsVo objects;
    @JSONField(name="arcs")
    private List<List<List<Integer>>> arcs;
    @JSONField(name="transform")
    private TransformVo transform;
    public String getType() {return type;}
    public void setType(String type) {this.type = type;}
    public ObjectsVo getObjects() {return objects;}
    public void setObjects(ObjectsVo objects) {this.objects = objects;}
    public List<List<List<Integer>>> getArcs() {return arcs;}
    public void setArcs(List<List<List<Integer>>> arcs) {this.arcs = arcs;}
    public TransformVo getTransform() {return transform;}
    public void setTransform(TransformVo transform) {this.transform = transform;}
}
```

``` java
package com.github.nezha.httpfetch.mbostock.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.github.nezha.httpfetch.BaseTest;

public class ObjectsVo {
    @JSONField(name="districts")
    private DistrictsVo districts;
    public DistrictsVo getDistricts() {return districts;}
    public void setDistricts(DistrictsVo districts) {this.districts = districts;}
}
```
另外返回的类还支持泛型。
``` java
@HttpApi(timeout = 1000, headers = {@Header(key="user-agent", value = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")})
    UsCongressResponseVo<TransformVo> getUsCongress();
```

更多示例可以在项目的`test`目录中查看

## 开源协议
本项目基于 [MIT](https://zh.wikipedia.org/wiki/MIT%E8%A8%B1%E5%8F%AF%E8%AD%89)协议，请自由地享受和参与开源。

## 贡献

如果你有好的意见或建议，欢迎给我们提 [issue] 或 [PR]，为优化 [http-fetch] 贡献力量
