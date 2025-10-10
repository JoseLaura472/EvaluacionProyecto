package com.example.proyecto.Models.IServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IFacultadDao;
import com.example.proyecto.Models.Entity.Facultad;
import com.example.proyecto.Models.IService.IFacultadService;

@Service
public class FacultadServiceImpl implements IFacultadService{

    @Autowired
    private IFacultadDao facultadDao;

    @Override
    public List<Facultad> findAll() {
        // TODO Auto-generated method stub
        return (List<Facultad>) facultadDao.findAll();
    }

    @Override
    public void save(Facultad facultad) {
        // TODO Auto-generated method stub
        facultadDao.save(facultad);
    }

    @Override
    public Facultad findOne(Long id) {
        // TODO Auto-generated method stub
        return facultadDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        facultadDao.deleteById(id);
    }
    
}
