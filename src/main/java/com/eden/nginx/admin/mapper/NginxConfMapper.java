package com.eden.nginx.admin.mapper;

import com.eden.nginx.admin.domain.entity.NginxConf;

public interface NginxConfMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(NginxConf record);

    int insertSelective(NginxConf record);

    NginxConf selectByPrimaryKey(Integer id);

    NginxConf selectByIP(String ip);

    int updateByPrimaryKeySelective(NginxConf record);

    int updateByPrimaryKey(NginxConf record);
}