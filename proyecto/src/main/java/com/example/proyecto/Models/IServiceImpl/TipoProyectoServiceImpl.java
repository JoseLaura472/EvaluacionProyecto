package com.example.proyecto.Models.IServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.ITipoProyectoDao;
import com.example.proyecto.Models.Entity.TipoProyecto;
import com.example.proyecto.Models.IService.ITipoProyectoService;

@Service
public class TipoProyectoServiceImpl implements ITipoProyectoService{

    @Autowired
    private ITipoProyectoDao tipoProyectoDao;

    @Override
    public List<TipoProyecto> findAll() {
        // TODO Auto-generated method stub
        return (List<TipoProyecto>) tipoProyectoDao.findAll();
    }

    @Override
    public void save(TipoProyecto tipoProyecto) {
        // TODO Auto-generated method stub
        tipoProyectoDao.save(tipoProyecto);
    }

    @Override
    public TipoProyecto findOne(Long id) {
        // TODO Auto-generated method stub
        return tipoProyectoDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        tipoProyectoDao.deleteById(id);
    }
    
}
