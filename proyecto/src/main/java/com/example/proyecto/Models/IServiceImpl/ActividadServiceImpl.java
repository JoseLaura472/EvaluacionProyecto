package com.example.proyecto.Models.IServiceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IActividaDao;
import com.example.proyecto.Models.Entity.Actividad;
import com.example.proyecto.Models.IService.IActividadService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActividadServiceImpl implements IActividadService {
    
    private final IActividaDao dao;

    @Override
    public List<Actividad> findAll() {
        return dao.findAll();
    }

    @Override
    public Actividad findById(Long idEntidad) {
        return dao.findById(idEntidad).orElse(null);
    }

    @Override
    public Actividad save(Actividad entidad) {
        return dao.save(entidad);
    }

    @Override
    public void deleteById(Long idEntidad) {
        dao.deleteById(idEntidad);
    }

    @Override
    public Optional<Actividad> buscarPorNombre(String nombre) {
        return dao.buscarPorNombre(nombre);
    }

    @Override
    public List<Actividad> listarActividades() {
        return dao.listarActividades();
    }

    @Override
    public List<Actividad> findAllOrderByFechaDesc() {
        return dao.findAllOrderByFechaDesc();
    }
}
