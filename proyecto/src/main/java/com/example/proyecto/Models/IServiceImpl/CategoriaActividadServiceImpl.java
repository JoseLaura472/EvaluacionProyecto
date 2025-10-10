package com.example.proyecto.Models.IServiceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.ICategoriaActividadDao;
import com.example.proyecto.Models.Entity.CategoriaActividad;
import com.example.proyecto.Models.IService.ICategoriaActividadService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaActividadServiceImpl implements ICategoriaActividadService {
    
    private final ICategoriaActividadDao dao;

    @Override
    public List<CategoriaActividad> findAll() {
        return dao.findAll();
    }

    @Override
    public CategoriaActividad findById(Long idEntidad) {
        return dao.findById(idEntidad).orElse(null);
    }

    @Override
    public CategoriaActividad save(CategoriaActividad entidad) {
        return dao.save(entidad);
    }

    @Override
    public void deleteById(Long idEntidad) {
        dao.deleteById(idEntidad);
    }

    @Override
    public Optional<CategoriaActividad> buscarPorNombre(String nombre) {
        return dao.buscarPorNombre(nombre);
    }

    @Override
    public List<CategoriaActividad> listarActividades() {
        return dao.listarActividades();
    }

    @Override
    public List<CategoriaActividad> findByActividad(Long actId) {
        return dao.findByActividad(actId);
    }
}
