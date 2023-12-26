package com.example.dwbug.service.impl;

import com.example.dwbug.dao.NoticeDao;
import com.example.dwbug.entity.NoticeEntity;
import com.example.dwbug.service.NoticeService;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


@Service("noticeService")
public class NoticeServiceImpl extends ServiceImpl<NoticeDao, NoticeEntity> implements NoticeService {



}