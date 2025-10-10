package com.example.proyecto.Models.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class SimpleResponse {
    private boolean ok; private String msg;
}
