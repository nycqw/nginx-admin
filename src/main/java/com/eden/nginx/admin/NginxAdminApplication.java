package com.eden.nginx.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class NginxAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(NginxAdminApplication.class, args);
    }

}
