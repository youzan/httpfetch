<p align="center">
    H
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
git clone https://github.com/youzan/zanui-weapp.git
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
![请求处理流程](http://onlz2qizd.bkt.clouddn.com/http-fetch.jpeg?a=3#1)

## 使用

1. 使用 [ZanUI-WeApp] 前请确保已经学习过微信官方的 [小程序简易教程] 和 [小程序框架介绍]。

更多示例可以在项目的`example`目录中查看

## 开源协议
本项目基于 [MIT](https://zh.wikipedia.org/wiki/MIT%E8%A8%B1%E5%8F%AF%E8%AD%89)协议，请自由地享受和参与开源。

## 贡献

如果你有好的意见或建议，欢迎给我们提 [issue] 或 [PR]，为优化 [http-fetch] 贡献力量
