package com.github.youzan.httpfetch.chains;

import com.alibaba.fastjson.JSON;
import com.github.youzan.httpfetch.*;
import com.github.youzan.httpfetch.*;
import com.github.youzan.httpfetch.resolver.ImageParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by daiqiang on 17/6/13.
 * 最后发起http请求
 */
public class ExecuteRequestChain implements HttpApiChain {

    private final static Logger LOGGER = LoggerFactory.getLogger(ExecuteRequestChain.class);

    @Override
    public HttpResult doChain(HttpApiInvoker invoker, Invocation invocation) {
        HttpApiMethodWrapper wrapper = invocation.getWrapper();
        HttpApiRequestParam requestParam = invocation.getRequestParam();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("调用开始,请求参数:" + JSON.toJSONString(requestParam) + "body:" + new String(requestParam.getRequestBody()));
        }
        try {
            return this.request(requestParam, wrapper);
        } catch (Exception e) {
            LOGGER.error("请求调用时发生异常! method [{}] requestParam [{}]", invocation.getMethod(), JSON.toJSONString(requestParam), e);
            HttpResult httpResult = new HttpResult();
            httpResult.setException(e);
            return httpResult;
        }
    }


    /**
     * @param requestParam
     * @return
     * @throws IOException
     */
    private HttpResult request(HttpApiRequestParam requestParam, HttpApiMethodWrapper wrapper) {
        StringBuffer url = new StringBuffer(requestParam.getUrl());
        String method = wrapper.getMethod();
        Map<String, String> getParam = requestParam.getGetParam();
        Map<String, String> postParam = requestParam.getPostParam();
        Map<String, Object> formParam = requestParam.getFormParam();
        byte[] body = requestParam.getRequestBody();
        Map<String, String> headers = requestParam.getHeaders();
        String encoding = requestParam.getEncoding();
        Integer timeout = wrapper.getTimeout();
        Integer readTimeout = wrapper.getReadTimeout();
        Integer retry = wrapper.getRetry();
        try {
            if (formParam == null || formParam.isEmpty()) {
                //没有文件上传的form
                //作为url后缀
                String postParamUrl = convertMap2UrlParam(postParam, encoding, false);
                if (body != null || !"POST".equals(method)) {
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
            } else {
                formParam.putAll(postParam);

                //如果需要则写道body流中
                long r = (long) (Math.random() * 1000000L);
                String boundary = "---------------------------7d" + r;
                headers.put("Content-Type", "multipart/form-data; boundary=" + boundary + "; charset=" + encoding);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                writeWithFormParams(formParam, boundary, encoding, baos);

                body = baos.toByteArray();
            }
        } catch (Exception e) {
            LOGGER.error("发起POST请求时出错! url [{}]", url, e);
            throw new RuntimeException("发起请求时出错!", e);
        }

        HttpResult httpResult = null;
        while(retry>=0) {
            try {
                httpResult = request(url, method, getParam, body, headers, encoding, timeout, readTimeout, retry);
            } catch (SocketTimeoutException | ConnectException  e) {
                if (retry > 0) {
                    LOGGER.info("超时重试:" + e.toString());
                }
            }

            retry--;
        }
        return httpResult;
    }

    /**
     * @param url         地址
     * @param method      GET、POST、DELETE、INPUT等http提供的功能
     * @param getParam    参数,始终做get参数传递
     * @param body        输出流的字节
     * @param headers     头
     * @param timeout     超时时间
     * @param readTimeout 读取超时时间
     * @return
     */
    private HttpResult request(StringBuffer url, String method, Map<String, String> getParam,
                               byte[] body, Map<String, String> headers, String encoding,
                               Integer timeout, Integer readTimeout, Integer retry) throws SocketTimeoutException, ConnectException{


        if (CommonUtils.isStringEmpty(url)) {
            throw new IllegalArgumentException("参数url为空!");
        }
        if (!CommonUtils.isInLimit(method,
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
                conn.setRequestMethod(method);
            } catch (ProtocolException e) {
                String msg = String.format("请求设置为POST方法时出错! url [%s] paramUrl [%s]", url, paramUrl);
                LOGGER.error(msg, e);
                throw new RuntimeException(msg, e);
            }

            if (headers != null && !headers.isEmpty()) {
                Iterator<Map.Entry<String, String>> entryIterator = headers.entrySet().iterator();
                while (entryIterator.hasNext()) {
                    Map.Entry<String, String> entry = entryIterator.next();
                    conn.addRequestProperty(entry.getKey(), entry.getValue());
                }
                if (LOGGER.isDebugEnabled()) {
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

            if (!CommonUtils.isArrayEmpty(body)) {
                //如果需要则写道body流中
                conn.setDoOutput(true);
                os = conn.getOutputStream();
                os.write(body);
                os.flush();
                os.close();
            }

            long time = System.currentTimeMillis();

            int resCode = conn.getResponseCode();

            if (resCode == 200) {
                is = conn.getInputStream();
            } else {
                is = conn.getErrorStream();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int len;
            while ((len = is.read(b)) != -1) {
                baos.write(b, 0, len);
            }
            is.close();
            HttpResult result = new HttpResult();
            result.setStatusCode(conn.getResponseCode());
            result.setData(baos.toByteArray());
            LOGGER.info("调用结果!,url [{}] rt[{}] result [{}]",
                    url, System.currentTimeMillis() - time, baos.toString());

            return result;
        } catch (java.net.SocketTimeoutException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("发起请求时出错! url [{}]", url, e);
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

    private String convertMap2UrlParam(Map<String, String> params, String encoding, boolean needEncode) throws UnsupportedEncodingException {
        if (params != null && !params.isEmpty()) {
            StringBuffer paramUrl = new StringBuffer();
            Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> e = it.next();
                if (e.getValue() != null) {
                    paramUrl.append("&");
                    paramUrl.append(e.getKey());
                    paramUrl.append("=");
                    String value = needEncode ? URLEncoder.encode(e.getValue(), encoding) : e.getValue();
                    paramUrl.append(value);
                }
            }
            if (paramUrl.length() > 0) {
                paramUrl.deleteCharAt(0);
            }
            return paramUrl.toString();
        }
        return "";
    }

    private void writeWithFormParams(Map<String, Object> formParam, String boundary, String encoding, ByteArrayOutputStream os) throws IOException {
        Iterator<Map.Entry<String, Object>> iterator = formParam.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String name = entry.getKey();
            Object value = entry.getValue();
            if (!CommonUtils.isStringEmpty(name)) {
                if (value instanceof File) {
                    File file = (File) value;
                    writeBytes("--" + boundary + "\r\n", encoding, os);
                    writeBytes("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + URLEncoder.encode(file.getName(), encoding) + "\"\r\n", encoding, os);
                    writeBytes("Content-Type: application/octet-stream\r\n", encoding, os);
                    writeBytes("\r\n", encoding, os);
                    writeBytes(file, os);
                    writeBytes("\r\n", encoding, os);
                } else if (value instanceof URL) {
                    URL url = (URL) value;
                    writeBytes("--" + boundary + "\r\n", encoding, os);
                    String fileName;
                    if (url.getFile().lastIndexOf("/") + 1 < url.getFile().length()) {
                        fileName = url.getFile().substring(url.getFile().lastIndexOf("/") + 1);
                    } else {
                        fileName = "";
                    }
                    writeBytes("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + URLEncoder.encode(fileName, encoding) + "\"\r\n", encoding, os);
                    writeBytes("Content-Type: application/octet-stream\r\n", encoding, os);
                    writeBytes("\r\n", encoding, os);
                    writeBytes(url, os);
                    writeBytes("\r\n", encoding, os);
                } else if (value instanceof ImageParam) {
                    ImageParam imageParam = (ImageParam) value;
                    if (imageParam.getImage() == null) {
                        throw new IllegalArgumentException("the parameter image is null");
                    }
                    writeBytes("--" + boundary + "\r\n", encoding, os);
                    String imageName = imageParam.getImageName();
                    writeBytes("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + URLEncoder.encode(imageName, encoding) + "\"\r\n", encoding, os);
                    writeBytes("Content-Type: application/octet-stream\r\n", encoding, os);
                    writeBytes("\r\n", encoding, os);
                    writeBytes(imageParam.getImage(), imageName, os);
                    writeBytes("\r\n", encoding, os);
                } else {
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

    private void writeBytes(BufferedImage image, String imageName, ByteArrayOutputStream os) throws IOException {
        String formatName = imageName.substring(imageName.lastIndexOf(".") + 1);
        ImageIO.write(image, formatName.toUpperCase(), os);
    }

    private void writeBytes(String content, String encoding, ByteArrayOutputStream os) throws IOException {
        os.write(content.getBytes(encoding));
    }

    private void writeBytes(File content, ByteArrayOutputStream os) {
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
                    String msg = "文件流关闭失败! fileName [" + content.getName() + "]";
                    throw new RuntimeException(msg);
                }
            }
        }
    }

    private void writeBytes(URL url, ByteArrayOutputStream os) {
        InputStream is = null;
        try {
            URLConnection urlConnection = url.openConnection();
            urlConnection.setDoInput(true);
            is = urlConnection.getInputStream();
            int len;
            byte[] b = new byte[1024];
            while ((len = is.read(b)) != -1) {
                os.write(b, 0, len);
            }
        } catch (Exception e) {
            LOGGER.error("读取文件出错!", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    String msg = "文件流关闭失败! fileName [" + url.getFile() + "]";
                    throw new RuntimeException(msg);
                }
            }
        }
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

}
