package com.example.dwbug.exception;



import com.example.dwbug.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局异常处理
 */
@Slf4j
@ResponseBody
@ControllerAdvice(basePackages = "com.example.dwbug.controller")
public class ExceptionControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handValidException(MethodArgumentNotValidException e){

//        log.error("数据校验异常，异常是"+e.getMessage()+"类型为"+e.getClass());
//
//        BindingResult bindingResult = e.getBindingResult();
//        Map<Object,Object> map = new HashMap<>();
//        bindingResult.getFieldErrors().forEach(item->{
//            //获取错误的属性名
//            String field = item.getField();
//            //获取错误的消息
//            String defaultMessage = item.getDefaultMessage();
//            map.put(field, defaultMessage);
//        });
//
//        return R.error(BizCode.VALID_EXCEPTION.getCode(), BizCode.VALID_EXCEPTION.getMsg()).put("data", map);
        log.error("数据校验异常，异常是"+e.getMessage()+"类型为"+e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        List<String> list = new ArrayList<>();
        bindingResult.getFieldErrors().forEach(item->{
//            //获取错误的属性名
            String field = item.getField();
//            //获取错误的消息
            String defaultMessage = item.getDefaultMessage();
            list.add(field+defaultMessage);
        });
        return R.error(BizCode.VALID_EXCEPTION.getCode(), list.get(0));
    }


    /**
     * 用户名已存在异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = SQLIntegrityConstraintViolationException.class)
    public R sQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e){
        log.error("，异常是"+e.getMessage()+"类型为"+e.getClass());
        return R.error(500,"用户名已存在");
    }
    @ExceptionHandler(value = DuplicateKeyException.class)
    public R DuplicateKeyException(DuplicateKeyException e){
        log.error("，异常是"+e.getMessage()+"类型为"+e.getClass());
        return R.error(500,"用户名已存在");
    }

    @ExceptionHandler(value = InternalAuthenticationServiceException.class)
    public R InternalAuthenticationServiceException(InternalAuthenticationServiceException e){
        log.error("数据校验异常，异常是"+e.getMessage()+"类型为"+e.getClass());
        return R.error(10001,e.getMessage());
    }


    @ExceptionHandler(value = AccessDeniedException.class)
    public R AccessDeniedException(AccessDeniedException e){
        log.error("异常是"+e.getMessage()+"类型为"+e.getClass());

        return R.error(403, "权限不足");
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public R MissingServletRequestParameterException(MissingServletRequestParameterException e){
        log.error("，异常是"+e.getMessage()+"类型为"+e.getClass());

        return R.error(402, "请求信息错误");
    }
    @ExceptionHandler(value = BadCredentialsException.class)
    public R MissingServletRequestParameterException(BadCredentialsException e){
        log.error("，异常是"+e.getMessage()+"类型为"+e.getClass());

        return R.error(500, "密码错误");
    }



    @ExceptionHandler(value = Throwable.class)
    public R handException(Throwable e){
        log.error("异常类型："+e.getClass()+"异常信息："+e.getMessage());

        return R.error("未知异常，请联系超管");
    }




}
