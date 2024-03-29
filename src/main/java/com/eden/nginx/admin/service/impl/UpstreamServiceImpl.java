package com.eden.nginx.admin.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.eden.nginx.admin.common.util.NgxUtil;
import com.eden.nginx.admin.domain.dto.NginxUpstream;
import com.eden.nginx.admin.exception.NginxException;
import com.eden.nginx.admin.service.NginxConfigService;
import com.eden.nginx.admin.service.NginxService;
import com.eden.nginx.admin.service.UpstreamService;
import com.github.odiszapc.nginxparser.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author chenqw
 * @version 1.0
 * @since 2019/6/29
 */
@Service
public class UpstreamServiceImpl implements UpstreamService {

    @Autowired
    private NginxConfigService nginxConfigService;

    @Autowired
    private NginxService nginxService;

    @Override
    public List<NginxUpstream> list(String ip) {
        NgxConfig ngxConfig = nginxConfigService.getNgxConf(ip);
        List<NginxUpstream> nginxUpstreams = new ArrayList<>();
        List<NgxBlock> upstreamList = NgxUtil.findBlock(ngxConfig, "upstream");
        for (int i=0; i<upstreamList.size(); i++) {
            NginxUpstream nginxUpstream = new NginxUpstream();
            nginxUpstream.setId(i);
            Iterator<NgxToken> iterator = upstreamList.get(i).getTokens().iterator();
            iterator.next();
            nginxUpstream.setName(iterator.next().getToken());
            List<NgxEntry> serverList = upstreamList.get(i).findAll(NgxParam.class, "server");

            List<NginxUpstream.ServerAddress> serverAddressList = new ArrayList<>();
            for (NgxEntry server : serverList) {
                NginxUpstream.ServerAddress serverAddress = new NginxUpstream.ServerAddress();
                NgxParam ngxParam = (NgxParam) server;
                Iterator<NgxToken> tokenIterator = ngxParam.getTokens().iterator();
                tokenIterator.next();
                String[] domain = tokenIterator.next().getToken().split(":");
                serverAddress.setName(domain[0]);
                if (domain.length > 1) {
                    serverAddress.setPort(Integer.parseInt(domain[1]));
                }
                if (tokenIterator.hasNext()) {
                    String weight = tokenIterator.next().getToken();
                    serverAddress.setWeight(Integer.parseInt(weight));
                }
                serverAddressList.add(serverAddress);
            }
            nginxUpstream.setServerAddressList(serverAddressList);
            nginxUpstreams.add(nginxUpstream);
        }
        return nginxUpstreams;
    }

    @Override
    public void save(NginxUpstream nginxUpstream) {
        String ip = nginxUpstream.getIp();
        NgxConfig conf = nginxConfigService.getNgxConf(ip);
        String bakConf = NgxUtil.toString(conf);
        List<NgxBlock> upstreamList = NgxUtil.findBlock(conf, "upstream");

        try {
            NgxBlock upstream = findUpstream(nginxUpstream, upstreamList);
            if (upstream == null) {
                upstream = new NgxBlock();
                upstream.addValue("upstream");
                upstream.addValue(nginxUpstream.getName());
                conf.addEntry(upstream);
            }
            handlerServerAddress(nginxUpstream, upstream);
            nginxConfigService.saveNgxConf(conf, ip);
        } catch (Exception e) {
            nginxService.bak(bakConf, ip);
            throw new NginxException(e.getMessage());
        }
    }

    @Override
    public void delete(NginxUpstream nginxUpstream) {
        String ip = nginxUpstream.getIp();
        NgxConfig conf = nginxConfigService.getNgxConf(ip);
        String bakConf = NgxUtil.toString(conf);
        List<NgxBlock> upstreamList = NgxUtil.findBlock(conf, "upstream");

        try {
            NgxBlock upstream = findUpstream(nginxUpstream, upstreamList);
            if (upstream != null) {
                conf.remove(upstream);
                nginxConfigService.saveNgxConf(conf, ip);
            }
        } catch (Exception e) {
            nginxService.bak(bakConf, ip);
            throw new NginxException(e.getMessage());
        }
    }


    private void handlerServerAddress(NginxUpstream nginxUpstream, NgxBlock upstream) {
        upstream.getEntries().clear();
        List<NginxUpstream.ServerAddress> serverAddressList = nginxUpstream.getServerAddressList();
        if (!CollectionUtils.isEmpty(serverAddressList)) {
            for (NginxUpstream.ServerAddress serverAddress : serverAddressList) {
                NgxParam ngxParam = new NgxParam();
                ngxParam.addValue("server");
                ngxParam.addValue(serverAddress.getName() + ":" + serverAddress.getPort());
                ngxParam.addValue(String.valueOf(serverAddress.getWeight()));
                upstream.addEntry(ngxParam);
            }
        }
    }

    private NgxBlock findUpstream(NginxUpstream nginxUpstream, List<NgxBlock> upstreamList) {
        for (NgxBlock ngxBlock : upstreamList) {
            Iterator<NgxToken> iterator = ngxBlock.getTokens().iterator();
            iterator.next();
            if (iterator.next().getToken().equals(nginxUpstream.getName())) {
                return ngxBlock;
            }
        }
        return null;
    }
}
