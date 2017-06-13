package com.github.nezha.httpfetch;

import com.github.nezha.httpfetch.convertor.ResponseGeneratorConvertor;
import com.github.nezha.httpfetch.chains.HttpApiChain;
import com.github.nezha.httpfetch.resolver.MethodParameterResolver;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by daiqiang on 16/12/6.
 */
public class XmlReader implements SourceReader {

    private final static Logger LOGGER = LoggerFactory.getLogger(XmlReader.class);

    private final static String ELEMENT_INTERCEPTORS = "interceptors";
    private final static String ELEMENT_INTERCEPTOR = "interceptor";

    private final static String ELEMENT_ARGUMENT_RESOLVERS = "argumentResolvers";
    private final static String ELEMENT_RESOLVER = "resolver";

    private final static String ELEMENT_RETURN_HANDLERS = "returnHandlers";
    private final static String ELEMENT_HANDLER = "handler";

    private final static String ELEMENT_ALIASES = "aliases";
    private final static String ELEMENT_ALIAS = "alias";
    private final static String ELEMENT_ALIAS_KEY = "key";
    private final static String ELEMENT_ALIAS_VALUE = "value";

    /**
     * 拦截器
     */
    private List<HttpApiChain> chains = new ArrayList<>();


    /**
     * 结果集处理类，如果需要不同的结果转换时，可以继承并注册
     */
    private List<ResponseGeneratorConvertor> handlers = new ArrayList<>();
    /**
     * 入参处理类，如果需要扩展参数的转换方式时可以继承并注册
     */
    private List<MethodParameterResolver> parameterResolvers = new ArrayList<>();

    private Map<String, String > urlAlias = new HashMap<>();

    private List<String> paths;

    public void init(){
        if(!CommonUtils.isCollectionEmpty(paths)){
            for(String path : paths){
                this.read(path);
            }
        }
    }

    private void read(String path){

        InputStream is;
        Document document;
        Element root =null;
        try {
            is = this.getClass().getClassLoader().getResourceAsStream(path);
            SAXReader saxReader = new SAXReader();
            document = saxReader.read(is);
            if (null!=document){
                root = document.getRootElement();
            }
        } catch (Exception e) {
            LOGGER.error("读取api框架配置信息出错! path [{}]", path, e);
        }

        if (null!=root){

            this.parseInterceptos(root);

            this.parseArgumentResolvers(root);

            this.parseReturnHandlers(root);

            this.parseUrlAlias(root);
        }

    }

    /**
     * 解析 参数处理类
     * @param root
     * @throws ClassNotFoundException 服务类查询不到
     * @throws IllegalAccessException 服务类初始化出错
     * @throws InstantiationException 服务类初始化出错
     */
    private void parseInterceptos(Element root) {
        try{
            Element resolversEl = root.element(ELEMENT_INTERCEPTORS);
            List<Element> resolverEl = resolversEl.elements(ELEMENT_INTERCEPTOR);
            if(resolverEl != null && resolverEl.size() > 0){
                for(Element e : resolverEl){
                    Class<HttpApiChain> cls = (Class<HttpApiChain>) Class.forName(e.getStringValue());
                    chains.add(cls.newInstance());
                }
            }
        }catch (Exception e){
            String msg = "解析请求拦截器集合时出错!";
            LOGGER.error(msg, e);
            throw new RuntimeException(msg);
        }
    }

    /**
     * 解析 参数处理类
     * @param root
     * @throws ClassNotFoundException 服务类查询不到
     * @throws IllegalAccessException 服务类初始化出错
     * @throws InstantiationException 服务类初始化出错
     */
    private void parseArgumentResolvers(Element root) {
        try{
            Element resolversEl = root.element(ELEMENT_ARGUMENT_RESOLVERS);
            List<Element> resolverEl = resolversEl.elements(ELEMENT_RESOLVER);
            if(resolverEl != null && resolverEl.size() > 0){
                for(Element e : resolverEl){
                    Class<MethodParameterResolver> cls = (Class<MethodParameterResolver>) Class.forName(e.getStringValue());
                    parameterResolvers.add(cls.newInstance());
                }
            }
        }catch (Exception e){
            String msg = "解析参数处理类集合时出错!";
            LOGGER.error(msg, e);
            throw new RuntimeException(msg);
        }
    }

    /**
     * 解析 回执处理类
     * @param root
     * @throws ClassNotFoundException 服务类查询不到
     * @throws IllegalAccessException 服务类初始化出错
     * @throws InstantiationException 服务类初始化出错
     */
    private void parseReturnHandlers(Element root) {
        try{
            Element returnHandlers = root.element(ELEMENT_RETURN_HANDLERS);
            List<Element> handlerEl = returnHandlers.elements(ELEMENT_HANDLER);
            if(handlerEl != null && handlerEl.size() > 0){
                for(Element e : handlerEl){
                    Class<ResponseGeneratorConvertor> cls = (Class<ResponseGeneratorConvertor>) Class.forName(e.getStringValue());
                    handlers.add(cls.newInstance());
                }
            }
        }catch (Exception e){
            String msg = "解析结果处理类集合时出错!";
            LOGGER.error(msg, e);
            throw new RuntimeException(msg);
        }
    }

    /**
     *
     * @param root
     */
    private void parseUrlAlias(Element root) {
        try{
            Element aliasesEl = root.element(ELEMENT_ALIASES);
            List<Element> aliasEl = aliasesEl.elements(ELEMENT_ALIAS);
            if(aliasEl != null && aliasEl.size() > 0){
                for(Element e : aliasEl){
                    String key = e.attributeValue(ELEMENT_ALIAS_KEY);
                    String value = e.attributeValue(ELEMENT_ALIAS_VALUE);
                    urlAlias.put(key, value);
                }
            }
        }catch (Exception e) {
            String msg = "解析url别名集合时出错!";
            LOGGER.error(msg, e);
            throw new RuntimeException(msg);
        }
    }

    @Override
    public List<HttpApiChain> getChains() {
        return chains;
    }

    @Override
    public List<ResponseGeneratorConvertor> getHandlers() {
        return handlers;
    }

    @Override
    public List<MethodParameterResolver> getParameterResolvers() {
        return parameterResolvers;
    }

    @Override
    public Map<String, String> getUrlAlias() {
        return urlAlias;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }
}
