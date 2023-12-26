package com.example.dwbug.dto;

import lombok.Data;

import java.io.Serializable;


@Data
public class UpdateInvation implements Serializable {

    private Long id;
    private Long date;
    private Long datestate;
    private Long state;
}
