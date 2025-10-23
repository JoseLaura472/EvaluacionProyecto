package com.example.proyecto.Models.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Entity.EvaluacionDetalle;

public interface IEvaluacionDetalleDao extends JpaRepository<EvaluacionDetalle, Long> {
    @Modifying
    @Query("delete from EvaluacionDetalle d where d.evaluacion.idEvaluacion = :idEval")
    void deleteByEvaluacionId(@Param("idEval") Long idEvaluacion);
}
