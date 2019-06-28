package com.eden.nginx.admin.exception;

/**
 * @author chenqw
 * @version 1.0
 * @since 2019/6/21
 */
public class NginxException extends RuntimeException {

    public NginxException(String message) {
        super(message);
    }

    public NginxException() {
        super();
    }
}
