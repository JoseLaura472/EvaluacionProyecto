package com.example.proyecto.Models.Dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Entity.Rubrica;

public interface IRubricaDao extends JpaRepository<Rubrica, Long> {
    
    @Query("SELECT r FROM Rubrica r WHERE r.nombre = ?1 AND r.estado = 'A'")
    Optional<Rubrica> buscarPorNombre(String nombre);

    @Query("SELECT r FROM Rubrica r WHERE r.estado = 'A'")
    List<Rubrica> listarRubrica();

    @Query("""
        select r from Rubrica r
        where r.actividad.idActividad = :actId
        order by r.version desc nulls last, r.idRubrica desc
    """)
    List<Rubrica> findByActividadOrder(@Param("actId") Long actId);
}
