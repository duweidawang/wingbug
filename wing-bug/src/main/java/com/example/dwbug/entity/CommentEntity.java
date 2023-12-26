package com.example.dwbug.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("bug_comment")
public class CommentEntity {

    @TableId
    private Long id;

    private Long userId;

    private Long loopId;

    private String comments;

    private LocalDateTime createTime;


    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String avator;





}
