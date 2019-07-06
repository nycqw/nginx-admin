package com.eden.nginx.admin.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.eden.nginx.admin.domain.entity.NginxConf;
import com.eden.nginx.admin.mapper.NginxConfMapper;
import com.eden.nginx.admin.service.NginxConfigService;
import com.eden.nginx.admin.service.NginxService;
import com.eden.resource.client.common.dto.NginxBlock;
import com.eden.resource.client.service.NginxTransferHandler;
import com.github.odiszapc.nginxparser.NgxBlock;
import com.github.odiszapc.nginxparser.NgxConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NginxConfServiceImpl implements NginxConfigService {

    @Autowired
    private NginxConfMapper nginxConfMapper;

    @Autowired
    private NginxService nginxService;

    @Override
    @Transactional
    public NgxConfig getNgxConf(String ip) {
        NginxConf conf = nginxConfMapper.selectByIP(ip);
        NginxBlock nginxBlock;
        if (conf == null) {
            nginxBlock = nginxService.read(ip);
            int id = insertNginxConf(ip, nginxBlock);
            conf = nginxConfMapper.selectByPrimaryKey(id);
        }
        nginxBlock = JSONObject.parseObject(conf.getConf(), NginxBlock.class);
        NgxConfig ngxConfig = NginxTransferHandler.reverseNgxConfig(nginxBlock);
        ngxConfig.setId(conf.getId());
        ngxConfig.setIp(conf.getIp());
        return ngxConfig;
    }

    @Override
    @Transactional
    public void saveNgxConf(NgxBlock ngxBlock, String ip) {
        NginxConf conf = nginxConfMapper.selectByIP(ip);
        if (conf != null) {
            nginxConfMapper.deleteByPrimaryKey(conf.getId());
        }
        NginxBlock nginxBlock = NginxTransferHandler.transferNgxConfig(ngxBlock);
        insertNginxConf(ip, nginxBlock);
        nginxService.save(nginxBlock, ip);
    }

    private int insertNginxConf(String ip, NginxBlock nginxBlock) {
        NginxConf nginxConf = new NginxConf();
        nginxConf.setConf(JSONObject.toJSONString(nginxBlock));
        nginxConf.setIp(ip);
        nginxConf.setStatus(1);
        nginxConf.setUpdateUser("admin");
        nginxConf.setCreateUser("admin");
        nginxConfMapper.insert(nginxConf);
        return nginxConf.getId();
    }

}
