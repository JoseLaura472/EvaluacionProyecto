package com.example.proyecto.Models.Dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class RubricaDto {
    private Long id;
    private String nombre;
    private String version;
    private List<RubricaCriterioDto> criterios;
}
