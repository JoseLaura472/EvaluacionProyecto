package com.example.proyecto.Models.IServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IDocenteDao;
import com.example.proyecto.Models.Entity.Docente;
import com.example.proyecto.Models.IService.IDocenteService;

@Service
public class DocenteServiceImpl implements IDocenteService {

    @Autowired
    private IDocenteDao docenteDao;

    @Override
    public List<Docente> findAll() {
         return (List<Docente>) docenteDao.findAll();
    }

    @Override
    public void save(Docente docente) {
        docenteDao.save(docente);
    }

    @Override
    public Docente findOne(Long id) {
        return docenteDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        docenteDao.deleteById(id);
    }

    @Override
    public List<Docente> listaDocentes(String estado) {
        // TODO Auto-generated method stub
        return docenteDao.listaDocentes(estado);
    }

    


    
}
