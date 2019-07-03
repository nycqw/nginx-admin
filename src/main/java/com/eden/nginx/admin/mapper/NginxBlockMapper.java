package com.eden.nginx.admin.mapper;

import com.eden.nginx.admin.domain.entity.NginxBlock;

import java.util.List;

public interface NginxBlockMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(NginxBlock record);

    int insertSelective(NginxBlock record);

    NginxBlock selectByPrimaryKey(Integer id);

    List<NginxBlock> selectBySelective(NginxBlock record);

    int updateByPrimaryKeySelective(NginxBlock record);

    int updateByPrimaryKey(NginxBlock record);
}