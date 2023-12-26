package com.example.dwbug.dto;

import com.fasterxml.jackson.databind.ser.std.SerializableSerializer;
import lombok.Data;

import java.io.Serializable;

@Data
public class GeneralInvitation implements Serializable {

    private Long num;
    private String note;
    private Long date;
}
