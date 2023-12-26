package com.example.dwbug.controller;


import com.example.dwbug.annotation.LogAnnotation;
import com.example.dwbug.entity.CommentEntity;
import com.example.dwbug.service.CommentService;

import com.example.dwbug.utils.R;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;


/**
 * 
 *
 * @author weisheng
 * @email sunlightcs@gmail.com
 * @date 2023-09-01 12:57:16
 */
@RestController
@RequestMapping("api/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;


    /**
     * 保存评论
     * @param commentEntity
     * @return
     */
    @PostMapping("/save")
    @LogAnnotation(operation = "提交评论")
    public R saveComment(@RequestBody CommentEntity commentEntity){


        return commentService.saveComment(commentEntity);


    }
    @DeleteMapping("delete")
    @PreAuthorize("hasAnyAuthority('bug:user:deleteComment')")
    @LogAnnotation(operation = "删除评论")
    public R deleteComment(@RequestParam("id") Long id){
        CommentEntity byId = commentService.getById(id);
        if(byId==null){
            return R.error("评论不存在");
        }
        else{
            commentService.removeById(id);
            return R.ok("删除成功");
        }


    }





}
