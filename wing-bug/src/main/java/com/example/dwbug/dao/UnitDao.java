package com.example.dwbug.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dwbug.entity.UnitEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 
 * 
 * @author weisheng
 * @email sunlightcs@gmail.com
 * @date 2023-08-25 13:24:35
 */
@Mapper
public interface UnitDao extends BaseMapper<UnitEntity> {

    List<UnitEntity> searchByNum(Long num);
	
}
