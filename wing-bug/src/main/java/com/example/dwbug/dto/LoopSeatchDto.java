package com.example.dwbug.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class LoopSeatchDto {

    private String title;
    private   Long gradeStart ;
    private   Long gradeEnd ;
    private   Long grade;
    private   LocalDateTime startTime;
    private   LocalDateTime endTime ;
    private   List<Long> authorId;
    private  Long index;
    private Long pageSize;
    private   List<Long> unitId;
    private Long visible;

}
