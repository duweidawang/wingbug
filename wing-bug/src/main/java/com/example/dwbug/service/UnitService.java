package com.example.dwbug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dwbug.entity.UnitEntity;
import com.example.dwbug.utils.R;


import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author weisheng
 * @email sunlightcs@gmail.com
 * @date 2023-08-25 13:24:35
 */
public interface UnitService extends IService<UnitEntity> {


    R returnList(Map<String, Object> params);

    R unitRank(Long pageSize,Long pageNum);

    R removeUnit(List<Long> deleteIds);

    List<UnitEntity> searchByNum(Long num);
}

