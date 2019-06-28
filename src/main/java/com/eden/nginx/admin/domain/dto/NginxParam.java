package com.eden.nginx.admin.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author chenqw
 * @version 1.0
 * @since 2019/6/23
 */
@Data
public class NginxParam {

    @NotBlank(message = "参数名不能为空")
    private String name;
    @NotBlank(message = "参数值不能为空")
    private String value;
}
