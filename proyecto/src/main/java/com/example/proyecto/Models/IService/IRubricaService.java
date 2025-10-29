package com.example.proyecto.Models.IService;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dto.RubricaDto;
import com.example.proyecto.Models.Entity.Rubrica;
import com.example.proyecto.Models.Service.IServiceGenerico;

@Service
public interface IRubricaService extends IServiceGenerico<Rubrica, Long> {
    
    Optional<Rubrica> buscarPorNombre(String nombre);
    
    List<Rubrica> listarRubrica();

    List<Rubrica> findByActividadOrder(@Param("actId") Long actId);

    // Evitar duplicados cuando se asocia a una ACTIVIDAD (estado activo)
    boolean existsByActividad_IdActividadAndVersionAndEstado(Long idActividad, String version, String estado);

    // Evitar duplicados cuando se asocia a una CATEGORÍA (estado activo)
    boolean existsByCategoriaActividad_IdCategoriaActividadAndVersionAndEstado(Long idCategoria, String version, String estado);

    // Para modificar (ignorar el mismo registro)
    boolean existsByActividad_IdActividadAndVersionAndEstadoAndIdRubricaNot(Long idActividad, String version, String estado, Long idRubrica);
    boolean existsByCategoriaActividad_IdCategoriaActividadAndVersionAndEstadoAndIdRubricaNot(Long idCategoria, String version, String estado, Long idRubrica);
    RubricaDto obtenerRubricaVigentePorCategoria(Long categoriaId);

    List<RubricaDto> obtenerRubricasActivasPorCategoria(Long categoriaId);

    long countActivasByCategoria(Long categoriaId);

    /* PARA ENTRADA UNIVERSITARIA */
    Rubrica findByCategoria(Long idCategoriaActividad);
}
