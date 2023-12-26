package com.example.dwbug.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dwbug.entity.RecordEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RecordDao extends BaseMapper<RecordEntity> {
    boolean insert(Map map);


}
