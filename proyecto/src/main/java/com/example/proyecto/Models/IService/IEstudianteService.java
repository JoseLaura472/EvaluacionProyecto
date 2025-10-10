package com.example.proyecto.Models.IService;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Entity.Estudiante;


public interface IEstudianteService {

    public List<Estudiante> findAll();
    
    public void save(Estudiante estudiante);

	public Estudiante findOne(Long id);

	public void delete(Long id);

    Page<Estudiante> findByEstadoWithPersona(String estado, Pageable pageable); // Corrige el tipo de retorno

    public List<Estudiante> listaEstudiantes(@Param("estado") String estado);

    long countByEstado(String estado);

}
