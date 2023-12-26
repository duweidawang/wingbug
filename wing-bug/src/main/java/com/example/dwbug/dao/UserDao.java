package com.example.dwbug.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dwbug.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author weisheng
 * @email sunlightcs@gmail.com
 * @date 2023-08-25 13:24:36
 */
@Mapper
public interface UserDao extends BaseMapper<UserEntity> {

    int queryRole(Long userId);
    List<Long> queryMenuId(Long roleId);

    String  queryMenuList(long menuId);

    List<UserEntity> searchByNum(Long num);

}
