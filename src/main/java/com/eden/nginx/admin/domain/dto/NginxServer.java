package com.eden.nginx.admin.domain.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenqw
 * @version 1.0
 * @since 2019/6/21
 */
@Data
public class NginxServer {

    /**
     * 监听端口
     */
    private int port;

    /**
     * 监听域名
     */
    private String name;

    private List<NginxParam> params;

    private List<NginxLocation> locations;
}
