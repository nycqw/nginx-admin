package com.eden.nginx.admin.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eden.nginx.admin.common.util.HttpUtils;
import com.eden.nginx.admin.exception.NginxException;
import com.eden.nginx.admin.service.NginxService;
import com.github.odiszapc.nginxparser.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;

@Service
@Slf4j
public class NginxServiceImpl implements NginxService {

    private final String PORT = "8181";
    private final String READ = "/nginx/read";
    private final String SAVE = "/nginx/save";
    private final String BAK = "/nginx/bak";

    @Override
    public NgxConfig read(String ip) {
        try {
            String response = HttpUtils.get(getUrl(ip, READ));
            JSONObject parseObject = JSONObject.parseObject(response);
            Integer code = parseObject.getInteger("code");
            if (code == 1) {
                JSONObject data = parseObject.getJSONObject("data");
                NgxConfig ngxConfig = new NgxConfig();
                NgxBlock ngxBlock = parseNgxConfig(data);
                Iterator<NgxEntry> iterator = ngxBlock.getEntries().iterator();
                while (iterator.hasNext()) {
                    ngxConfig.addEntry(iterator.next());
                }
                Iterator<NgxToken> tokenIterator = ngxBlock.getTokens().iterator();
                while (tokenIterator.hasNext()) {
                    ngxConfig.addValue(tokenIterator.next().getToken());
                }
                return ngxConfig;
            } else {
                throw new NginxException(parseObject.getString("message"));
            }
        } catch (Exception e) {
            log.error("读取Nginx配置异常！", e);
            throw new NginxException(e.getMessage());
        }
    }

    private NgxBlock parseNgxConfig(JSONObject data) {
        NgxBlock ngxBlock = new NgxBlock();
        ngxBlock.addValue(data.getString("name"));
        ngxBlock.addValue(data.getString("value"));

        JSONArray entries = data.getJSONArray("entries");
        Iterator<Object> iterator = entries.iterator();
        while (iterator.hasNext()) {
            JSONObject next = (JSONObject)iterator.next();
            JSONArray subEntries = next.getJSONArray("entries");
            if (subEntries == null) {
                NgxParam ngxParam = new NgxParam();
                ngxParam.addValue(next.getString("name"));
                ngxParam.addValue(next.getString("value"));
                ngxBlock.addEntry(ngxParam);
            } else {
                NgxBlock subNgxBlock = parseNgxConfig(next);
                ngxBlock.addEntry(subNgxBlock);
            }
        }
        return ngxBlock;
    }

    @Override
    public void save(NgxConfig conf, String ip) {
        String params = JSONObject.toJSONString(conf);
        try {
            String response = HttpUtils.post(getUrl(ip, SAVE), params);
            JSONObject parseObject = JSONObject.parseObject(response);
            Integer code = parseObject.getInteger("code");
            if (code != 1) {
                throw new NginxException(parseObject.getString("message"));
            }
        } catch (Exception e) {
            log.error("保存Nginx配置异常！", e);
            throw new NginxException(e.getMessage());
        }
    }

    @Override
    public void bak(String conf, String ip) {
        String params = JSONObject.toJSONString(conf);
        try {
            String response = HttpUtils.post(getUrl(ip, BAK), params);
            JSONObject parseObject = JSONObject.parseObject(response);
            Integer code = parseObject.getInteger("code");
            if (code != 1) {
                throw new NginxException(parseObject.getString("message"));
            }
        } catch (Exception e) {
            log.error("备份Nginx配置异常！", e);
            throw new NginxException(e.getMessage());
        }
    }


    private String getUrl(String ip, String api) {
        return "http://" + ip + ":" + PORT + api;
    }
}
