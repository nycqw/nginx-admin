package com.eden.nginx.admin.service;


import com.github.odiszapc.nginxparser.NgxConfig;

public interface NginxService {
    NgxConfig read(String ip);

    void save(NgxConfig conf, String ip);

    void bak(String conf, String ip);
}
