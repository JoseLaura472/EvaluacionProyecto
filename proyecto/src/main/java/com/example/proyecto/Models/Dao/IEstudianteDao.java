package com.example.proyecto.Models.Dao;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Entity.Estudiante;

public interface IEstudianteDao extends CrudRepository<Estudiante, Long>{
    
    @Query("SELECT e FROM Estudiante e JOIN FETCH e.persona p WHERE e.estado = :estado")
    List<Estudiante> findByEstadoWithPersona(@Param("estado") String estado, Pageable pageable);

    @Query("SELECT e FROM Estudiante e JOIN FETCH e.persona p WHERE e.estado = :estado")
    public List<Estudiante> listaEstudiantes(@Param("estado") String estado);

    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.estado = :estado")
    long countByEstado(@Param("estado") String estado);
}
