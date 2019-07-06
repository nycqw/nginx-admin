package com.eden.nginx.admin.domain.dto;

import com.eden.nginx.admin.domain.entity.NginxParam;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author chenqw
 * @version 1.0
 * @since 2019/6/21
 */
@Data
public class NginxServer {

    private String ip;

    private String path;

    private Integer id;

    private Integer port;

    @NotEmpty(message = "域名不能为空")
    private String name;

    private List<NginxParam> params;

    private List<NginxLocation> locations;
}
