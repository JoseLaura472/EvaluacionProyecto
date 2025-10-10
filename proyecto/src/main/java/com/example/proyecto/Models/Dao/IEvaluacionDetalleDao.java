package com.example.proyecto.Models.Dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.proyecto.Models.Entity.EvaluacionDetalle;

public interface IEvaluacionDetalleDao extends JpaRepository<EvaluacionDetalle, Long> {
    
}
