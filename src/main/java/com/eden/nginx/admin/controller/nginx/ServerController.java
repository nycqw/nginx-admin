package com.eden.nginx.admin.controller.nginx;

import com.eden.nginx.admin.domain.dto.NginxServer;
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
    public List<NginxServer> listServer() {
        return serverService.list();
    }

    @PostMapping("save")
    public void saveServer(@RequestBody @Valid NginxServer server) {
        serverService.save(server);
    }

    @PostMapping("delete")
    public void deleteServer(@RequestBody @Valid NginxServer server) {
        serverService.delete(server);
    }
}
