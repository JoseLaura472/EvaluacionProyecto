package com.example.proyecto.Models.IService;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Entity.Evaluacion;
import com.example.proyecto.Models.Service.IServiceGenerico;

public interface IEvaluacionService extends IServiceGenerico<Evaluacion, Long>{
    boolean existsByActividad_IdActividadAndInscripcion_IdInscripcionAndJurado_IdJurado(Long actId, Long inscId, Long juradoId);

    List<Evaluacion> findByActividad_IdActividad(Long actividadId);

    List<Evaluacion> findFullByActividadAndInscripcion(@Param("actId") Long actId,
                                                        @Param("inscId") Long inscId);
}
