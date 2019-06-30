package com.eden.nginx.admin.controller.nginx;

import com.eden.nginx.admin.aspect.annotation.Verify;
import com.eden.nginx.admin.domain.dto.NginxUpstream;
import com.eden.nginx.admin.domain.dto.Result;
import com.eden.nginx.admin.service.UpstreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author chenqw
 * @version 1.0
 * @since 2019/6/29
 */
@RestController
@RequestMapping("/upstream")
public class UpstreamController {

    @Autowired
    private UpstreamService upstreamService;

    @RequestMapping("list")
    public Result list() {
        List<NginxUpstream> list = upstreamService.list();
        return Result.success(list);
    }

    @RequestMapping("save")
    public Result save(@RequestBody NginxUpstream nginxUpstream) {
        upstreamService.save(nginxUpstream);
        return Result.success();
    }

    @RequestMapping("delete")
    public Result delete(@RequestBody NginxUpstream nginxUpstream) {
        upstreamService.delete(nginxUpstream);
        return Result.success();
    }
}
