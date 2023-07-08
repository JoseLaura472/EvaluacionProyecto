package com.example.proyecto.Models.Service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IProyectoDao;
import com.example.proyecto.Models.Entity.Proyecto;
import com.example.proyecto.Models.Service.IProyectoService;

@Service
public class ProyectoServiceImpl implements IProyectoService {

    @Autowired
    private IProyectoDao proyectoDao;

    @Override
    public List<Proyecto> findAll() {
        return (List<Proyecto>) proyectoDao.findAll();
    }

    @Override
    public void save(Proyecto proyecto) {
        proyectoDao.save(proyecto);
    }

    @Override
    public Proyecto findOne(Long id) {
        return proyectoDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        proyectoDao.deleteById(id);
    }
    
}