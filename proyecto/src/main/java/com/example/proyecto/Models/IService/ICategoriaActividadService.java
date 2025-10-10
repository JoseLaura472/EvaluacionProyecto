package com.example.proyecto.Models.IService;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Entity.CategoriaActividad;
import com.example.proyecto.Models.Service.IServiceGenerico;

@Service
public interface ICategoriaActividadService extends IServiceGenerico<CategoriaActividad, Long>{
    Optional<CategoriaActividad> buscarPorNombre(String nombre);
    List<CategoriaActividad> listarActividades();
    List<CategoriaActividad> findByActividad(@Param("actId") Long actId);
}
