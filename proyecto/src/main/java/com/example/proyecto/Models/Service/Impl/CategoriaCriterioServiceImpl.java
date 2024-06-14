package com.example.proyecto.Models.Service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.ICategoriaCriterioDao;
import com.example.proyecto.Models.Entity.CategoriaCriterio;
import com.example.proyecto.Models.Entity.Criterio;
import com.example.proyecto.Models.Service.ICategoriaCriterioService;


@Service
public class CategoriaCriterioServiceImpl implements ICategoriaCriterioService {


    @Autowired
    private ICategoriaCriterioDao categoriaCriterioDao;

    @Override
    public List<CategoriaCriterio> findAll() {
        return (List<CategoriaCriterio>) categoriaCriterioDao.findAll();
    }

    @Override
    public void save(CategoriaCriterio categoriaCriterio) {
        categoriaCriterioDao.save(categoriaCriterio);
    }

    @Override
    public CategoriaCriterio findOne(Long id) {
        return categoriaCriterioDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        categoriaCriterioDao.deleteById(id);
    }

    @Override
    public List<CategoriaCriterio> obtenerPonderacionesPorProyecto(Long id_proyecto) {
        
        return categoriaCriterioDao.obtenerPonderacionesPorProyecto(id_proyecto);
    }

}
