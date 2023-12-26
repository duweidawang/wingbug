package com.example.dwbug.utils;

import cn.hutool.core.stream.StreamUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.example.dwbug.dao.UserDao;
import com.example.dwbug.entity.LoginUser;
import com.example.dwbug.entity.UserEntity;

import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private  StringRedisTemplate stringRedisTemplate;
    public JwtAuthenticationTokenFilter(StringRedisTemplate stringRedisTemplate){
      this.stringRedisTemplate=stringRedisTemplate;

    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

            //获取token
            String token = request.getHeader("token");
            if (!StringUtils.hasText(token)) {
                //放行
                R put = R.error(411, "token不存在");
                String json = JSON.toJSONString(put);
                WebUtils.renderString(response,json);
                return;

            }
            //解析token
            String userid;
            try {
               jwtutils.checkToken(token);
               userid = jwtutils.getMemberIdByJwtToken(token);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("token非法");
            }
            //从redis中获取用户信息
            String redisKey = "LOGIN_USER_KEY:" + userid;

            String redisString = stringRedisTemplate.opsForValue().get(redisKey);
            if(!StringUtils.hasText(redisString)){
                R put = R.error(410, "token过期");
                String json = JSON.toJSONString(put);
                WebUtils.renderString(response,json);
                return;
            }

            LoginUser loginUser = JSONUtil.toBean(redisString, LoginUser.class);
            if(Objects.isNull(loginUser)){
                throw new RuntimeException("用户未登录");
            }
            stringRedisTemplate.expire(redisKey, 60L, TimeUnit.MINUTES);

            //存入SecurityContextHolder
            //TODO 获取权限信息封装到Authentication中
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginUser,null, loginUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            //放行
            filterChain.doFilter(request, response);
        }

    }

