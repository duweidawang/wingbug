package com.example.dwbug.service.impl;


import com.alibaba.fastjson.JSON;
import com.example.dwbug.utils.R;
import com.example.dwbug.utils.WebUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;



import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

            R put = R.error(403, "权限不足");
            String json = JSON.toJSONString(put);
            WebUtils.renderString(response,json);

        }
    }




