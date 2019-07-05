package com.eden.nginx.admin.common.util;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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


    /**
     * 获取客户端ip地址
     */
    public static String getClientIp(HttpServletRequest request) {
        // 网宿cdn的真实ip
        String ip = request.getHeader("Cdn-Src-Ip");
        if (StringUtils.isBlank(ip) || " unknown".equalsIgnoreCase(ip)) {
            // 蓝讯cdn的真实ip
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(ip) || " unknown".equalsIgnoreCase(ip)) {
            // 获取代理ip
            ip = request.getHeader("X-Forwarded-For");
        }
        if (StringUtils.isBlank(ip) || " unknown".equalsIgnoreCase(ip)) {
            // 获取代理ip
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            // 获取代理ip
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            // 获取真实ip
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

}
