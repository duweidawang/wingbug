package com.example.dwbug.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("bug_record")
public class RecordEntity {
    private Long id;
    private Long userId;
    private Long recordRank;
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String name;


}
