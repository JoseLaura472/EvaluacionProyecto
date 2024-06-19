package com.example.proyecto.Models.Service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IPuntajeDao;
import com.example.proyecto.Models.Entity.Pregunta;
import com.example.proyecto.Models.Entity.Puntaje;
import com.example.proyecto.Models.Service.IPuntajeService;

@Service
public class PuntajeServiceImpl implements IPuntajeService{
    
    @Autowired
    private IPuntajeDao puntajeDao;

    @Override
    public List<Puntaje> findAll() {
          return (List<Puntaje>) puntajeDao.findAll();
    }

    @Override
    public void save(Puntaje puntaje) {
        puntajeDao.save(puntaje);
    }

    @Override
    public Puntaje findOne(Long id) {
        return puntajeDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        puntajeDao.deleteById(id);
    }



    
}
