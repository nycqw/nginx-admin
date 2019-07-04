package com.eden.nginx.admin.service;

import com.github.odiszapc.nginxparser.NgxBlock;
import com.github.odiszapc.nginxparser.NgxConfig;

public interface NginxConfigService {

    NgxConfig getNgxConf(String ip);

    void saveNgxConf(NgxBlock ngxBlock, String ip);

    NgxConfig recoveryNgxConf(int id);
}
