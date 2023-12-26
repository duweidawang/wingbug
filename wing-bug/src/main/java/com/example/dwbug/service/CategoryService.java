package com.example.dwbug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dwbug.entity.CategoryEntity;
import com.example.dwbug.utils.R;


/**
 * 
 *
 * @author weisheng
 * @email sunlightcs@gmail.com
 * @date 2023-08-25 13:24:35
 */
public interface CategoryService extends IService<CategoryEntity> {


    R removeCategory(Long ids);

    R saveCategory(CategoryEntity category1);
}

