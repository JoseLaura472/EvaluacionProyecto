package com.example.proyecto.Models.IService;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Entity.Rubrica;
import com.example.proyecto.Models.Service.IServiceGenerico;

@Service
public interface IRubricaService extends IServiceGenerico<Rubrica, Long> {
    
    Optional<Rubrica> buscarPorNombre(String nombre);
    
    List<Rubrica> listarRubrica();

    List<Rubrica> findByActividadOrder(@Param("actId") Long actId);
}
