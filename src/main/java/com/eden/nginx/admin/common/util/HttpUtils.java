package com.eden.nginx.admin.common.util;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Slf4j
public class HttpUtils {

    private static final int READ_TIMEOUT = 60000;

    private static final int CONNECT_TIMEOUT = 60000;

    public static String get(String urlAddr) throws Exception {
        return get(urlAddr, null, 0, 0);
    }

    public static String get(String urlAddr, Map<String, Object> paramsMap) throws Exception {
        return get(urlAddr, paramsMap, 0, 0);
    }

    public static String get(String urlAddr, Map<String, Object> paramsMap, int connectTimeout, int readTimeout) throws Exception {
        log.info("get request url: {}, params: {}", urlAddr, JSONObject.toJSONString(paramsMap));
        String line;
        String params = "";
        HttpURLConnection conn = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        StringBuffer result = new StringBuffer();

        if (connectTimeout < 1) {
            connectTimeout = CONNECT_TIMEOUT;
        }
        if (readTimeout < 1) {
            readTimeout = READ_TIMEOUT;
        }
        if (paramsMap != null && !paramsMap.isEmpty()) {
            StringBuffer str = getParams(paramsMap);
            if (str.length() > 0) {
                params = "?" + str.substring(0, str.length() - 1);
            }
        }
        try {
            URL url = new URL(urlAddr + params);
            conn = (HttpURLConnection) url.openConnection();
            // 设置读取超时时间
            conn.setReadTimeout(readTimeout);
            // 设置连接超时时间
            conn.setConnectTimeout(connectTimeout);
            conn.connect();
            inputStreamReader = new InputStreamReader(conn.getInputStream(), "utf-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
        } finally {
            closeStream(conn, bufferedReader, inputStreamReader, null);
        }
        return result.toString();
    }

    private static StringBuffer getParams(Map<String, Object> paramsMap) {
        StringBuffer str = new StringBuffer();
        Set set = paramsMap.keySet();
        Iterator iter = set.iterator();
        while (iter.hasNext()) {
            String key = iter.next().toString();
            if (paramsMap.get(key) == null) {
                continue;
            }
            str.append(key).append("=").append(paramsMap.get(key)).append("&");
        }
        return str;
    }

    public static String post(String urlAddr, Map<String, Object> paramsMap, int connectTimeout, int readTimeout) throws Exception {
        log.info("post request url: {}, params: {}", urlAddr, JSONObject.toJSONString(paramsMap));
        String line;
        String params = "";
        HttpURLConnection conn = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        StringBuffer result = new StringBuffer();
        try {
            if (connectTimeout < 1) {
                connectTimeout = CONNECT_TIMEOUT;
            }
            if (readTimeout < 1) {
                readTimeout = READ_TIMEOUT;
            }
            if (paramsMap != null && !paramsMap.isEmpty()) {
                StringBuffer str = getParams(paramsMap);
                if (str.length() > 0) {
                    params = str.substring(0, str.length() - 1);
                }
            }
            URL url = new URL(urlAddr);
            conn = (HttpURLConnection) url.openConnection();
            setConnection(connectTimeout, readTimeout, conn);
            if (!params.isEmpty()) {
                // 此处getOutputStream会隐含的进行connect()
                outputStreamWriter = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
                // 写入
                outputStreamWriter.write(params);
                // 刷新该流的缓冲
                outputStreamWriter.flush();
            }
            inputStreamReader = new InputStreamReader(conn.getInputStream(), "utf-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
        } finally {
            closeStream(conn, bufferedReader, inputStreamReader, outputStreamWriter);
        }
        return result.toString();
    }

    private static void setConnection(int connectTimeout, int readTimeout, HttpURLConnection conn) throws ProtocolException {
        // 设置读取超时时间
        conn.setReadTimeout(readTimeout);
        // 设置连接超时时间
        conn.setConnectTimeout(connectTimeout);
        // 设置是否向HttpURLConnection输出，因为这个是post请求，参数要放在http正文内，
        // 因此需要设为true, 默认情况下是false
        conn.setDoOutput(true);
        // 不使用缓存,默认情况下是true
        conn.setUseCaches(false);
        // 设定请求的方法为"POST",默认是GET
        conn.setRequestMethod("POST");
    }

    private static void closeStream(HttpURLConnection conn, BufferedReader bufferedReader, InputStreamReader inputStreamReader, OutputStreamWriter outputStreamWriter) {
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (inputStreamReader != null) {
            try {
                inputStreamReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (outputStreamWriter != null) {
            try {
                outputStreamWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String post(String urlAddr, String paramsStr) throws Exception {
        return post(urlAddr, paramsStr, 0, 0);
    }

    public static String post(String urlAddr, String paramsStr, int connectTimeout, int readTimeout) throws Exception {
        log.info("post request url: {}, params: {}", urlAddr, paramsStr);
        String line;
        HttpURLConnection conn = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        StringBuffer result = new StringBuffer();
        try {
            if (connectTimeout < 1) {
                connectTimeout = CONNECT_TIMEOUT;
            }
            if (readTimeout < 1) {
                readTimeout = READ_TIMEOUT;
            }
            URL url = new URL(urlAddr);
            conn = (HttpURLConnection) url.openConnection();
            setConnection(connectTimeout, readTimeout, conn);
            if (paramsStr != null && !paramsStr.isEmpty()) {
                // 此处getOutputStream会隐含的进行connect()
                outputStreamWriter = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
                // 写入
                outputStreamWriter.write(paramsStr);
                // 刷新该流的缓冲
                outputStreamWriter.flush();
            }
            inputStreamReader = new InputStreamReader(conn.getInputStream(), "utf-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
        } finally {
            closeStream(conn, bufferedReader, inputStreamReader, outputStreamWriter);
        }
        return result.toString();
    }


    public static String post(String requestUrl,  Object serializedObject) throws Exception {
        return post(requestUrl, 0, 0, serializedObject);
    }

    public static String post(String requestUrl, int connTimeoutMills,
                                              int readTimeoutMills, Object serializedObject) throws Exception {
        HttpURLConnection httpUrlConn = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        ObjectOutputStream oos = null;
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(requestUrl);
            httpUrlConn = (HttpURLConnection) url.openConnection();
            // 设置content_type=SERIALIZED_OBJECT
            // 如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException
            httpUrlConn.setRequestProperty("Content-Type", "application/x-java-serialized-object");
            if (connTimeoutMills < 1) {
                connTimeoutMills = CONNECT_TIMEOUT;
            }
            if (readTimeoutMills < 1) {
                readTimeoutMills = READ_TIMEOUT;
            }
            httpUrlConn.setConnectTimeout(connTimeoutMills);
            httpUrlConn.setReadTimeout(readTimeoutMills);
            // 设置是否向httpUrlConn输出，因为是post请求，参数要放在http正文内，因此需要设为true, 默认情况下是false
            httpUrlConn.setDoOutput(true);
            // 设置是否从httpUrlConn读入，默认情况下是true
            httpUrlConn.setDoInput(true);
            // 不使用缓存
            httpUrlConn.setUseCaches(false);

            // 设置请求方式，默认是GET
            httpUrlConn.setRequestMethod("POST");
            httpUrlConn.connect();

            if (serializedObject != null) {
                // 此处getOutputStream会隐含的进行connect，即：如同调用上面的connect()方法，
                // 所以在开发中不调用上述的connect()也可以，不过建议最好显式调用
                // write object(impl Serializable) using ObjectOutputStream
                oos = new ObjectOutputStream(httpUrlConn.getOutputStream());
                oos.writeObject(serializedObject);
                oos.flush();
                // outputStream不是一个网络流，充其量是个字符串流，往里面写入的东西不会立即发送到网络，
                // 而是存在于内存缓冲区中，待outputStream流关闭时，根据输入的内容生成http正文。所以这里的close是必须的
                oos.close();
            }
            // 将返回的输入流转换成字符串
            // 无论是post还是get，http请求实际上直到HttpURLConnection的getInputStream()这个函数里面才正式发送出去
            inputStream = httpUrlConn.getInputStream();//注意，实际发送请求的代码段就在这里
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
        } catch (Exception e) {
            log.error(requestUrl + " error ", e);
            throw e;
        } finally {
            try {
                IOUtils.closeQuietly(bufferedReader);
                IOUtils.closeQuietly(inputStreamReader);
                IOUtils.closeQuietly(inputStream);
                IOUtils.closeQuietly(oos);
                if (httpUrlConn != null) {
                    httpUrlConn.disconnect();
                }
            } catch (Exception e) {
            }
        }
        return buffer.toString();
    }


}
