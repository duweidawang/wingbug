package com.example.dwbug.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.dwbug.dao.UserDao;
import com.example.dwbug.entity.LoginUser;
import com.example.dwbug.entity.UserEntity;
import com.example.dwbug.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserService userService;
    @Autowired
    UserDao userDao;
    @Autowired
    StringRedisTemplate stringRedisTemplate;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //根据用户名查询是否存在用户
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        UserEntity userEntity = userService.getOne(queryWrapper);

        if(Objects.isNull(userEntity)){
            throw new RuntimeException("用户名错误");
        }


        List<String> permissions = new ArrayList<>();
        permissions = getPermissions(userEntity.getId());


        //封装成UserDetails对象返回
        return new LoginUser(userEntity,permissions);


    }

    /**
     * 查询权限列表
     * @param userId
     * @return
     */
    public List<String> getPermissions(long userId){
        int role = userDao.queryRole(userId);
        List<Long> roleId = userDao.queryMenuId((long)role);
        List<String> list = new ArrayList<>();
        for(long i : roleId){
            String s = userDao.queryMenuList(i);
            list.add(s);
        }
       return list;

    }
}
