package com.crisp.saleproject.common;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */
@Slf4j
@ControllerAdvice(annotations = {RestController.class, Controller.class})//加了这些注解的都会被处理
@ResponseBody
public class GlobalExceptionHandler {

    /**
     * 处理SQLIntegrityConstraintViolationException异常
     * @param ex
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> excetionHandler(SQLIntegrityConstraintViolationException ex){

        //出现重复unique对象的情况
        if(ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            return R.error(split[2] + "已存在");
        }

        return R.error("SQLIntegrityConstraintViolationException错误: " + ex.getMessage());
    }

    @ExceptionHandler(CustomException.class)
    public R<String> customExceptionHanlder(CustomException ex){
        return R.error(ex.getMessage());
    }
}
