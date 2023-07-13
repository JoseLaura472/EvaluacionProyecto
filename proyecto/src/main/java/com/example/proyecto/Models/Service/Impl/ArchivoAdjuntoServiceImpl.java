package com.example.proyecto.Models.Service.Impl;
import java.util.List;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.proyecto.Models.Dao.IArchivoAdjuntoDao;
import com.example.proyecto.Models.Entity.ArchivoAdjunto;
import com.example.proyecto.Models.Service.IArchivoAdjuntoService;

@Service
@Transactional
public class ArchivoAdjuntoServiceImpl implements IArchivoAdjuntoService{
    @Autowired
    private IArchivoAdjuntoDao archivoAdjuntoDao;

    @Override
    public ArchivoAdjunto registrarArchivoAdjunto(ArchivoAdjunto archivoAdjunto) {
        return archivoAdjuntoDao.registrarArchivoAdjunto(archivoAdjunto);
    }

    @Override
    public ArchivoAdjunto buscarArchivoAdjunto(Long id_archivo_adjunto) {
        return archivoAdjuntoDao.buscarArchivoAdjunto(id_archivo_adjunto);
    }

    @Override
    public void modificarArchivoAdjunto(ArchivoAdjunto archivoAdjunto) {
        archivoAdjuntoDao.modificarArchivoAdjunto(archivoAdjunto);
    }

    @Override
    public List<ArchivoAdjunto> listarArchivoAdjunto() {
        return archivoAdjuntoDao.listarArchivoAdjuntoJPQL();
    }

 

    @Override
    public ArchivoAdjunto buscarArchivoAdjuntoPorProyecto(Long id_proyecto) {
       return archivoAdjuntoDao.buscarArchivoAdjuntoPorProyecto(id_proyecto);
    }
 
}
