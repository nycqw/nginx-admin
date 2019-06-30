package com.eden.nginx.admin.service.impl;

import com.eden.nginx.admin.config.NginxContext;
import com.eden.nginx.admin.domain.dto.NginxLocation;
import com.eden.nginx.admin.domain.dto.NginxParam;
import com.eden.nginx.admin.domain.dto.NginxServer;
import com.eden.nginx.admin.exception.NginxException;
import com.eden.nginx.admin.service.ServerService;
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
 * @since 2019/6/21
 */
@Service
public class ServerServiceImpl implements ServerService {

    @Autowired
    private NginxContext context;

    @Override
    public List<NginxServer> list() {
        NgxConfig ngxConfig = context.read();

        List<NgxEntry> serverList = ngxConfig.findAll(NgxBlock.class, "http", "server");
        List<NginxServer> result = new ArrayList<>();
        for (NgxEntry ngxEntry : serverList) {
            NgxBlock server = (NgxBlock) ngxEntry;
            NginxServer nginxServer = new NginxServer();
            NgxParam listen = server.findParam("listen");
            if (null != listen) {
                nginxServer.setPort(Integer.valueOf(listen.getValue()));
            }
            NgxParam server_name = server.findParam("server_name");
            if (null != server_name) {
                nginxServer.setName(server_name.getValue());
            }
            ArrayList<NginxParam> nginxParams = getNginxParams(server);
            nginxServer.setParams(nginxParams);

            List<NginxLocation> locations = getLocations(server);
            nginxServer.setLocations(locations);

            result.add(nginxServer);
        }
        if (!CollectionUtils.isEmpty(result)) {
            for (int i=0; i<result.size(); i++) {
                result.get(i).setId(i);
            }
        }
        return result;
    }

    @Override
    public void save(NginxServer server) {
        NgxConfig conf = context.read();
        String bakConf = context.toString(conf);
        NgxBlock http = conf.findBlock("http");
        List<NgxEntry> ngxServers = http.findAll(NgxConfig.BLOCK, "server");

        try {
            NgxBlock ngxServer = findServer(server, ngxServers);
            if (ngxServer == null) {
                ngxServer = new NgxBlock();
                ngxServer.addValue("server");
                handleServerDomain(server, ngxServer);
                http.addEntry(ngxServer);
            }
            handleServerParam(server, ngxServer);
            handleServerLocation(server, ngxServer);

            context.save(conf);
        } catch (Exception e) {
            context.save(bakConf);
            throw new NginxException(e.getMessage());
        }
    }

    @Override
    public void delete(NginxServer server) {
        NgxConfig conf = context.read();
        String bakConf = context.toString(conf);
        NgxBlock http = conf.findBlock("http");
        List<NgxEntry> ngxServers = http.findAll(NgxConfig.BLOCK, "server");
        try {
            NgxBlock ngxServer = findServer(server, ngxServers);
            if (ngxServer != null) {
                http.remove(ngxServer);
            }
            context.save(conf);
        } catch (Exception e) {
            context.save(bakConf);
            throw new NginxException("已回滚到上次配置:");
        }
    }

    private ArrayList<NginxParam> getNginxParams(NgxBlock server) {
        ArrayList<NginxParam> nginxParams = new ArrayList<>();
        List<NgxParam> params = findAllParam(server);
        for (NgxParam param : params) {
            Iterator<NgxToken> iterator = param.getTokens().iterator();
            NginxParam nginxParam = new NginxParam();
            String name = iterator.next().getToken();
            if (name.equals("listen") || name.equals("server_name")) {
                continue;
            }
            nginxParam.setName(name);
            nginxParam.setValue(getParamValue(iterator));
            nginxParams.add(nginxParam);
        }
        return nginxParams;
    }

    private String getParamValue(Iterator<NgxToken> iterator) {
        StringBuilder value = new StringBuilder();
        while (iterator.hasNext()) {
            value.append(iterator.next().getToken()).append(" ");
        }
        return value.toString().trim();
    }

    private List<NgxParam> findAllParam(NgxBlock ngxBlock) {
        List<NgxParam> result = new ArrayList<>();
        Iterator<NgxEntry> iterator = ngxBlock.getEntries().iterator();
        while (iterator.hasNext()) {
            NgxEntry entry = iterator.next();
            NgxEntryType type = NgxEntryType.fromClass(entry.getClass());
            if (NgxEntryType.PARAM.equals(type)) {
                result.add((NgxParam) entry);
            }
        }
        return result;
    }

