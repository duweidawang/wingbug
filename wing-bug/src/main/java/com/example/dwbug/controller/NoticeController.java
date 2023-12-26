package com.example.dwbug.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dwbug.annotation.LogAnnotation;
import com.example.dwbug.entity.NoticeEntity;
import com.example.dwbug.service.NoticeService;
import com.example.dwbug.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;


/**
 * 
 *
 * @author weisheng
 * @email sunlightcs@gmail.com
 * @date 2023-08-25 13:24:35
 */
@RestController
@RequestMapping("api/notice")
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

    /**
     * 列表
     */
    @GetMapping("/list")
    public R list(@RequestParam("pageSize") int pageSize,@RequestParam("pageNum") int pageNum
    ,@RequestParam(value = "title",required = false) String title,@RequestParam(value = "startTime",required = false) String startTime
    ,@RequestParam(value = "endTime",required = false) String endTime){
        IPage<NoticeEntity> page = new Page<>();
        page.setSize(pageSize);
        page.setCurrent(pageNum);
        QueryWrapper<NoticeEntity> queryWrapper = new QueryWrapper<>();
        if(StringUtils.hasText(title)){
            queryWrapper.like("title",title);
        }
        if(StringUtils.hasText(startTime) && StringUtils.hasText(endTime)){
            String[] split = startTime.split("-");
            String[] split1 = endTime.split("-");
            String dateKey = split[0]+"-"+split[1]+"-"+split[2]+" 00:00:00";
            String dateKey1 = split1[0]+"-"+split1[1]+"-"+split1[2]+" 23:59:59";
            LocalDateTime localDate = LocalDateTime.parse(dateKey, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime localDate1 = LocalDateTime.parse(dateKey1, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryWrapper.ge("create_time",localDate).le("create_time",localDate1);

        }
        queryWrapper.orderByDesc("create_time");
        IPage<NoticeEntity> page1 = noticeService.page(page, queryWrapper);
        return R.ok().put("data",page1);

    }


    /**
     * 保存
     */
    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('bug:user:Notice')")
    @LogAnnotation(operation = "添加一个公告")
    public R save(@Valid  @RequestBody NoticeEntity notice){

        notice.setCreateTime(LocalDateTime.now());
		noticeService.save(notice);
        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('bug:user:Notice')")
    @LogAnnotation(operation = "修改公告")
    public R update(@Valid @RequestBody NoticeEntity notice){

		noticeService.updateById(notice);
        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('bug:user:Notice')")
    @LogAnnotation(operation = "删除公告")
    public R delete(@RequestParam("ids")  Long ids){
		noticeService.removeById(ids);
        return R.ok();
    }

}
