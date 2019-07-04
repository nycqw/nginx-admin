package com.eden.nginx.admin.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class NginxParam {
    private Integer id;

    private String ip;

    private String name;

    private String value;

    private String description;

    private Integer pid;

    private Integer status;

    private String updateUser;

    private Date createTime;

    private Date updateTime;

}