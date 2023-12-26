package com.example.dwbug.exception;

import com.example.dwbug.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ResponseBody
@ControllerAdvice
public class GlobalException {



    /**
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = FileSizeLimitExceededException.class)
    public R fileSize(FileSizeLimitExceededException e){
        log.error("异常类型："+e.getClass()+"异常信息："+e.getMessage());
        return R.error("文件上传内容过大");
    }



}
