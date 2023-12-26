package com.example.dwbug.dto;


import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.parameters.P;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


/**
 *改动密码
 */
@Data
public class ChangePasswordDto {

    @NotNull
    private String originPassword;
    @NotNull
    @Length(min = 6,max = 15)
    @Pattern(regexp = "[a-zA-Z0-9]+$",message = "密码只能为字母或数字")
    private String password;
    @NotNull
    private String code;
}
