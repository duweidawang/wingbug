package com.example.dwbug.dto;

import com.example.dwbug.validate.ListValue;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@Data
public class UpdateUserBymanagerDto {


    private Long id;
    @Length(min = 1,max = 25)
    private String name;
    @Email
    private String username;

    private String password;
    @NotNull
    @ListValue(vals = {0,1,2},message = "角色必须为0或1或2")
    private Long role;
}
