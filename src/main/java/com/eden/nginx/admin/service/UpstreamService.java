package com.eden.nginx.admin.service;

import com.eden.nginx.admin.domain.dto.NginxUpstream;

import java.util.List;

/**
 * @author chenqw
 * @version 1.0
 * @since 2019/6/29
 */
public interface UpstreamService {
    List<NginxUpstream> list();

    void save(NginxUpstream nginxUpstream);

    void delete(NginxUpstream nginxUpstream);
}
