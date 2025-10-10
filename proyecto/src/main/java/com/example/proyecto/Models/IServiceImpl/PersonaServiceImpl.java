package com.example.proyecto.Models.IServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IPersonaDao;
import com.example.proyecto.Models.Entity.Persona;
import com.example.proyecto.Models.IService.IPersonaService;

@Service
public class PersonaServiceImpl implements IPersonaService{
    @Autowired
    private IPersonaDao personaDao; 

    @Override
    public List<Persona> findAll() {
        // TODO Auto-generated method stub
        return (List<Persona>) personaDao.findAll();
    }

    @Override
    public void save(Persona Persona) {
        // TODO Auto-generated method stub
        personaDao.save(Persona);
    }

    @Override
    public Persona findOne(Long id) {
        // TODO Auto-generated method stub
        return personaDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        personaDao.deleteById(id);
    }

    @Override
    public Persona getPersonaCI(String ci) {
        // TODO Auto-generated method stub
        return personaDao.getPersonaCI(ci);
    }

    @Override
    public List<Persona> listarParticipantes() {
        return personaDao.listarParticipantes();
    }
}
