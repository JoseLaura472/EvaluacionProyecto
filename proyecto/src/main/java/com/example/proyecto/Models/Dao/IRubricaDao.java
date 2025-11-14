package com.example.proyecto.Models.Dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Entity.Rubrica;

public interface IRubricaDao extends JpaRepository<Rubrica, Long> {
    
    @Query("SELECT r FROM Rubrica r WHERE r.nombre = ?1 AND r.estado = 'A'")
    Optional<Rubrica> buscarPorNombre(String nombre);

    @Query("SELECT r FROM Rubrica r WHERE r.estado = 'A'")
    List<Rubrica> listarRubrica();

    @Query("""
        select r from Rubrica r
        where r.actividad.idActividad = :actId
        order by r.version desc nulls last, r.idRubrica desc
    """)
    List<Rubrica> findByActividadOrder(@Param("actId") Long actId);

    // Evitar duplicados cuando se asocia a una ACTIVIDAD (estado activo)
    boolean existsByActividad_IdActividadAndVersionAndEstado(Long idActividad, String version, String estado);

    // Evitar duplicados cuando se asocia a una CATEGORÍA (estado activo)
    boolean existsByCategoriaActividad_IdCategoriaActividadAndVersionAndEstado(Long idCategoria, String version, String estado);

    // Para modificar (ignorar el mismo registro)
    boolean existsByActividad_IdActividadAndVersionAndEstadoAndIdRubricaNot(Long idActividad, String version, String estado, Long idRubrica);
    boolean existsByCategoriaActividad_IdCategoriaActividadAndVersionAndEstadoAndIdRubricaNot(Long idCategoria, String version, String estado, Long idRubrica);

    @Query("""
        select r
        from Rubrica r
        left join fetch r.categoriaActividad c
        where r.estado='A' and c.idCategoriaActividad = :categoriaId
        order by r.idRubrica asc
    """)
    List<Rubrica> findActivasPorCategoriaOrderDesc(@Param("categoriaId") Long categoriaId);

    @Query("""
        select count(r) 
        from Rubrica r 
        where r.categoriaActividad.idCategoriaActividad = :categoriaId 
          and coalesce(r.estado,'A') = 'A'
    """)
    long countActivasByCategoria(@Param("categoriaId") Long categoriaId);

    /* PARA ENTRADA UNIVERSITARIA */
    // Obtener la rúbrica por id de categoría (asumiendo una versión activa)

    List<Rubrica> findByCategoriaActividadIdCategoriaActividad(Long idCategoriaActividad);

    Rubrica findByActividadIdActividadAndCategoriaActividadIdCategoriaActividad(Long idActividad, Long idCategoriaActividad); // creo que esto ya no usaremos,,

    /* FEXCOIN */
    List<Rubrica> findAllByIdRubricaIn(List<Long> rubricaIds);

    /**
     * ✅ NUEVO: Cuenta rúbricas activas de una categoría
     */
    long countByCategoriaActividad_IdCategoriaActividadAndEstado(
        Long categoriaId, 
        String estado
    );
}
