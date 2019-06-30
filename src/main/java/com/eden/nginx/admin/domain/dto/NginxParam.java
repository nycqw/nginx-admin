package com.eden.nginx.admin.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author chenqw
 * @version 1.0
 * @since 2019/6/23
 */
@Data
public class NginxParam {

    @NotEmpty(message = "参数名不能为空")
    private String name;
    @NotEmpty(message = "参数值不能为空")
    private String value;
}
