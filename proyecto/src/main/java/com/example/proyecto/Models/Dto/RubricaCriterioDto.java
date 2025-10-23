package com.example.proyecto.Models.Dto;

public record RubricaCriterioDto(
    Long id, 
    String nombre, 
    Integer porcentaje, 
    Integer maxPuntos, 
    String descripcion) {}
