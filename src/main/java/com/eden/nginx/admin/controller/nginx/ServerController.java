package com.eden.nginx.admin.controller.nginx;

import com.eden.nginx.admin.aspect.annotation.Verify;
import com.eden.nginx.admin.domain.dto.NginxServer;
import com.eden.nginx.admin.domain.dto.Result;
import com.eden.nginx.admin.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author chenqw
 * @version 1.0
 * @since 2019/6/21
 */
@RestController
@RequestMapping("/server")
public class ServerController {

    @Autowired
    private ServerService serverService;

    @GetMapping("list")
    public Result<List<NginxServer>> listServer() {
        List<NginxServer> list = serverService.list();
        return Result.success(list);
    }

    @PostMapping("save")
    @Verify
    public Result saveServer(@RequestBody NginxServer server) {
        serverService.save(server);
        return Result.success();
    }

    @PostMapping("delete")
    public Result deleteServer(@RequestBody NginxServer server) {
        serverService.delete(server);
        return Result.success();
    }
}
