package com.example.proyecto.Models.Service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IJuradoDao;
import com.example.proyecto.Models.Entity.Jurado;

import com.example.proyecto.Models.Service.IJuradoService;

@Service
public class JuradoServiceImpl implements IJuradoService {
    
    @Autowired
    private IJuradoDao juradoDao;

    @Override
    public List<Jurado> findAll() {
         return (List<Jurado>) juradoDao.findAll();
    }

    @Override
    public void save(Jurado jurado) {
        juradoDao.save(jurado);
    }

    @Override
    public Jurado findOne(Long id) {
        return juradoDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        juradoDao.deleteById(id);
    }

    @Override
    public Jurado juradoPorIdPersona(Long id_persona) {
        return juradoDao.juradoPorIdPersona(id_persona);
    }
}
