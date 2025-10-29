package com.example.proyecto.Models.Dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Entity.CategoriaActividad;

public interface ICategoriaActividadDao extends JpaRepository<CategoriaActividad, Long> {
    
    @Query("SELECT ca FROM CategoriaActividad ca WHERE ca.nombre = ?1 AND ca.estado = 'A'")
    Optional<CategoriaActividad> buscarPorNombre(String nombre);

    @Query("SELECT ca FROM CategoriaActividad ca WHERE ca.estado = 'A'")
    List<CategoriaActividad> listarActividades();

    @Query("select c from CategoriaActividad c where c.actividad.idActividad = :actId order by c.nombre asc")
    List<CategoriaActividad> findByActividad(@Param("actId") Long actId);

    /* para enetrada universitaria */
    List<CategoriaActividad> findByActividadIdActividadAndFase(Long idActividad, String fase);
}
