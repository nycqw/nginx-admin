package com.eden.nginx.admin.config;

import com.eden.nginx.admin.common.constants.Constants;
import com.eden.nginx.admin.exception.NginxOperationConfException;
import com.github.odiszapc.nginxparser.NgxConfig;
import com.github.odiszapc.nginxparser.NgxDumper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author chenqw
 * @version 1.0
 * @since 2019/6/21
 */
@Component
public class NginxContext {

    @Value("${nginx.config}")
    private String nginxConfPath;

    /**
     * 读取配置文件
     *
     * @return
     */
    public NgxConfig read() {
        try {
            return NgxConfig.read(nginxConfPath + Constants.NGINX_CONF_NAME);
        } catch (IOException e) {
            throw new NginxOperationConfException();
        }
    }

    /**
     * 写配置到文件中
     *
     * @param conf
     */
    public void save(NgxConfig conf) {
        try (FileOutputStream out = new FileOutputStream(nginxConfPath + Constants.NGINX_CONF_NAME)) {
            out.write(toString(conf).getBytes("UTF-8"));
            out.flush();
        } catch (Exception e) {
            throw new NginxOperationConfException("Nginx配置文件写入失败");
        }
    }

    /**
     * 写配置到文件中
     * @param conf
     */
    public void save(String conf) {
        try (FileOutputStream out = new FileOutputStream(nginxConfPath + Constants.NGINX_CONF_NAME)) {
            out.write(conf.getBytes("UTF-8"));
            out.flush();
        } catch (Exception e) {
            throw new NginxOperationConfException("Nginx配置文件写入失败");
        }
    }

    /**
     * 配置到文本
     *
     * @param conf
     * @return
     */
    public static String toString(NgxConfig conf) {
        if (null == conf) {
            throw new NginxOperationConfException("不能写入空配置");
        }
        NgxDumper dumper = new NgxDumper(conf);
        return dumper.dump();
    }

}
