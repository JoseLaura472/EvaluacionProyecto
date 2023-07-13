package com.example.proyecto.Models.Dao;

import java.util.List;

import com.example.proyecto.Models.Entity.ArchivoAdjunto;

public interface IArchivoAdjuntoDao {
    public ArchivoAdjunto registrarArchivoAdjunto(ArchivoAdjunto archivoAdjunto);

    public ArchivoAdjunto buscarArchivoAdjunto(Long id_archivo_adjunto);



    public ArchivoAdjunto buscarArchivoAdjuntoPorProyecto(Long id_proyecto);

    public void modificarArchivoAdjunto(ArchivoAdjunto archivoAdjunto);

    public List<ArchivoAdjunto> listarArchivoAdjuntoJPQL();
}
