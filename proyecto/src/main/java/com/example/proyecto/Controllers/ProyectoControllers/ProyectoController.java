package com.example.proyecto.Controllers.ProyectoControllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.proyecto.Models.Entity.ArchivoAdjunto;
import com.example.proyecto.Models.Entity.Proyecto;
import com.example.proyecto.Models.Service.IArchivoAdjuntoService;
import com.example.proyecto.Models.Service.IDocenteService;
import com.example.proyecto.Models.Service.IEstudianteService;
import com.example.proyecto.Models.Service.IJuradoService;
import com.example.proyecto.Models.Service.IProgramaService;
import com.example.proyecto.Models.Service.IProyectoService;
import com.example.proyecto.Models.Service.ITipoProyectoService;
import com.example.proyecto.Models.Utils.AdjuntarArchivo;

@Controller
public class ProyectoController {
    
    @Autowired
	private IEstudianteService estudianteService;

    @Autowired
	private IDocenteService docenteService;

    @Autowired
	private IProyectoService proyectoService;

    @Autowired
	private IProgramaService programaService;

    @Autowired
	private IJuradoService juradoService;

    @Autowired
    private IArchivoAdjuntoService archivoAdjuntoService;

    @Autowired
    private ITipoProyectoService tipoProyectoService;

       // FUNCION PARA LA VISUALIZACION DE REGISTRO DE MNACIONALIDAD
	@RequestMapping(value = "/ProyectoR", method = RequestMethod.GET) // Pagina principal
	public String ProyectoR(HttpServletRequest request, Model model) {
		if (request.getSession().getAttribute("usuario") != null) {

			model.addAttribute("proyecto", new Proyecto());
			model.addAttribute("proyectos", proyectoService.findAll());
            model.addAttribute("docentes", docenteService.findAll());
            model.addAttribute("estudiantes", estudianteService.findAll());
            // model.addAttribute("programas", programaService.findAll());
            model.addAttribute("jurados", juradoService.findAll());
            model.addAttribute("tiposProyectos", tipoProyectoService.findAll());


			return "proyecto/gestionar-proyecto_escuela_tecnica";
		} else {
			return "redirect:LoginR";
		}
	}


