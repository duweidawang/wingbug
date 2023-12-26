package com.example.dwbug.service.impl;

import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import com.example.dwbug.utils.R;
import com.example.dwbug.utils.WebUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;



@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

                R put = R.error(401, "认证失败请重新登录");
                String json = JSON.toJSONString(put);
                WebUtils.renderString(response,json);


        }
    }




