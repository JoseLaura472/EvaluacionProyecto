package com.example.proyecto.Models.IService;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Entity.Actividad;
import com.example.proyecto.Models.Service.IServiceGenerico;

@Service
public interface IActividadService extends IServiceGenerico<Actividad, Long>{
    Optional<Actividad> buscarPorNombre(String nombre);
    List<Actividad> listarActividades();
    List<Actividad> findAllOrderByFechaDesc();
}
