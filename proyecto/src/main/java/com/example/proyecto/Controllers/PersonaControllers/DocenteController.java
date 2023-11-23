package com.example.proyecto.Controllers.PersonaControllers;

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

import com.example.proyecto.Models.Entity.Usuario;
import com.example.proyecto.Models.Entity.Docente;
import com.example.proyecto.Models.Entity.Persona;
import com.example.proyecto.Models.Service.IDocenteService;
import com.example.proyecto.Models.Service.IPersonaService;
import com.example.proyecto.Models.Service.IUsuarioService;

@Controller
public class DocenteController {
    
    @Autowired
	private IUsuarioService usuarioService;

    @Autowired
	private IDocenteService docenteService;

    @Autowired
    private IPersonaService personaService;

      // FUNCION PARA LA VISUALIZACION DE REGISTRO DE MNACIONALIDAD
	@RequestMapping(value = "/DocenteR", method = RequestMethod.GET) // Pagina principal
	public String Docente(HttpServletRequest request, Model model) {
		if (request.getSession().getAttribute("usuario") != null) {

			model.addAttribute("docente", new Docente());
			model.addAttribute("docentes", docenteService.findAll());
            model.addAttribute("personas", personaService.findAll());

			return "persona/gestionar-docente";
		} else {
			return "redirect:LoginR";
		}
	}

     @RequestMapping(value = "/DocenteF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
	public String DocenteF(@Validated Docente docente,@RequestParam(value = "persona")Long id_persona, RedirectAttributes redirectAttrs) { // validar los datos capturados (1)
		Persona persona = personaService.findOne(id_persona);
		docente.setPersona(persona);
		docente.setEstado("A");
		docenteService.save(docente);

		persona.setEstado("E");
		personaService.save(persona);
		redirectAttrs
				.addFlashAttribute("mensaje", "Registro Exitoso del País")
				.addFlashAttribute("clase", "success alert-dismissible fade show");

		return "redirect:/DocenteR";
	}

    @RequestMapping(value = "/editar-docente/{id_docente}")
	public String editar_docente(@PathVariable("id_docente") Long id_docente, Model model) {

		
        Docente docente = docenteService.findOne(id_docente);

		model.addAttribute("docente", docente);
		model.addAttribute("docente", docenteService.findAll());
        model.addAttribute("personas", personaService.findAll());
		model.addAttribute("edit", "true");

		return "persona/gestionar-docente";

	}

     @RequestMapping(value = "/DocenteModF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
	public String DocenteModF(@Validated Docente docente, RedirectAttributes redirectAttrs) { // validar los datos capturados (1)

		docenteService.save(docente);
		redirectAttrs
				.addFlashAttribute("mensaje", "Datos del País Actualizados Correctamente")
				.addFlashAttribute("clase", "success alert-dismissible fade show");

		return "redirect:/DocenteR";
	}

     @RequestMapping(value = "/eliminar-docente/{id_docente}")
	public String eliminar_usuario(@PathVariable("id_docente") Long id_docente) {

		 Docente docente = docenteService.findOne(id_docente);

		docente.setEstado("X");

		docenteService.save(docente);
		return "redirect:/DocenteR";

	}



}
