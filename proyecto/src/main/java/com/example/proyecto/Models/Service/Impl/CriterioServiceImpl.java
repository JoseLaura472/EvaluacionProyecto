package com.example.proyecto.Models.Service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.ICriterioDao;
import com.example.proyecto.Models.Entity.Criterio;

import com.example.proyecto.Models.Service.ICriterioService;

@Service
public class CriterioServiceImpl implements ICriterioService {

    @Autowired
    private ICriterioDao criterioDao;

    @Override
    public List<Criterio> findAll() {
       return (List<Criterio>) criterioDao.findAll();
    }

    @Override
    public void save(Criterio criterio) {
        criterioDao.save(criterio);
    }

    @Override
    public Criterio findOne(Long id) {
        return criterioDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        criterioDao.deleteById(id);
    }
    
}
