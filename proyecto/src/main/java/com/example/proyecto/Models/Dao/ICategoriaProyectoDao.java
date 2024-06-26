package com.example.proyecto.Models.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.proyecto.Models.Entity.CategoriaProyecto;

public interface ICategoriaProyectoDao extends CrudRepository<CategoriaProyecto,Long>{
    
    @Query(value = "SELECT cp.* FROM categoria_proyecto cp \n" + //
                "LEFT JOIN tipo_proyecto tp ON tp.id_tipo_proyecto = cp.id_tipo_proyecto \n" + //
                "WHERE tp.id_tipo_proyecto = ?1",nativeQuery = true)
    public List<CategoriaProyecto> getCategoriasPorTipoProyecto(Long id_tipoProyecto);
}
