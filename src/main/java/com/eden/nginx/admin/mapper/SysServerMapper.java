package com.eden.nginx.admin.mapper;

import com.eden.nginx.admin.domain.entity.SysServer;

public interface SysServerMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysServer record);

    int insertSelective(SysServer record);

    SysServer selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysServer record);

    int updateByPrimaryKey(SysServer record);
}