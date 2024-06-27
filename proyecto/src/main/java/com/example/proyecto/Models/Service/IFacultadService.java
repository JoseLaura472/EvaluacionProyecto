package com.example.proyecto.Models.Service;

import java.util.List;

import com.example.proyecto.Models.Entity.Facultad;

public interface IFacultadService {

    public List<Facultad> findAll();

	public void save(Facultad facultad);

	public Facultad findOne(Long id);

	public void delete(Long id);
}
