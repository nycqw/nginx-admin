package com.eden.nginx.admin.aspect;

import com.eden.nginx.admin.common.util.AopUtil;
import com.eden.nginx.admin.exception.ParamValidException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author chenqw
 * @version 1.0
 * @since 2019/6/29
 */
@Aspect
@Component
public class ParamValidAspect {

    @Pointcut("@annotation(com.eden.nginx.admin.aspect.annotation.Verify)")
    private void pointCut() {
    }

    @Before("pointCut()")
    private void before(JoinPoint joinPoint) throws IllegalAccessException {
        Object[] args = joinPoint.getArgs();
        for (Object object : args) {
            verifyParam(object);
        }
    }

    private void verifyParam(Object obj) throws IllegalAccessException {
        if (obj == null) {
            return;
        }
        Field[] fields = obj.getClass().getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.getClass().isPrimitive()
                        || field.getType() == String.class
                        ||  field.getType() == Integer.class) {
                    verifyNotEmpty(obj, field);
                    verifyNotNull(obj, field);
                } else {
                    if (field.getType() == List.class) {
                        List list = (List) field.get(obj);
                        for (Object object : list) {
                            verifyParam(object);
                        }
                    } else {
                        verifyParam(field.get(obj));
                    }
                }
            }
        }
    }

    private void verifyNotEmpty(Object obj, Field field) throws IllegalAccessException {
        if (field.isAnnotationPresent(NotEmpty.class)) {
            if (field.getType() != String.class) {
                throw new ParamValidException("NotEmpty verify expect String field");
            }
            String value = (String) field.get(obj);
            if (StringUtils.isEmpty(value)) {
                NotEmpty annotation = field.getAnnotation(NotEmpty.class);
                throw new ParamValidException(annotation.message());
            }
        }
    }

    private void verifyNotNull(Object obj, Field field) throws IllegalAccessException {
        if (field.isAnnotationPresent(NotNull.class)) {
            if (field.get(obj) == null) {
                NotNull annotation = field.getAnnotation(NotNull.class);
                throw new ParamValidException(annotation.message());
            }
        }
    }


}
