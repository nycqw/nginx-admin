package com.eden.nginx.admin.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.eden.nginx.admin.common.util.HttpUtils;
import com.eden.nginx.admin.exception.NginxException;
import com.eden.nginx.admin.service.NginxService;
import com.eden.resource.client.common.dto.NginxBlock;
import com.eden.resource.client.service.NginxTransferHandler;
import com.github.odiszapc.nginxparser.NgxConfig;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NginxServiceImpl implements NginxService {

    private final String PORT = "8181";
    private final String READ = "/nginx/read";
    private final String SAVE = "/nginx/save";
    private final String BAK = "/nginx/bak";

    @Override
    public NginxBlock read(String ip) {
        try {
            String response = HttpUtils.get(getUrl(ip, READ));
            JSONObject parseObject = JSONObject.parseObject(response);
            Integer code = parseObject.getInteger("code");
            if (code == 1) {
                String data = parseObject.getString("data");
                return JSONObject.parseObject(data, NginxBlock.class);
            } else {
                throw new NginxException(parseObject.getString("message"));
            }
        } catch (Exception e) {
            log.error("读取Nginx配置异常！", e);
            throw new NginxException(e.getMessage());
        }
    }

    @Override
    public void save(NginxBlock nginxBlock, String ip) {
        try {
            String response = HttpUtils.post(getUrl(ip, SAVE), nginxBlock);
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
