package com.eden.nginx.admin.exception;

/**
 * @author chenqw
 * @version 1.0
 * @since 2019/6/21
 */
public class NginxOperationConfException extends RuntimeException {

    public NginxOperationConfException(String message) {
        super(message);
    }

    public NginxOperationConfException() {
        super();
    }
}
