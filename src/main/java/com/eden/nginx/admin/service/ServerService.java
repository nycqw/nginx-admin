package com.eden.nginx.admin.service;

import com.eden.nginx.admin.domain.dto.NginxServer;

import java.util.List;

/**
 * @author chenqw
 * @version 1.0
 * @since 2019/6/21
 */
public interface ServerService {
    List<NginxServer> list();

    void save(NginxServer server);
}