    private List<NginxLocation> getLocations(NgxBlock server) {
        List<NginxLocation> locations = new ArrayList<>();
        for (NgxEntry entry : server.getEntries()) {
            if (entry instanceof NgxBlock) {
                NgxBlock ngxBlock = (NgxBlock) entry;
                Iterator<NgxToken> iterator = ngxBlock.getTokens().iterator();
                if (iterator.next().getToken().equals("location")) {
                    NginxLocation nginxLocation = new NginxLocation();
                    nginxLocation.setPath(iterator.next().getToken());

                    List<NginxLocation.Attr> attrs = new ArrayList<>();
                    Iterator<NgxEntry> entryIterator = ngxBlock.getEntries().iterator();
                    while (entryIterator.hasNext()) {
                        NginxLocation.Attr attr = new NginxLocation.Attr();
                        NgxParam ngxParam = (NgxParam) entryIterator.next();
                        Iterator<NgxToken> tokenIterator = ngxParam.getTokens().iterator();
                        attr.setName(tokenIterator.next().getToken());
                        String value = "";
                        while (tokenIterator.hasNext()) {
                            value += tokenIterator.next().getToken() + " ";
                        }
                        attr.setValue(value.trim());
                        attrs.add(attr);
                    }
                    nginxLocation.setAttrs(attrs);
                    locations.add(nginxLocation);
                }
            }
        }
        return locations;
    }

    private void handleServerDomain(NginxServer server, NgxBlock ngxServer) {
        NgxParam portParam = new NgxParam();
        portParam.addValue("listen");
        portParam.addValue(String.valueOf(server.getPort()));
        ngxServer.addEntry(portParam);
        NgxParam domainParam = new NgxParam();
        domainParam.addValue("server_name");
        domainParam.addValue(String.valueOf(server.getName()));
        ngxServer.addEntry(domainParam);
    }

    private void handleServerParam(NginxServer server, NgxBlock ngxServer) {
        List<NginxParam> params = server.getParams();
        List<NgxParam> ngxParams = findAllParam(ngxServer);
        if (!CollectionUtils.isEmpty(ngxParams)) {
            for (NgxParam nginxParam : ngxParams) {
                String name = nginxParam.getTokens().iterator().next().getToken();
                if (name.equals("listen") || name.equals("server_name")) {
                    continue;
                }
                ngxServer.remove(nginxParam);
            }
        }
        for (NginxParam param : params) {
            String name = param.getName();
            String value = param.getValue();
            NgxParam ngxParam = new NgxParam();
            ngxParam.addValue(name);
            ngxParam.addValue(value);
            ngxServer.addEntry(ngxParam);
        }
    }

    private void handleServerLocation(NginxServer server, NgxBlock ngxServer) {
        List<NginxLocation> locations = server.getLocations();
        for (NginxLocation location : locations) {
            String path = location.getPath();
            if (!StringUtils.isEmpty(path)) {
                NgxBlock ngxLocation = findBlock(ngxServer, "location", path);
                if (ngxLocation != null) {
                    ngxLocation.getEntries().clear();
                    ngxServer.remove(ngxLocation);
                } else {
                    ngxLocation = new NgxBlock();
                    ngxLocation.addValue("location");
                    ngxLocation.addValue(path);
                }

                List<NginxLocation.Attr> attrs = location.getAttrs();
                if (!CollectionUtils.isEmpty(attrs)) {
                    for (NginxLocation.Attr attr : attrs) {
                        String name = attr.getName();
                        if (!StringUtils.isEmpty(name)) {
                            NgxParam ngxParam = new NgxParam();
                            ngxParam.addValue(name);
                            ngxParam.addValue(attr.getValue());
                            ngxLocation.addEntry(ngxParam);
                        }
                    }
                    ngxServer.addEntry(ngxLocation);
                }
            }
        }
    }

    private NgxBlock findServer(NginxServer server, List<NgxEntry> ngxServers) {
        if (!CollectionUtils.isEmpty(ngxServers)) {
            for (NgxEntry ngxEntry : ngxServers) {
                NgxBlock ngxServer = (NgxBlock) ngxEntry;
                String port = getPort(ngxServer);
                String serverName = getServerName(ngxServer);
                if (server.getPort() == Integer.valueOf(port)
                        && serverName.equals(server.getName())) {
                    return ngxServer;
                }
            }
        }
        return null;
    }

    private String getServerName(NgxBlock ngxServer) {
        NgxParam serverName = ngxServer.findParam("server_name");
        return serverName.getValue();
    }

    private String getPort(NgxBlock ngxServer) {
        NgxParam listen = ngxServer.findParam("listen");
        return listen.getValue();
    }

    private NgxBlock findBlock(NgxBlock server, String name, String value) {
        for (NgxEntry entry : server.getEntries()) {
            if (entry instanceof NgxBlock) {
                NgxBlock ngxBlock = (NgxBlock) entry;
                Iterator<NgxToken> iterator = ngxBlock.getTokens().iterator();
                if (iterator.next().getToken().equals(name)
                        && iterator.next().getToken().equals(value)) {
                    return ngxBlock;
                }
            }
        }
        return null;
    }

}
