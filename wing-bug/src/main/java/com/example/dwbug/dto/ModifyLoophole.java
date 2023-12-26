package com.example.dwbug.dto;


import com.baomidou.mybatisplus.annotation.TableId;
import com.example.dwbug.validate.ListValue;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class ModifyLoophole {
    /**
     *
     */
    @NotNull
    private Long id;
    /**
     *
     */
    @NotEmpty
    @Length(min = 1,max = 25,message = "标题字段太长或空缺")
    private String title;
    /**
     *
     */
    @NotNull(message = "单位不能空缺")
    private Long unitId;
    /**
     *
     */
    @NotNull(message = "分类不能空缺")
    private Long categoryId;
    /**
     *
     */
    @NotEmpty
    @Length(min = 1,max = 300,message = "描述字数空缺或超过300字")
    private String description;
    /**
     * 富文本
     */
    @NotEmpty
    private String content;


    /**
     *
     */
    @NotNull

    @ListValue(vals = {0,1},message = "必须为0或1")
    private Long visible;
    /**
     *
     */

    private Long loopRank;
    /**
     *
     */
    @NotNull
    @ListValue(vals = {1,2,3,4},message = "必须为1-4")
    private Long grade;


    private String comments;
    /**
     *
     */
    private String status;

}
