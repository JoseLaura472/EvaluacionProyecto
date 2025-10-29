package com.example.proyecto.Models.IService;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Entity.Actividad;
import com.example.proyecto.Models.Entity.Jurado;


public interface IJuradoService {
    
    public List<Jurado> findAll();
    
    public void save(Jurado jurado);

	public Jurado findOne(Long id);

	public void delete(Long id);

    public Jurado juradoPorIdPersona(Long idPersona);
    List<Jurado> findByProyectoId(@Param("proyectoId") Long proyectoId);

    Jurado findByNombreCompleto(@Param("nombreCompleto") String nombreCompleto);

    List<Jurado> listarParticipantes();

    boolean existsByPersona_IdPersona(Long idPersona);

    Optional<Jurado> findActivoByPersonaId(@Param("idPersona") Long idPersona);

    List<Actividad> findActividadesByJurado(@Param("juradoId") Long juradoId);

    Jurado findByPersonaId(Long idPersona);
}