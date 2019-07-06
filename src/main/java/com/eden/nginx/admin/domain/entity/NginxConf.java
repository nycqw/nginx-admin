package com.eden.nginx.admin.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class NginxConf {
    private Integer id;

    private String ip;

    private String path;

    private String conf;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    private String createUser;

    private String updateUser;
}