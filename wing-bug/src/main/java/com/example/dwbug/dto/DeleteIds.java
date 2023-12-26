package com.example.dwbug.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DeleteIds implements Serializable {

    private List<Long> ids;
}
