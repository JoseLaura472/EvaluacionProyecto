package com.example.proyecto.Models.Utils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import org.springframework.web.multipart.MultipartFile;

import com.example.proyecto.Models.Entity.Proyecto;




public class AdjuntarArchivo {
    MultipartFile file; 

    public AdjuntarArchivo() {
     }
    
    public String crearSacDirectorio(String sDirectorio){
        File directorio = new File(sDirectorio);
        if (!directorio.exists()) {
            if (directorio.mkdirs()) {
                  return  directorio.getPath()+"/";
            } else {
                  return null;
            }
        }
        
        return directorio.getPath()+"/";
    }

    public Integer adjuntarArchivoProyecto(Proyecto proyecto, String rutaArchivo) throws FileNotFoundException, IOException{

        // Save file on system
    file = proyecto.getFile();
    if (file != null && !file.getOriginalFilename().isEmpty()) {
        // Guarda el archivo en el sistema
        BufferedOutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream(new File(rutaArchivo, proyecto.getNombreArchivo())));
        outputStream.write(file.getBytes());
        outputStream.flush();
        outputStream.close();
        return 1; // Adjuntado Correctamente
    } else {
        // No se subió ningún archivo, pero no es un error
        return 2; // Opcional: Código para indicar que no se adjuntó archivo pero no es un error
    }
 }

 
}
