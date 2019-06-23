package com.eden.nginx.admin.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author chenqw
 * @version 1.0
 * @since 2019/6/22
 */
@Data
public class NginxLocation {

    private String path;

    private List<Attr> attrs;

    @Data
    public static class Attr{
        private String name;
        private String value;
    }
}
