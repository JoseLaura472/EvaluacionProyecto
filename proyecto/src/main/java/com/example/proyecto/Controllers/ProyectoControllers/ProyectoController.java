package com.example.proyecto.Controllers.ProyectoControllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.example.proyecto.Models.Entity.Proyecto;
import com.example.proyecto.Models.Service.IDocenteService;
import com.example.proyecto.Models.Service.IEstudianteService;
import com.example.proyecto.Models.Service.IProgramaService;
import com.example.proyecto.Models.Service.IProyectoService;

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



       // FUNCION PARA LA VISUALIZACION DE REGISTRO DE MNACIONALIDAD
	@RequestMapping(value = "/ProyectoR", method = RequestMethod.GET) // Pagina principal
	public String ProyectoR(HttpServletRequest request, Model model) {
		if (request.getSession().getAttribute("usuario") != null) {

			model.addAttribute("proyecto", new Proyecto());
			model.addAttribute("proyectos", proyectoService.findAll());
            model.addAttribute("docentes", docenteService.findAll());
            model.addAttribute("estudiantes", estudianteService.findAll());
            model.addAttribute("programas", programaService.findAll());

			return "proyecto/gestionar-proyecto";
		} else {
			return "redirect:LoginR";
		}
	}


     // Boton para Guardar Documento
    @RequestMapping(value = "/ProyectoF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
    public String ProyectoF(@Validated Proyecto proyecto, RedirectAttributes redirectAttrs,
            @RequestParam(value = "estudiante") Long[] id_estudiantes) { // validar los datos capturados (1)
       
        proyecto.setEstado("A");
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
        model.addAttribute("programas", programaService.findAll());

        model.addAttribute("edit", "true");
        return "proyecto/gestionar-proyecto";

    }

    // Boton para Guardar Documento
    @RequestMapping(value = "/ProyectoModF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
    public String ProyectoModF(@Validated Proyecto proyecto, RedirectAttributes redirectAttrs,
             @RequestParam(value = "estudiante") Long[] id_estudiantes) { // validar los datos capturados (1)
      
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


}
