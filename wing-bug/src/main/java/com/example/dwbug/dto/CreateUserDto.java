package com.example.dwbug.dto;


import com.example.dwbug.validate.ListValue;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


/**
 * 创建新用户
 */
@Data
public class CreateUserDto {


    @Email
    private String username;

    @NotEmpty
    @Length(min = 6,max = 15)
    @Pattern(regexp = "[a-zA-Z0-9]+$",message = "密码只能为字母或数字")
    private String password;

    @NotNull
    @ListValue(vals = {1,2},message = "角色必须为为1或2")
    private Long role;

    @Length(min = 1,max = 25,message = "用户名长度过长")
    private String name;
}
