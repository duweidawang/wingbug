package com.example.dwbug.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoopAuthorDto {



    private Long id;
    /**
     *
     */
    private String title;
    /**
     *
     */
    private Long unitId;
    /**
     *
     */
    private Long categoryId;
    /**
     *
     */
    private String description;
    /**
     * 富文本
     */
    private String content;

    private Long author;
    /**
     *
     */
    private Long visible;
    /**
     *
     */
    private Long loopRank;
    /**
     *
     */
    private Long grade;

    private String comments;
    /**
     *
     */
    private String status;
    /**
     *
     */
    private LocalDateTime createTime;
    /**
     *
     */
    private LocalDateTime updateTime;

    private String authorName;
}
