package com.eden.nginx.admin.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.eden.nginx.admin.domain.entity.SysServer;
import com.eden.nginx.admin.mapper.SysServerMapper;
import com.eden.nginx.admin.service.SysServerService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class SysServerServiceImpl implements SysServerService {

    @Autowired
    private SysServerMapper sysServerMapper;

    @Override
    public List<SysServer> list() {
        return sysServerMapper.selectAll();
    }
}
