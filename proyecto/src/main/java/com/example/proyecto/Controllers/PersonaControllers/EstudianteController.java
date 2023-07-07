package com.example.proyecto.Controllers.PersonaControllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.proyecto.Models.Entity.Docente;
import com.example.proyecto.Models.Entity.Estudiante;
import com.example.proyecto.Models.Service.IEstudianteService;
import com.example.proyecto.Models.Service.IPersonaService;

@Controller
public class EstudianteController {

     @Autowired
	private IEstudianteService estudianteService;

    @Autowired
    private IPersonaService personaService;



    
      // FUNCION PARA LA VISUALIZACION DE REGISTRO DE MNACIONALIDAD
	@RequestMapping(value = "/EstudianteR", method = RequestMethod.GET) // Pagina principal
	public String EstudianteR(HttpServletRequest request, Model model) {
		if (request.getSession().getAttribute("usuario") != null) {

			model.addAttribute("estudiante", new Estudiante());
			model.addAttribute("estudiantes", estudianteService.findAll());
            model.addAttribute("personas", personaService.findAll());

			return "persona/gestionar-estudiante";
		} else {
			return "redirect:LoginR";
		}
	}

     @RequestMapping(value = "/EstudianteF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
	public String EstudianteF(@Validated Estudiante estudiante, RedirectAttributes redirectAttrs) { // validar los datos capturados (1)

		estudiante.setEstado("A");
		estudianteService.save(estudiante);
		redirectAttrs
				.addFlashAttribute("mensaje", "Registro Exitoso del País")
				.addFlashAttribute("clase", "success alert-dismissible fade show");

		return "redirect:/EstudianteR";
	}

    @RequestMapping(value = "/editar-estudiante/{id_estudiante}")
	public String editar_estudiante(@PathVariable("id_estudiante") Long id_estudiante, Model model) {

		Estudiante estudiante = estudianteService.findOne(id_estudiante);


		model.addAttribute("estudiante", estudiante);
		model.addAttribute("estudiantes", estudianteService.findAll());
        model.addAttribute("personas", personaService.findAll());
		model.addAttribute("edit", "true");

		return "persona/gestionar-estudiante";

	}

     @RequestMapping(value = "/EstudianteModF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
	public String EstudianteModF(@Validated Estudiante estudiante, RedirectAttributes redirectAttrs) { // validar los datos capturados (1)

		estudianteService.save(estudiante);
		redirectAttrs
				.addFlashAttribute("mensaje", "Datos del País Actualizados Correctamente")
				.addFlashAttribute("clase", "success alert-dismissible fade show");

		return "redirect:/EstudianteR";
	}

     @RequestMapping(value = "/eliminar-estudiante/{id_estudiante}")
	public String eliminar_estudiante(@PathVariable("id_estudiante") Long id_estudiante) {

		 Estudiante estudiante = estudianteService.findOne(id_estudiante);

		estudiante.setEstado("X");

		estudianteService.save(estudiante);
		return "redirect:/Estudiante";

	}

}
