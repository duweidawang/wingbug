package com.example.dwbug.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dwbug.annotation.LogAnnotation;
import com.example.dwbug.entity.CategoryEntity;
import com.example.dwbug.service.CategoryService;
import com.example.dwbug.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * 
 *
 * @author weisheng
 * @email sunlightcs@gmail.com
 * @date 2023-08-25 13:24:35
 */
@RestController
@RequestMapping("api/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 列表
     */

    @PreAuthorize("hasAnyAuthority('bug:user:saveCategory')")
    @GetMapping("/list")

    public R list(@RequestParam Map<String, Object> params){

        IPage<CategoryEntity> page = new Page<>();
        page.setCurrent(Long.valueOf(params.get("pageNum").toString()));
        page.setSize(Long.valueOf(params.get("pageSize").toString()));
        IPage<CategoryEntity> page1 = categoryService.page(page);


        return R.ok().put("page", page1);
    }



    /**
     * 保存
     */
    @PreAuthorize("hasAnyAuthority('bug:user:saveCategory')")
    @PostMapping("/save")
    @LogAnnotation(operation = "保存分类")
    public R save(@Valid @RequestBody CategoryEntity category){
        category.setCreateTime(LocalDateTime.now());
        return categoryService.saveCategory(category);
    }

    /**
     * 修改
     */
    @PreAuthorize("hasAnyAuthority('bug:user:saveCategory')")
    @PutMapping("/update")
    @LogAnnotation(operation = "修改分类")
    public R update(@Valid @RequestBody CategoryEntity category){


		categoryService.updateById(category);
        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('bug:user:deleteCategory')")
    @LogAnnotation(operation = "删除分类")
    public R delete(@RequestParam("ids") Long ids){
		return categoryService.removeCategory(ids);

    }


    /**
     * 新增漏洞查询分类
     * @return
     */
    @GetMapping("/loophole/list")
    public R list(){
        List<CategoryEntity> list = categoryService.list();
        list.stream().forEach(cate->{
            cate.setCreateTime(null);
        });

        return R.ok().put("data",list);

    }






}
