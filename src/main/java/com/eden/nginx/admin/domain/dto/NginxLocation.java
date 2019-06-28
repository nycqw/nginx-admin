package com.eden.nginx.admin.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author chenqw
 * @version 1.0
 * @since 2019/6/22
 */
@Data
public class NginxLocation {

    @NotBlank
    private String path;

    private List<Attr> attrs;

    @Data
    public static class Attr{
        @NotBlank
        private String name;
        @NotBlank
        private String value;
    }
}
