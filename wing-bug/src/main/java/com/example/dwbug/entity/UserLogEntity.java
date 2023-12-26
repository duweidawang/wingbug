package com.example.dwbug.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserLogEntity implements Serializable {

    private String userName;

    private String operation;

    private LocalDateTime timestamp;

    private StringBuffer details;


}
