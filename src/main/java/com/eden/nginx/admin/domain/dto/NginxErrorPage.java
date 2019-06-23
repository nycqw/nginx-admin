package com.eden.nginx.admin.domain.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenqw
 * @version 1.0
 * @since 2019/6/21
 */
@Data
public class NginxErrorPage {

    /**
     * 页面状态码
     */
    private List<String> status = new ArrayList<>();

    /**
     * 跳转页面
     */
    private String path;
}
