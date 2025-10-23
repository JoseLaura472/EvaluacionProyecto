package com.example.proyecto.Models.Dto;

import java.util.List;

public record RubricaDto(Long id, String nombre, String version, List<RubricaCriterioDto> criterios) {}

