package com.github.nezha.httpfetch;

import com.alibaba.fastjson.JSON;
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

    public static byte[] get(String url, Map<String, String> param, Map<String, String> headers, Integer timeOut, Integer readTimeout) {
        return httpRequest(url, "GET", param, null, null, headers, "UTF-8", timeOut, readTimeout);
    }

    public static byte[] post(String url, Map<String, String> param, Map<String, File> fileParam,
                              byte[] body, Map<String, String> headers, String encoding,
                              Integer timeOut, Integer readTimeout) {
        return httpRequest(url, "POST", param, fileParam,
                body, headers, encoding, timeOut, readTimeout);
    }

    /**
     * @param url         地址
     * @param requestType GET、POST、DELETE、INPUT等http提供的功能
     * @param param       参数,如果有body则作为url后缀传递,如果没有body作为body传递
     * @param body        输出流的字节
     * @param headers     头
     * @param timeOut     超时时间
     * @param readTimeout 读取超时时间
     * @return
     */
    public static byte[] httpRequest(String url, String requestType, Map<String, String> param, Map<String, File> fileParam,
                                     byte[] body, Map<String, String> headers, String encoding,
                                     Integer timeOut, Integer readTimeout) {


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

            StringBuffer paramUrl = new StringBuffer();

            if (fileParam == null || fileParam.isEmpty()) {
                //没有文件上传的form
                if (param != null && !param.isEmpty()) {
                    Iterator<Map.Entry<String, String>> it = param.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, String> e = it.next();
                        if (StringUtils.isNotBlank(e.getValue())) {
                            paramUrl.append("&" + e.getKey() + "=" + URLEncoder.encode(e.getValue(), encoding));
                        }
                    }
                }

                if (paramUrl.length() > 0) {
                    if (body != null || !"POST".equals(requestType)) {
                        //需要向输出流写所以
                        //param做为url后缀传递
                        if (url.indexOf("?") == -1) {
                            url += "?";
                        }
                        url += paramUrl.toString();
                    } else {
                        //将参数作为二进制流传递
                        body = paramUrl.toString().getBytes();
                    }
                }
            }

            HttpURLConnection conn = (HttpURLConnection) (new URL(url).openConnection());

            try {
                // 可以根据需要 提交 GET、POST、DELETE、INPUT等http提供的功能
                conn.setRequestMethod(requestType);
            } catch (ProtocolException e) {
                String msg = String.format("请求设置为POST方法时出错! url [%s] paramUrl [%s]", url, paramUrl);
                LOGGER.error(msg, e);
                throw new RuntimeException(msg, e);
            }

            if (headers != null && !headers.isEmpty()) {
                Iterator<Map.Entry<String, String>> it = headers.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> e = it.next();
                    conn.addRequestProperty(e.getKey(), e.getValue());
                }

                if(LOGGER.isDebugEnabled()){
                    LOGGER.debug("headers:::{}", JSON.toJSONString(conn.getRequestProperties()));
                }
            }

            if (readTimeout != null) {
                conn.setReadTimeout(readTimeout);
            }
            if (timeOut != null) {
                conn.setConnectTimeout(timeOut);
            }

            conn.setDoInput(true);

            if (fileParam != null && !fileParam.isEmpty()) {
                //如果需要则写道body流中
                long r = (long) (Math.random() * 1000000L);
                String boundary = "---------------------------7d" + r;
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary + "; charset=" + encoding);

                conn.setDoOutput(true);
                os = conn.getOutputStream();

                //采用文件上传方式写入流中
                writeWithFileParams(param, fileParam, boundary, encoding, os);
            } else if (ArrayUtils.isNotEmpty(body)) {
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
                LOGGER.info("请求::{} 耗时::{}!", url, System.currentTimeMillis()-time);
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
            LOGGER.error("发起POST请求时出错! url [{}]", url);
            throw new RuntimeException("发起POST请求时出错!", e);
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

    private static void writeWithFileParams(Map<String, String> stringParam, Map<String, File> fileParam, String boundary, String encoding, OutputStream os) throws Exception {
        Iterator<Map.Entry<String, String>> iterator = stringParam.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String name = entry.getKey();
            String value = entry.getValue();
            if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(value)) {
                writeBytes("--" + boundary + "\r\n", encoding, os);
                writeBytes("Content-Disposition: form-data; name=\"" + name + "\"\r\n", encoding, os);
                writeBytes("Content-Type: text/plain; charset=" + encoding + "\r\n", encoding, os);
                writeBytes("\r\n", encoding, os);
                writeBytes(URLEncoder.encode(value, encoding) + "\r\n", encoding, os);
            }
        }

        Iterator<Map.Entry<String, File>> fileIterator = fileParam.entrySet().iterator();
        while (fileIterator.hasNext()) {
            Map.Entry<String, File> entry = fileIterator.next();
            String name = entry.getKey();
            File value = entry.getValue();
            if (StringUtils.isNotEmpty(name) && value != null) {
                writeBytes("--" + boundary + "\r\n", encoding, os);
                writeBytes("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + URLEncoder.encode(value.getName(), encoding) + "\"\r\n", encoding, os);
                writeBytes("Content-Type: application/octet-stream\r\n", encoding, os);
                writeBytes("\r\n", encoding, os);
                writeBytes(value, os);
                writeBytes("\r\n", encoding, os);
            }
        }

        writeBytes("--" + boundary + "--\r\n", encoding, os);
    }

    private static void writeBytes(String content, String encoding, OutputStream os) throws IOException {
        os.write(content.getBytes(encoding));
    }

    private static void writeBytes(File content, OutputStream os) throws IOException {
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
                fis.close();
            }
        }
    }

}
