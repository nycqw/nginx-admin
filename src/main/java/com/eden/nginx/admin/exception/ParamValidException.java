package com.eden.nginx.admin.exception;

/**
 * @author chenqw
 * @version 1.0
 * @since 2019/6/29
 */
public class ParamValidException extends RuntimeException {

    public ParamValidException() {
        super();
    }

    public ParamValidException(String message) {
        super(message);
    }
}
