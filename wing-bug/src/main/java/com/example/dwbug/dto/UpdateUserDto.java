package com.example.dwbug.dto;


import lombok.Data;
import org.hibernate.validator.constraints.Length;


/**
 * 更新用户信息
 */
@Data
public class UpdateUserDto {

    @Length(min = 1,max = 25)
    private String name;

    private String description;

    private String avator;
}
