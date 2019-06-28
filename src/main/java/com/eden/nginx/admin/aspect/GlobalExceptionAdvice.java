package com.eden.nginx.admin.aspect;

import com.eden.nginx.admin.domain.dto.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chenqw
 * @version 1.0
 * @since 2018/11/28
 */
@ControllerAdvice
public class GlobalExceptionAdvice {

    /**
     * 未知异常处理
     *
     * @param e 未知异常
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result exceptionHandle(Exception e) {
        return Result.fail(e.getMessage());
    }

    /**
     * 参数校验异常处理
     *
     * @param e 参数校验异常
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Result argumentValidExceptionHandle(MethodArgumentNotValidException e) {
        FieldError fieldError = (FieldError) e.getBindingResult().getAllErrors().get(0);
        return Result.fail(fieldError.getDefaultMessage());
    }

    /**
     * 一般的参数绑定时候抛出的异常
     * @param ex
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public Result handleBindException(BindException ex){
        List<String> defaultMsg = ex.getBindingResult().getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());
        return Result.fail(defaultMsg.get(0));
    }

    /**
     * 单个参数校验
     * @param ex
     * @return
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseBody
    public Result handleBindGetException(ConstraintViolationException ex){
        List<String> defaultMsg = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        return Result.fail(defaultMsg.get(0));
    }
}
