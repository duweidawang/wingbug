package com.example.dwbug.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.dwbug.dao.CategoryDao;
import com.example.dwbug.entity.CategoryEntity;
import com.example.dwbug.entity.LoopholeEntity;
import com.example.dwbug.service.CategoryService;
import com.example.dwbug.service.LoopholeService;
import com.example.dwbug.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;




@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    @Lazy
    LoopholeService loopholeService;

    @Autowired
    CategoryDao categoryDao;
    /**
     * 删除分类
     * @param ids
     * @return
     */
    @Override
    public R removeCategory(Long ids) {
        QueryWrapper<LoopholeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_id",ids);
        int count = loopholeService.count(queryWrapper);
        if(count!=0){
            return R.error(503,"有漏洞关联分类,无法删除");
        }

        removeById(ids);
        return R.ok("删除成功");

    }

    /**
     * 新增分类
     * @param category1
     * @return
     */
    @Override
    public R saveCategory(CategoryEntity category1) {
        categoryDao.insert(category1);
        return R.ok("新增成功");

    }
}