     // Boton para Guardar Documento
    @RequestMapping(value = "/ProyectoF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
    public String ProyectoF(@Validated Proyecto proyecto, RedirectAttributes redirectAttrs,
            @RequestParam(value = "estudiante",required = false) Long[] id_estudiantes,
            @RequestParam(value = "jurado") Long[] id_jurados) throws FileNotFoundException, IOException{ // validar los datos capturados (1)
            
        MultipartFile multipartFile = proyecto.getFile();
        ArchivoAdjunto archivoAdjunto = new ArchivoAdjunto();
        AdjuntarArchivo adjuntarArchivo = new AdjuntarArchivo();

        Path rootPath = Paths.get("archivos/");
        Path rootAbsolutPath = rootPath.toAbsolutePath();
        String rutaDirectorio = rootAbsolutPath+"";
        
        String rutaArchivo = adjuntarArchivo.crearSacDirectorio(rutaDirectorio);
        List<ArchivoAdjunto> listArchivos = archivoAdjuntoService.listarArchivoAdjunto();
        proyecto.setNombreArchivo((listArchivos.size() + 1)+".pdf");
        Integer ad = adjuntarArchivo.adjuntarArchivoProyecto(proyecto, rutaArchivo);
        archivoAdjunto.setNombre_archivo((listArchivos.size() + 1)+".pdf");
        archivoAdjunto.setTipo_archivo(multipartFile.getContentType());
        archivoAdjunto.setRuta_archivo(rutaArchivo);
        archivoAdjunto.setEstado("A");
        ArchivoAdjunto archivoAdjunto2 = archivoAdjuntoService.registrarArchivoAdjunto(archivoAdjunto);

        proyecto.setEstado("A");
        proyecto.setArchivoAdjunto(archivoAdjunto2);
        proyectoService.save(proyecto);
        redirectAttrs
                .addFlashAttribute("mensaje", "Registro Exitoso del Documento")
                .addFlashAttribute("clase", "success alert-dismissible fade show");

        return "redirect:/ProyectoR";
    }


     // Boton para Editar Documentos
    @RequestMapping(value = "/editar-proyecto/{id_proyecto}")
    public String editar_proyecto(@PathVariable("id_proyecto") Long id_proyecto, Model model) {

        Proyecto proyecto = proyectoService.findOne(id_proyecto);

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("proyectos", proyectoService.findAll());
        model.addAttribute("estudiantes", estudianteService.findAll());
        model.addAttribute("docentes", docenteService.findAll());
        // model.addAttribute("programas", programaService.findAll());
        model.addAttribute("jurados", juradoService.findAll());
        model.addAttribute("tiposProyectos", tipoProyectoService.findAll());


        model.addAttribute("edit", "true");
        return "proyecto/gestionar-proyecto_escuela_tecnica";

    }

    // Boton para Guardar Documento
    @RequestMapping(value = "/ProyectoModF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
    public String ProyectoModF(@Validated Proyecto proyecto, RedirectAttributes redirectAttrs,
             @RequestParam(value = "estudiante",required = false) Long[] id_estudiantes,
            @RequestParam(value = "jurado") Long[] id_jurados) throws FileNotFoundException, IOException{ // validar los datos capturados (1)
      
             MultipartFile multipartFile = proyecto.getFile();
            ArchivoAdjunto archivoAdjunto = new ArchivoAdjunto();
            AdjuntarArchivo adjuntarArchivo = new AdjuntarArchivo();
            
            Path rootPath = Paths.get("archivos");
            Path rootAbsolutPath = rootPath.toAbsolutePath();
            String rutaDirectorio = rootAbsolutPath+"";
            String rutaArchivo = adjuntarArchivo.crearSacDirectorio(rutaDirectorio);

            List<ArchivoAdjunto> listArchivos = archivoAdjuntoService.listarArchivoAdjunto();
            proyecto.setNombreArchivo((listArchivos.size() + 1)+"-mod"+".pdf");
            Integer ad = adjuntarArchivo.adjuntarArchivoProyecto(proyecto, rutaArchivo);
            if (ad == 1) {
                ArchivoAdjunto barchivoAdjunto = archivoAdjuntoService.buscarArchivoAdjuntoPorProyecto(proyecto.getId_proyecto());
                if (barchivoAdjunto == null) {
                archivoAdjunto.setNombre_archivo(proyecto.getNombreArchivo());
                archivoAdjunto.setRuta_archivo(rutaArchivo);
                archivoAdjunto.setEstado("A");
                archivoAdjuntoService.registrarArchivoAdjunto(archivoAdjunto); 
                proyecto.setArchivoAdjunto(archivoAdjunto);  
                }else{
                 barchivoAdjunto.setNombre_archivo(proyecto.getNombreArchivo());
                barchivoAdjunto.setRuta_archivo(rutaArchivo);
                archivoAdjuntoService.modificarArchivoAdjunto(barchivoAdjunto);
                }
               
            }

        proyecto.setEstado("A");
        
        proyectoService.save(proyecto);
        redirectAttrs
                .addFlashAttribute("mensaje2", "Datos del Documento Actualizados Correctamente")
                .addFlashAttribute("clase2", "success alert-dismissible fade show");

        return "redirect:/ProyectoR";
    }


     @RequestMapping(value = "/eliminar-proyecto/{id_proyecto}")
	public String eliminar_proyecto(@PathVariable("id_proyecto") Long id_proyecto) {

        Proyecto proyecto = proyectoService.findOne(id_proyecto);


		proyecto.setEstado("X");

		proyectoService.save(proyecto);
		return "redirect:/ProyectoR";

	}


      // FUNCION PARA LA VISUALIZACION DE REGISTRO DE MNACIONALIDAD
	@RequestMapping(value = "/ProyectoE", method = RequestMethod.GET) // Pagina principal
	public String ProyectoE(HttpServletRequest request, Model model) {
		if (request.getSession().getAttribute("usuario") != null) {

			
			model.addAttribute("proyectos", proyectoService.proyectosEvaluados());
            model.addAttribute("docentes", docenteService.findAll());
            model.addAttribute("estudiantes", estudianteService.findAll());
            model.addAttribute("programas", programaService.findAll());
            model.addAttribute("jurados", juradoService.findAll());

			return "proyecto/proyectos-evaluados";
		} else {
			return "redirect:LoginR";
		}
	}


     @RequestMapping(value = "/openFileProyecto/{id}", method = RequestMethod.GET, produces = "application/pdf")
    public @ResponseBody FileSystemResource abrirArchivoMedianteResourse(HttpServletResponse response,
            @PathVariable("id") long id_proyecto) throws FileNotFoundException {
        ArchivoAdjunto ArchivoAdjuntos = archivoAdjuntoService.buscarArchivoAdjuntoPorProyecto(id_proyecto);
    
        File file = new File(ArchivoAdjuntos.getRuta_archivo() +ArchivoAdjuntos.getNombre_archivo());
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=" + file.getName());
        response.setHeader("Content-Length", String.valueOf(file.length()));
        return new FileSystemResource(file);
    }

    @GetMapping(value = "/openFileProyectoN/{id}", produces = "application/pdf")
public void abrirDocumentoPDF(@PathVariable("id") long id_proyecto, HttpServletResponse response) throws IOException {
    ArchivoAdjunto archivoAdjunto = archivoAdjuntoService.buscarArchivoAdjuntoPorProyecto(id_proyecto);

    File file = new File(archivoAdjunto.getRuta_archivo() + archivoAdjunto.getNombre_archivo());
    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "inline; filename=" + file.getName());
    response.setHeader("Content-Length", String.valueOf(file.length()));

    try (InputStream inputStream = new FileInputStream(file);
         OutputStream outputStream = response.getOutputStream()) {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
    }
}



}
