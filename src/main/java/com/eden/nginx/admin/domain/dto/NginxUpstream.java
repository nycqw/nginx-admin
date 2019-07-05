package com.eden.nginx.admin.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author chenqw
 * @version 1.0
 * @since 2019/6/29
 */
@Data
public class NginxUpstream {

    private int id;
    @NotEmpty
    private String ip;
    @NotEmpty
    private String name;
    private String description;
    private List<ServerAddress> serverAddressList;

    @Data
    public static class ServerAddress {
        @NotEmpty
        private String name;
        @NotEmpty
        private int port;
        private int weight;
    }
}
