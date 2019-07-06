package com.eden.nginx.admin.service.impl;

import com.eden.nginx.admin.common.util.NgxUtil;
import com.eden.nginx.admin.domain.entity.NginxBlock;
import com.eden.nginx.admin.domain.entity.NginxParam;
import com.eden.nginx.admin.mapper.NginxBlockMapper;
import com.eden.nginx.admin.mapper.NginxParamMapper;
import com.eden.nginx.admin.service.NginxConfigService;
import com.eden.nginx.admin.service.NginxService;
import com.eden.resource.client.service.NginxTransferHandler;
import com.github.odiszapc.nginxparser.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.List;

@Service
public class NginxConfServiceImpl implements NginxConfigService {

    @Autowired
    private NginxParamMapper nginxParamMapper;

    @Autowired
    private NginxBlockMapper nginxBlockMapper;

    @Autowired
    private NginxService nginxService;

    @Override
    @Transactional
    public NgxConfig getNgxConf(String ip) {
        NginxBlock params = new NginxBlock();
        params.setValue(ip);
        List<NginxBlock> nginxBlocks = nginxBlockMapper.selectBySelective(params);
        if (CollectionUtils.isEmpty(nginxBlocks)) {
            NgxConfig ngxConfig = nginxService.read(ip);
            saveNgxConf(ngxConfig, ip);
        }
        return recoveryNgxConf(nginxBlocks.get(0).getId());
    }

    /**
     * 从文件中存储到数据库中
     *
     * @param ngxBlock
     */
    @Override
    public void saveNgxConf(NgxBlock ngxBlock, String ip) {
        saveNgxConf(ngxBlock, ip, null);
    }

    private void saveNgxConf(NgxBlock ngxBlock, String ip, Integer pid) {
        int id = saveNginxBlock(ngxBlock, ip, pid);
        saveNginxParam(ngxBlock, ip, id);

        List<NgxBlock> ngxBlocks = NgxUtil.findEntryList(ngxBlock, NgxBlock.class);
        for (NgxBlock block : ngxBlocks) {
            saveNgxConf(block, ip, id);
        }
    }

    /**
     * 从数据库中恢复到文件中
     *
     * @param id
     * @return
     */
    @Override
    public NgxConfig recoveryNgxConf(int id) {
        NgxConfig ngxConfig = new NgxConfig();
        NginxBlock nginxBlock = nginxBlockMapper.selectByPrimaryKey(id);
        NgxBlock ngxBlock = recoveryNgxBlock(nginxBlock);
        Iterator<NgxToken> tokenIterator = ngxBlock.getTokens().iterator();
        while (tokenIterator.hasNext()) {
            ngxConfig.addValue(tokenIterator.next().getToken());
        }
        Iterator<NgxEntry> iterator = ngxBlock.getEntries().iterator();
        while (iterator.hasNext()) {
            ngxConfig.addEntry(iterator.next());
        }
        return ngxConfig;
    }

    private void saveNginxParam(NgxBlock ngxBlock, String ip, Integer id) {
        List<NgxParam> ngxParams = NgxUtil.findEntryList(ngxBlock, NgxParam.class);
        if (!CollectionUtils.isEmpty(ngxParams)) {
            for (NgxParam ngxParam : ngxParams) {
                NginxParam nginxParam = new NginxParam();
                nginxParam.setPid(id);
                nginxParam.setIp(ip);
                Iterator<NgxToken> iterator = ngxParam.getTokens().iterator();
                nginxParam.setName(iterator.next().getToken());
                nginxParam.setValue(getValue(iterator));
                nginxParamMapper.insertSelective(nginxParam);
            }
        }
    }

    private int saveNginxBlock(NgxBlock ngxBlock, String ip, Integer pid) {
        NginxBlock nginxBlock = new NginxBlock();
        Iterator<NgxToken> iterator = ngxBlock.getTokens().iterator();
        if (iterator.hasNext()) {
            nginxBlock.setName(iterator.next().getToken());
            nginxBlock.setValue(getValue(iterator));
        }
        nginxBlock.setIp(ip);
        nginxBlock.setPid(pid);
        nginxBlockMapper.insert(nginxBlock);
        return nginxBlock.getId();
    }

    private String getValue(Iterator<NgxToken> iterator) {
        StringBuilder value = new StringBuilder();
        while (iterator.hasNext()) {
            value.append(iterator.next().getToken()).append(" ");
        }
        return value.toString().trim();
    }

    private NgxBlock recoveryNgxBlock(NginxBlock nginxBlock) {
        NgxBlock ngxBlock = new NgxBlock();
        ngxBlock.addValue(nginxBlock.getName());
        String value = nginxBlock.getValue();
        if (!StringUtils.isEmpty(value)) {
            ngxBlock.addValue(value);
        }

        recoveryNgxParam(ngxBlock, nginxBlock);

        NginxBlock param = new NginxBlock();
        param.setPid(nginxBlock.getId());
        List<NginxBlock> nginxBlocks = nginxBlockMapper.selectBySelective(param);
        if (!CollectionUtils.isEmpty(nginxBlocks)) {
            for (NginxBlock block : nginxBlocks) {
                ngxBlock.addEntry(recoveryNgxBlock(block));
            }
        }
        return ngxBlock;
    }

    private void recoveryNgxParam(NgxBlock ngxBlock, NginxBlock nginxBlock) {
        NginxParam params = new NginxParam();
        params.setPid(nginxBlock.getId());
        List<NginxParam> nginxParams = nginxParamMapper.selectBySelective(params);
        if (!CollectionUtils.isEmpty(nginxParams)) {
            for (NginxParam nginxParam : nginxParams) {
                NgxParam ngxParam = new NgxParam();
                ngxParam.addValue(nginxParam.getName());
                String[] tokens = nginxParam.getValue().split(" ");
                for (String token : tokens) {
                    ngxParam.addValue(token);
                }
                ngxBlock.addEntry(ngxParam);
            }
        }
    }
}
