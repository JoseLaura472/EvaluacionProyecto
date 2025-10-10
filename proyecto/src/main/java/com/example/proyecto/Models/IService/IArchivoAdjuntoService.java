package com.example.proyecto.Models.IService;

import java.util.List;

import com.example.proyecto.Models.Entity.ArchivoAdjunto;

public interface IArchivoAdjuntoService {
    
    public List<ArchivoAdjunto> listarArchivoAdjunto();
    public ArchivoAdjunto registrarArchivoAdjunto(ArchivoAdjunto archivoAdjunto);

    public ArchivoAdjunto buscarArchivoAdjunto(Long id_archivo_adjunto);

    public void modificarArchivoAdjunto(ArchivoAdjunto archivoAdjunto);

    public ArchivoAdjunto buscarArchivoAdjuntoPorProyecto(Long id_proyecto);

  
}
