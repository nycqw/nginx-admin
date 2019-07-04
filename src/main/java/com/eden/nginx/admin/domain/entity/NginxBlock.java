package com.eden.nginx.admin.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class NginxBlock {
    private Integer id;

    private String ip;

    private String name;

    private String value;

    private String description;

    private Integer status;

    private Integer pid;

    private String updateUser;

    private Date createTime;

    private Date updateTime;

}