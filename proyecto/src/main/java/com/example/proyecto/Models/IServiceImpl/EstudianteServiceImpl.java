package com.example.proyecto.Models.IServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IEstudianteDao;
import com.example.proyecto.Models.Entity.Estudiante;
import com.example.proyecto.Models.IService.IEstudianteService;



@Service
public class EstudianteServiceImpl implements IEstudianteService {

    @Autowired
    private IEstudianteDao estudianteDao;

    @Override
    public List<Estudiante> findAll() {
         return (List<Estudiante>) estudianteDao.findAll();
    }

    @Override
    public void save(Estudiante estudiante) {
        estudianteDao.save(estudiante);
    }

    @Override
    public Estudiante findOne(Long id) {
        return estudianteDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        estudianteDao.deleteById(id);
    }

    @Override
    public Page<Estudiante> findByEstadoWithPersona(String estado, Pageable pageable) {
        List<Estudiante> estudiantes = estudianteDao.findByEstadoWithPersona(estado, pageable);
        long total = estudianteDao.countByEstado(estado);
        return new PageImpl<>(estudiantes, pageable, total);
    }

    @Override
    public long countByEstado(String estado) {
        return estudianteDao.countByEstado(estado);
    }

    @Override
    public List<Estudiante> listaEstudiantes(String estado) {
        // TODO Auto-generated method stub
        return estudianteDao.listaEstudiantes(estado);
    }

   
    
}
