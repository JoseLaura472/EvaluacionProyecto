package com.example.proyecto.Models.IService;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Entity.Persona;

public interface IPersonaService {
    
    public List<Persona> findAll();

	public void save(Persona persona);

	public Persona findOne(Long id);

	public void delete(Long id);
	
	public Persona getPersonaCI(String ci); 

	List<Persona> listarPersona(@Param("estado") String estado);
	Persona findByNombres(String nombres);
}
