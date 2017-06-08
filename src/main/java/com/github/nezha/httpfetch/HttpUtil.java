package com.github.nezha.httpfetch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by xiaowa on 11/4/16.
 */
public class HttpUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);

    public static byte[] get(String url, Map<String, String> getParam, Map<String, String> headers, Integer timeOut, Integer readTimeout) {
        return httpRequest(new StringBuffer(url), "GET", getParam, null, headers, "UTF-8", timeOut, readTimeout);
    }

    public static byte[] post(String url, Map<String, String> getParam, Map<String, String> postParam, Map<String, Object> formParam,
                              byte[] body, Map<String, String> headers, String encoding,
                              Integer timeOut, Integer readTimeout) {
        return httpRequest(new StringBuffer(url), "POST", getParam, postParam, formParam, body, headers, encoding, timeOut, readTimeout);
    }

    /**
     *
     * @param url
     * @param requestType
     * @param getParam
     * @param postParam   参数,如果有body则作为url后缀传递,如果没有body作为body传递
     * @param formParam
     * @param body
     * @param headers
     * @param encoding
     * @param timeout
     * @param readTimeout
     * @return
     * @throws IOException
     */
    public static byte[] httpRequest(StringBuffer url, String requestType, Map<String, String> getParam, Map<String, String> postParam, Map<String, Object> formParam,
                                     byte[] body, Map<String, String> headers, String encoding,
                                     Integer timeout, Integer readTimeout) {
        try{
            if (formParam == null || formParam.isEmpty()) {
                //没有文件上传的form
                //作为url后缀
                String postParamUrl = convertMap2UrlParam(postParam, encoding, false);
                if (body != null || !"POST".equals(requestType)) {
                    //需要向输出流写所以
                    //param做为url后缀传递
                    if (url.indexOf("?") == -1) {
                        url.append("?");
                    }
                    url.append(postParamUrl);
                } else {
                    //将参数作为二进制流传递
                    body = postParamUrl.getBytes();
                }
            }else{
                formParam.putAll(postParam);

                //如果需要则写道body流中
                long r = (long) (Math.random() * 1000000L);
                String boundary = "---------------------------7d" + r;
                headers.put("Content-Type", "multipart/form-data; boundary=" + boundary + "; charset=" + encoding);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                writeWithFormParams(formParam, boundary, encoding, baos);

                body = baos.toByteArray();
            }
        }catch (Exception e){
            LOGGER.error("发起POST请求时出错!", "url", url);
            throw new RuntimeException("发起请求时出错!", e);
        }

        return httpRequest(url, requestType, getParam, body, headers, encoding, timeout, readTimeout);
    }
    /**
     * @param url         地址
     * @param requestType GET、POST、DELETE、INPUT等http提供的功能
     * @param getParam    参数,始终做get参数传递
     * @param body        输出流的字节
     * @param headers     头
     * @param timeout     超时时间
     * @param readTimeout 读取超时时间
     * @return
     */
    private static byte[] httpRequest(StringBuffer url, String requestType, Map<String, String> getParam,
                                      byte[] body, Map<String, String> headers, String encoding,
                                      Integer timeout, Integer readTimeout) {


        if (StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException("参数url为空!");
        }
        if (!CommonUtils.isInLimit(requestType,
                "GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE")) {
            throw new IllegalArgumentException("参数requestType有误!");
        }


        InputStream is = null;
        OutputStream os = null;
        try {
            //作为url后缀
            String paramUrl = convertMap2UrlParam(getParam, encoding, true);
            if (url.indexOf("?") == -1) {
                url.append("?");
            }
            url.append(paramUrl);

            HttpURLConnection conn = (HttpURLConnection) (new URL(url.toString()).openConnection());

            try {
                // 可以根据需要 提交 GET、POST、DELETE、INPUT等http提供的功能
                conn.setRequestMethod(requestType);
            } catch (ProtocolException e) {
                String msg = String.format("请求设置为POST方法时出错! url [%s] paramUrl [%s]", url, paramUrl);
                LOGGER.error(msg, e);
                throw new RuntimeException(msg, e);
            }

            if (headers != null && !headers.isEmpty()) {
                Iterator<Map.Entry<String, String>> entryIterator = headers.entrySet().iterator();
                while(entryIterator.hasNext()){
                    Map.Entry<String, String> entry = entryIterator.next();
                    conn.addRequestProperty(entry.getKey(), entry.getValue());
                }
                if(LOGGER.isDebugEnabled()){
                    LOGGER.debug("request http! header [{}]", JSON.toJSONString(conn.getRequestProperties()));
                }
            }

            if (readTimeout != null) {
                conn.setReadTimeout(readTimeout);
            }
            if (timeout != null) {
                conn.setConnectTimeout(timeout);
            }

            conn.setDoInput(true);

            if (ArrayUtils.isNotEmpty(body)) {
                //如果需要则写道body流中
                conn.setDoOutput(true);

                os = conn.getOutputStream();
                os.write(body);
                os.close();
            }

            long time = System.currentTimeMillis();
            if (conn.getResponseCode() == 200) {
                //成功 取stream
                is = conn.getInputStream();
            } else {
                is = conn.getErrorStream();
            }
            if(LOGGER.isInfoEnabled()){
                LOGGER.info("调用结束!", "url", url, "rt", System.currentTimeMillis()-time);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int len;
            while ((len = is.read(b)) != -1) {
                baos.write(b, 0, len);
            }
            is.close();
            return baos.toByteArray();
        } catch (Exception e) {
            LOGGER.error("发起请求时出错!", "url", url);
            throw new RuntimeException("发起请求时出错!", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LOGGER.error("关闭输入流时出错!", e);
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    LOGGER.error("关闭输出流时出错!", e);
                }
            }
        }
    }

    private static String convertMap2UrlParam(Map<String, String> params, String encoding, boolean needEncode) throws UnsupportedEncodingException {
        if (params != null && !params.isEmpty()) {
            StringBuffer paramUrl = new StringBuffer();
            Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> e = it.next();
                if (StringUtils.isNotBlank(e.getValue())) {
                    paramUrl.append("&");
                    paramUrl.append(e.getKey());
                    paramUrl.append("=");
                    String value = needEncode ? URLEncoder.encode(e.getValue(), encoding) : e.getValue();
                    paramUrl.append(value);
                }
            }
            if(paramUrl.length() > 0){
                paramUrl.deleteCharAt(0);
            }
            return paramUrl.toString();
        }
        return "";
    }

    private static void writeWithFormParams( Map<String, Object> formParam, String boundary, String encoding, ByteArrayOutputStream os) throws IOException {
        Iterator<Map.Entry<String, Object>> iterator = formParam.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String name = entry.getKey();
            Object value = entry.getValue();
            if (StringUtils.isNotEmpty(name)) {
                if(value instanceof File){
                    File file = (File)value;
                    writeBytes("--" + boundary + "\r\n", encoding, os);
                    writeBytes("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + URLEncoder.encode(file.getName(), encoding) + "\"\r\n", encoding, os);
                    writeBytes("Content-Type: application/octet-stream\r\n", encoding, os);
                    writeBytes("\r\n", encoding, os);
                    writeBytes(file, os);
                    writeBytes("\r\n", encoding, os);
                }else{
                    writeBytes("--" + boundary + "\r\n", encoding, os);
                    writeBytes("Content-Disposition: form-data; name=\"" + name + "\"\r\n", encoding, os);
                    writeBytes("Content-Type: text/plain; charset=" + encoding + "\r\n", encoding, os);
                    writeBytes("\r\n", encoding, os);
                    writeBytes(String.valueOf(value) + "\r\n", encoding, os);
                }
            }
        }
        writeBytes("--" + boundary + "--\r\n", encoding, os);
    }

    private static void writeBytes(String content, String encoding, ByteArrayOutputStream os) throws IOException {
        os.write(content.getBytes(encoding));
    }

    private static void writeBytes(File content, ByteArrayOutputStream os) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(content);
            int len;
            byte[] b = new byte[1024];
            while ((len = fis.read(b)) != -1) {
                os.write(b, 0, len);
            }
        } catch (Exception e) {
            LOGGER.error("读取文件出错!", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    String msg = "文件流关闭失败! fileName ["+content.getName()+"]";
                    throw new RuntimeException(msg);
                }
            }
        }
    }

}
