package com.example.dwbug.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dwbug.dto.ChangePasswordDto;
import com.example.dwbug.dto.RegisterDto;
import com.example.dwbug.dto.UpdateUserBymanagerDto;
import com.example.dwbug.entity.UserEntity;
import com.example.dwbug.utils.R;


import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author weisheng
 * @email sunlightcs@gmail.com
 * @date 2023-08-25 13:24:36
 */
public interface UserService extends IService<UserEntity> {


    R login(UserEntity userEntity);

    R logout();

    R userMsg(Long id);

    R updateUser(UserEntity userEntity);

    R createManagetUser(UserEntity userEntity);

    R invitationCode();

    R registerUser(RegisterDto registerDto);

    R getCode();

    R change(ChangePasswordDto changePasswordDto);

    R monthRank(Integer year, Integer month,Long pageSize,Long pageNum);

    R yearRank(Integer year,Long pageSize,Long pageNum);

    R userRank(long pageNum, Long pageSize);

    IPage<UserEntity> userList(String name,String email, Long pageSize, Long pageNum);

    R deleteUser(List<Long> id);

    R updateByManger(UpdateUserBymanagerDto updateUserBymanagerDto);

    List<UserEntity> serachUsers(Long num);
}

