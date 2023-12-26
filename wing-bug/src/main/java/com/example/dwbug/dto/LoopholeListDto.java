package com.example.dwbug.dto;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoopholeListDto {

    private Long id;

    private String title;

    private Long grade;

    private String status;

    private Long loopRank;

    private LocalDateTime updateTime;


    private String authorName;

    private String unitName;

    private Long author;
}
