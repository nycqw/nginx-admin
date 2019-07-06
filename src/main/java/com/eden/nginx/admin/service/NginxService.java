package com.eden.nginx.admin.service;


import com.eden.resource.client.common.dto.NginxBlock;

public interface NginxService {
    NginxBlock read(String ip);

    void save(NginxBlock nginxBlock, String ip);

    void bak(String conf, String ip);
}
