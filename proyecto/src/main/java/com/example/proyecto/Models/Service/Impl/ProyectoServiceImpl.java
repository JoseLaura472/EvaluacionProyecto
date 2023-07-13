package com.example.proyecto.Models.Service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.proyecto.Models.Dao.IProyectoDao;
import com.example.proyecto.Models.Entity.Proyecto;
import com.example.proyecto.Models.Service.IProyectoService;

@Service
@Transactional
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

    @Override
    public List<Proyecto> findByJuradoId(Long juradoId) {
       return (List<Proyecto>) proyectoDao.findByJuradoId(juradoId);
    }

    @Override
    public List<Proyecto> Primerlugar() {
        // TODO Auto-generated method stub
        return (List<Proyecto>) proyectoDao.Primerlugar();
    }

    @Override
    public List<Proyecto> Segundolugar() {
        // TODO Auto-generated method stub
        return (List<Proyecto>) proyectoDao.Segundolugar();
    }

    @Override
    public List<Proyecto> Tercerlugar() {
        // TODO Auto-generated method stub
        return (List<Proyecto>) proyectoDao.Tercerlugar();
    }

    @Override
    public List<Proyecto> proyectosEvaluados() {
       return (List<Proyecto>) proyectoDao.proyectosEvaluados();
    }

    @Override
    public List<Proyecto> proyectosRanking() {
        // TODO Auto-generated method stub
        return (List<Proyecto>) proyectoDao.proyectosRanking();
    }
    
}
