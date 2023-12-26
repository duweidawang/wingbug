package com.example.dwbug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dwbug.entity.CommentEntity;
import com.example.dwbug.utils.R;


/**
 * 
 *
 * @author weisheng
 * @email sunlightcs@gmail.com
 * @date 2023-09-01 12:57:16
 */
public interface CommentService extends IService<CommentEntity> {


    R saveComment(CommentEntity commentEntity);
}

