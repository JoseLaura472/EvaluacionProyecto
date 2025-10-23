package com.example.proyecto.Models.Dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Entity.Actividad;
import com.example.proyecto.Models.Entity.Jurado;
public interface IJuradoDao  extends CrudRepository<Jurado, Long>{

    @Query(value = "Select * From jurado where idPersona = ?1", nativeQuery = true)
    public Jurado juradoPorIdPersona(Long idPersona);

    @Query("SELECT j FROM Jurado j JOIN j.proyecto p WHERE p.id = :proyectoId and j.estado != 'X' ")
    List<Jurado> findByProyectoId(@Param("proyectoId") Long proyectoId);

    @Query("SELECT j FROM Jurado j " +
       "WHERE CONCAT(j.persona.nombres, ' ', j.persona.paterno, ' ', j.persona.materno) = :nombreCompleto")
    Jurado findByNombreCompleto(@Param("nombreCompleto") String nombreCompleto);

    @Query("SELECT p FROM Jurado p WHERE p.estado = 'A'")
    List<Jurado> listarParticipantes();

    boolean existsByPersona_IdPersona(Long idPersona);

    @Query("select j from Jurado j join fetch j.persona p where p.idPersona = :idPersona and j.estado <> 'ELIMINADO'")
    Optional<Jurado> findActivoByPersonaId(@Param("idPersona") Long idPersona);

    @Query("""
        select a
        from JuradoAsignacion ja
        join ja.actividad a
        where ja.jurado.idJurado = :juradoId
        order by a.fecha desc, a.nombre asc
    """)
    List<Actividad> findActividadesByJurado(@Param("juradoId") Long juradoId);

    Optional<Jurado> findFirstByPersona_IdPersonaAndEstado(Long idPersona, String estado);
}