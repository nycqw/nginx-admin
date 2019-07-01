package com.eden.nginx.admin.service.impl;

import com.eden.nginx.admin.config.NginxContext;
import com.eden.nginx.admin.domain.dto.NginxUpstream;
import com.eden.nginx.admin.exception.NginxException;
import com.eden.nginx.admin.service.UpstreamService;
import com.github.odiszapc.nginxparser.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
    private NginxContext context;

    @Override
    public List<NginxUpstream> list() {
        NgxConfig ngxConfig = context.read();

        ArrayList<NginxUpstream> nginxUpstreams = new ArrayList<>();
        List<NgxBlock> upstreamList = context.findBlock(ngxConfig, "upstream");
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
        NgxConfig conf = context.read();
        String bakConf = context.toString(conf);
        List<NgxBlock> upstreamList = context.findBlock(conf, "upstream");

        try {
            NgxBlock upstream = findUpstream(nginxUpstream, upstreamList);
            if (upstream == null) {
                upstream = new NgxBlock();
                upstream.addValue("upstream");
                upstream.addValue(nginxUpstream.getName());
                conf.addEntry(upstream);
            }
            handlerServerAddress(nginxUpstream, upstream);
            context.save(conf);
        } catch (Exception e) {
            context.save(bakConf);
            throw new NginxException(e.getMessage());
        }
    }

    @Override
    public void delete(NginxUpstream nginxUpstream) {
        NgxConfig conf = context.read();
        String bakConf = context.toString(conf);
        List<NgxBlock> upstreamList = context.findBlock(conf, "upstream");

        try {
            NgxBlock upstream = findUpstream(nginxUpstream, upstreamList);
            if (upstream != null) {
                conf.remove(upstream);
                context.save(conf);
            }
        } catch (Exception e) {
            context.save(bakConf);
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
