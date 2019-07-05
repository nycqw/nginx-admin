package com.eden.nginx.admin.mapper;

import com.eden.nginx.admin.domain.entity.SysServer;

import java.util.List;

public interface SysServerMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysServer record);

    int insertSelective(SysServer record);

    SysServer selectByPrimaryKey(Integer id);

    List<SysServer> selectAll();

    int updateByPrimaryKeySelective(SysServer record);

    int updateByPrimaryKey(SysServer record);
}