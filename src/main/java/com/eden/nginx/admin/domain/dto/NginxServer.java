package com.eden.nginx.admin.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenqw
 * @version 1.0
 * @since 2019/6/21
 */
@Data
public class NginxServer {

    /**
     * 监听端口
     */
    @NotNull(message = "端口不能为空")
    private int port;

    /**
     * 监听域名
     */
    @NotBlank(message = "域名不能为空")
    private String name;

    private List<NginxParam> params;

    private List<NginxLocation> locations;
}
