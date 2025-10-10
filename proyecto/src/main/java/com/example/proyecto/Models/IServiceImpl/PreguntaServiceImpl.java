package com.example.proyecto.Models.IServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IPreguntaDao;
import com.example.proyecto.Models.Entity.Pregunta;
import com.example.proyecto.Models.IService.IPreguntaService;

@Service
public class PreguntaServiceImpl implements IPreguntaService {

    @Autowired
    private IPreguntaDao preguntaDao;

    @Override
    public List<Pregunta> findAll() {
         return (List<Pregunta>) preguntaDao.findAll();
    }

    @Override
    public void save(Pregunta pregunta) {
        preguntaDao.save(pregunta);
    }

    @Override
    public Pregunta findOne(Long id) {
        return preguntaDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        preguntaDao.deleteById(id);
    }
    
}
