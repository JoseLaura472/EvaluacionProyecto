package com.example.proyecto.Models.Service;

import java.util.List;

import com.example.proyecto.Models.Entity.Docente;


public interface IDocenteService {
    
    public List<Docente> findAll();
    
    public void save(Docente docente);

	public Docente findOne(Long id);

	public void delete(Long id);
}
