package com.example.proyecto.Models.Service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IProgramaDao;
import com.example.proyecto.Models.Entity.Programa;

import com.example.proyecto.Models.Service.IProgramaService;

@Service
public class ProgramaServiceImpl implements IProgramaService {

    @Autowired
    private IProgramaDao programaDao;



    @Override
    public List<Programa> findAll() {
        return (List<Programa>) programaDao.findAll();
    }

    @Override
    public void save(Programa programa) {
        programaDao.save(programa);
    }

    @Override
    public Programa findOne(Long id) {
        return programaDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        programaDao.deleteById(id);
    }
    
}
