package com.example.proyecto.Models.Service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IPonderacionDao;
import com.example.proyecto.Models.Entity.Docente;
import com.example.proyecto.Models.Entity.Ponderacion;
import com.example.proyecto.Models.Service.IPonderacionService;

@Service
public class PonderacionServiceImpl implements IPonderacionService{

    @Autowired
    private IPonderacionDao ponderacionDao;
    @Override
    public List<Ponderacion> findAll() {
        // TODO Auto-generated method stub
        return (List<Ponderacion>) ponderacionDao.findAll();
    }

    @Override
    public void save(Ponderacion ponderacion) {
        // TODO Auto-generated method stub
        ponderacionDao.save(ponderacion);
    }

    @Override
    public Ponderacion findOne(Long id) {
        // TODO Auto-generated method stub
        return ponderacionDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        ponderacionDao.deleteById(id);
    }
    
}
