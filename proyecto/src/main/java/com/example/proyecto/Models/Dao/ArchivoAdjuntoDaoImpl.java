package com.example.proyecto.Models.Dao;


import java.util.List;


import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.proyecto.Models.Entity.ArchivoAdjunto;

@Repository("IArchivoAdjuntoDao")
public class ArchivoAdjuntoDaoImpl implements IArchivoAdjuntoDao{
    
    @PersistenceContext
    private jakarta.persistence.EntityManager em;

    @Transactional
  
    public ArchivoAdjunto registrarArchivoAdjunto(ArchivoAdjunto archivoAdjunto){

        em.persist(archivoAdjunto);
        return archivoAdjunto;
    }

    public ArchivoAdjunto buscarArchivoAdjunto(Long id_archivo_adjunto){
        String sql = " SELECT arc "
        + " FROM ArchivoAdjunto arc "
        + " WHERE arc.id_archivo_adjunto =:id_archivo_adjunto"
        + " AND arc.idEstado = 'A' ";
        Query q = em.createQuery(sql);
        q.setParameter("id_archivo_adjunto", id_archivo_adjunto);
        try {
        return (ArchivoAdjunto) q.getSingleResult();
        } catch (Exception e) {
        return null;
        }
    }


    public void modificarArchivoAdjunto(ArchivoAdjunto archivoAdjunto){

        em.merge(archivoAdjunto);
    }

    @Override
    public List<ArchivoAdjunto> listarArchivoAdjuntoJPQL() {
        String sql = "SELECT adj "
        + " FROM ArchivoAdjunto adj "
        + " WHERE adj.estado = 'A' ";
        Query q = em.createQuery(sql);
        return q.getResultList();
    }


    @Override
    public ArchivoAdjunto buscarArchivoAdjuntoPorProyecto(Long id_proyecto) {
      
         String sql = "SELECT gaa  "
        + " FROM Proyecto tr LEFT JOIN  tr.archivoAdjunto gaa"
        + " WHERE tr.id_proyecto =:id_proyecto "
        + " AND gaa.estado = 'A' ";
        /*String sql = "select gaa from pasarela_tramite tr, pasarela_archivo_adjunto ar WHERE tr.id_archivo_adjunto=ar.id_archivo_adjunto and tr.estado='A' and tr.id_tramite=:id_tramite;";*/
        Query q = em.createQuery(sql);
        q.setParameter("id_proyecto", id_proyecto);
        try {
        return (ArchivoAdjunto) q.getSingleResult();
        } catch (Exception e) {
        return null;
        }
    }
}
