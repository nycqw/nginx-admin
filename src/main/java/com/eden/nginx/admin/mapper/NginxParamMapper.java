package com.eden.nginx.admin.mapper;

import com.eden.nginx.admin.domain.entity.NginxParam;

import java.util.List;

public interface NginxParamMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(NginxParam record);

    int insertSelective(NginxParam record);

    NginxParam selectByPrimaryKey(Integer id);

    List<NginxParam> selectBySelective(NginxParam record);

    int updateByPrimaryKeySelective(NginxParam record);

    int updateByPrimaryKey(NginxParam record);
}