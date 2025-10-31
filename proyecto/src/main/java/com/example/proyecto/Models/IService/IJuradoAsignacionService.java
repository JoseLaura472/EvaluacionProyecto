package com.example.proyecto.Models.IService;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dto.CategoriaDto;
import com.example.proyecto.Models.Entity.Actividad;
import com.example.proyecto.Models.Entity.Jurado;
import com.example.proyecto.Models.Entity.JuradoAsignacion;
import com.example.proyecto.Models.Service.IServiceGenerico;

@Service
public interface IJuradoAsignacionService extends IServiceGenerico<JuradoAsignacion, Long> {
    List<JuradoAsignacion> listarJuradoAsignacion();
    List<Actividad> findActividadesAsignadas(@Param("idJurado") Long idJurado);
    List<Jurado> findJuradosByActividadOrdenAsignacion(@Param("actId") Long actId);
    boolean existsCategoriaAsignadaByPersona(@Param("idPersona") Long idPersona);
    List<CategoriaDto> listarCategoriasDeJuradoPorPersona(Long idPersona);

    List<Jurado> findJuradosByActividadAndCategoriaOrdenAsignacion(Long actividadId, Long categoriaId);
    JuradoAsignacion findFirstByJuradoId(Long idJurado);
    List<Actividad> findActividadesAsignadasByJurado(Long idJurado);

    List<JuradoAsignacion> findByActividad(Long idActividad);
    List<JuradoAsignacion> findByCategoriaActividad(Long idCategoriaActividad);
    List<JuradoAsignacion> findByActividadAndCategoria(Long idActividad, Long idCategoria);
    int countByActividadAndCategoria(Long idActividad, Long idCategoria);

}
