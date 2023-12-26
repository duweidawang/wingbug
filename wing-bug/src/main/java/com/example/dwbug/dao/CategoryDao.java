package com.example.dwbug.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dwbug.entity.CategoryEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * 
 * @author weisheng
 * @email sunlightcs@gmail.com
 * @date 2023-08-25 13:24:35
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
