package com.eden.nginx.admin;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableDubbo
public class NginxAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(NginxAdminApplication.class, args);
    }

}
