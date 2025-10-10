package com.example.proyecto.Models.IServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.ICategoriaProyectoDao;
import com.example.proyecto.Models.Entity.CategoriaProyecto;
import com.example.proyecto.Models.IService.ICategoriaProyectoService;

@Service
public class CategoriaProyectoServiceImpl implements ICategoriaProyectoService{

    @Autowired
    private ICategoriaProyectoDao categoriaProyectoDao;

    @Override
    public List<CategoriaProyecto> findAll() {
        // TODO Auto-generated method stub
        return (List<CategoriaProyecto>) categoriaProyectoDao.findAll();
    }

    @Override
    public void save(CategoriaProyecto categoriaProyecto) {
        // TODO Auto-generated method stub
        categoriaProyectoDao.save(categoriaProyecto);
    }

    @Override
    public CategoriaProyecto findOne(Long id) {
        // TODO Auto-generated method stub
        return categoriaProyectoDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        categoriaProyectoDao.deleteById(id);
    }

    @Override
    public List<CategoriaProyecto> getCategoriasPorTipoProyecto(Long id_tipoProyecto) {
        // TODO Auto-generated method stub
        return categoriaProyectoDao.getCategoriasPorTipoProyecto(id_tipoProyecto);
    }
    
}
