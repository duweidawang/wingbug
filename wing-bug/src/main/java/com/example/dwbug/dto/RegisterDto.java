package com.example.dwbug.dto;


import com.example.dwbug.validate.addUser;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


/**
 * 注册新用户
 */
@Data
public class RegisterDto {

    @Email(message = "邮箱格式错误",groups = {addUser.class})
    private String username;  //邮箱

    @NotEmpty(message = "密码不能为空",groups = {addUser.class})
    @Length(min = 6,max = 15,groups ={addUser.class})
    @Pattern(regexp = "[a-zA-Z0-9]+$",groups = {addUser.class},message = "密码只能是字母或数字")
    private String password;

    @Length(min = 1,max = 25,groups = {addUser.class})
    private String name;

    @NotEmpty(message = "邀请码不能为空",groups = {addUser.class})
    private String invitationCode;


}
