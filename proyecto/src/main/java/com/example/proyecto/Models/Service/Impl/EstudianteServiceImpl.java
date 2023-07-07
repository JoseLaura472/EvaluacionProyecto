package com.example.proyecto.Models.Service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IEstudianteDao;

import com.example.proyecto.Models.Entity.Estudiante;
import com.example.proyecto.Models.Service.IEstudianteService;



@Service
public class EstudianteServiceImpl implements IEstudianteService {

    @Autowired
    private IEstudianteDao estudianteDao;

    @Override
    public List<Estudiante> findAll() {
         return (List<Estudiante>) estudianteDao.findAll();
    }

    @Override
    public void save(Estudiante estudiante) {
        estudianteDao.save(estudiante);
    }

    @Override
    public Estudiante findOne(Long id) {
        return estudianteDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        estudianteDao.deleteById(id);
    }
    
}
