package com.example.proyecto.Controllers.PersonaControllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.proyecto.Models.Entity.Estudiante;
import com.example.proyecto.Models.Entity.Jurado;
import com.example.proyecto.Models.Entity.Persona;
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

			model.addAttribute("estudiantes", estudianteService.findAll());

			return "persona/gestionar-Estudiante";
		} else {
			return "redirect:LoginR";
		}
	}

   
	@RequestMapping(value = "/EstudianteF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
	public ResponseEntity<String> EstudianteF(@Validated Estudiante estudiante,@Validated Persona persona, RedirectAttributes redirectAttrs) { // validar los datos capturados (1)
		
		if (estudiante.getId_estudiante() == null && persona.getId_persona() == null) {
			persona.setEstado("E");
			personaService.save(persona);
			estudiante.setPersona(persona);
			estudiante.setEstado("A");
			estudianteService.save(estudiante);
			return ResponseEntity.ok("1");
		}else{
			Persona p = personaService.getPersonaCI(persona.getCi());
			if (p != null) {
				System.out.println("ya Existe un Registro Igual");
			}
			return ResponseEntity.ok("3");
		}
		
	}

	@GetMapping("/nuevo_registroE")
	public String nuevo_registroE(Model model) {

		model.addAttribute("persona", new Persona());
		model.addAttribute("estudiante", new Estudiante());
		return "persona/contentPersona :: Estudiante";
	}
	
	@GetMapping("/tabla_estudiantes")
	public String tabla_estudiantes(Model model) {

		model.addAttribute("estudiantes", estudianteService.findAll());

		return "persona/contentPersona :: Tabla_Estudiantes";
	}

    @RequestMapping(value = "/editar-estudiante/{id_estudiante}")
	public String editar_estudiante(@PathVariable("id_estudiante") Long id_estudiante, Model model) {

		Estudiante estudiante = estudianteService.findOne(id_estudiante);

		model.addAttribute("estudiante", estudiante);
        model.addAttribute("persona", estudiante.getPersona());
		model.addAttribute("edit", "true");

		return "persona/contentPersona :: Estudiante";
	}

	@RequestMapping(value = "/EstudianteModF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
	public ResponseEntity<String> EstudianteModF(@Validated Estudiante estudiante, @Validated Persona persona,
			RedirectAttributes redirectAttrs) { // validar los datos capturados (1)
		
		persona.setEstado("A");
		personaService.save(persona);
		estudiante.setPersona(persona);
		estudiante.setEstado("A");
		estudianteService.save(estudiante);
		return ResponseEntity.ok("2");
	}

	@RequestMapping(value = "/eliminar-estudiante/{id_estudiante}")
	@ResponseBody
	public void eliminar_estudiante(@PathVariable("id_estudiante") Long id_estudiante) {

		Estudiante estudiante = estudianteService.findOne(id_estudiante);

		estudiante.setEstado("X");

		estudianteService.save(estudiante);

	}

}
