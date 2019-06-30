package com.eden.nginx.admin.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author chenqw
 * @version 1.0
 * @since 2019/6/22
 */
@Data
public class NginxLocation {

    @NotEmpty
    private String path;

    private List<Attr> attrs;

    @Data
    public static class Attr{
        @NotEmpty(message = "参数值不能为空")
        private String name;
        @NotEmpty(message = "参数值不能为空")
        private String value;
    }
}
