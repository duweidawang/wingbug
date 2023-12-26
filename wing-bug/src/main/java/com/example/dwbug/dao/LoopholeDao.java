package com.example.dwbug.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dwbug.dto.LoopSeatchDto;
import com.example.dwbug.entity.LoopholeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * 
 * @author weisheng
 * @email sunlightcs@gmail.com
 * @date 2023-08-25 13:24:36
 */
@Mapper
public interface LoopholeDao extends BaseMapper<LoopholeEntity> {

    Integer countList(LoopSeatchDto loopSeatchDto);
    List<LoopholeEntity> queryList(LoopSeatchDto loopSeatchDto);
    //普通用户
    List<LoopholeEntity> queryCommonList(LoopSeatchDto loopSeatchDto);
	Integer count(LoopSeatchDto loopSeatchDto);
}
