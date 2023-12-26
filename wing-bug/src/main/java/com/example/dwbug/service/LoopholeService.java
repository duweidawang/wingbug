package com.example.dwbug.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dwbug.dto.CreateLoopholeDto;
import com.example.dwbug.dto.ModifyLoophole;
import com.example.dwbug.entity.LoopholeEntity;
import com.example.dwbug.utils.R;


import java.util.Map;

/**
 * 
 *
 * @author weisheng
 * @email sunlightcs@gmail.com
 * @date 2023-08-25 13:24:36
 */
public interface LoopholeService extends IService<LoopholeEntity> {


    R saveLoophole(CreateLoopholeDto createLoopholeDto);

    R queryList(Long pageNum, Long pageSize,Map<String, Object> map);

    R updateLoop(CreateLoopholeDto createLoopholeDto);

    R queryProcess(Long pageNum,Long pageSize);

    R process(LoopholeEntity loopholeEntity);

    R returnUserLoop(Long pageSize, Long pageNum);

    R loopDetail(Long id);

    void removeLoop(Long ids);

     R editloophole(ModifyLoophole modifyLoophole);
}

