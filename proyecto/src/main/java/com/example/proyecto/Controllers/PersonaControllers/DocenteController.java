package com.example.proyecto.Controllers.PersonaControllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.proyecto.Models.Entity.Usuario;
import com.example.proyecto.Models.Entity.Docente;
import com.example.proyecto.Models.Entity.Persona;
import com.example.proyecto.Models.Service.IDocenteService;
import com.example.proyecto.Models.Service.IPersonaService;
import com.example.proyecto.Models.Service.IUsuarioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



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


			return "persona/gestionar-docente";
		} else {
			return "redirect:LoginR";
		}
	}

     @RequestMapping(value = "/DocenteF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
	public ResponseEntity<String> DocenteF(@Validated Docente docente,@Validated Persona persona, RedirectAttributes redirectAttrs) { // validar los datos capturados (1)
		
		if (docente.getId_docente() == null && persona.getId_persona() == null) {
			persona.setEstado("E");
			personaService.save(persona);
			docente.setPersona(persona);
			docente.setEstado("A");
			docenteService.save(docente);
			return ResponseEntity.ok("1");
		}else{
			Persona p = personaService.getPersonaCI(persona.getCi());
			if (p != null) {
				System.out.println("ya Existe un Registro Igual");
			}
			return ResponseEntity.ok("3");
		}
		
	}

	@GetMapping("/nuevo_registroD")
	public String nuevo_registroD(Model model) {

		model.addAttribute("persona", new Persona());
		model.addAttribute("docente", new Docente());
		return "persona/contentPersona :: Docente";
	}
	
	@GetMapping("/tabla_docentes")
	public String tabla_docentes(Model model) {

		model.addAttribute("docentes", docenteService.listaDocentes("A"));

		return "persona/contentPersona :: Tabla_Docentes";
	}
	

    @RequestMapping(value = "/editar-docente/{id_docente}")
	public String editar_docente(@PathVariable("id_docente") Long id_docente, Model model) {

        Docente docente = docenteService.findOne(id_docente);
		
		model.addAttribute("docente", docente);
		model.addAttribute("persona", docente.getPersona());
		model.addAttribute("edit", "true");
		return "persona/contentPersona :: Docente";
	}

    @RequestMapping(value = "/DocenteModF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
	public ResponseEntity<String> DocenteModF(@Validated Docente docente, @Validated Persona persona,
			RedirectAttributes redirectAttrs) { // validar los datos capturados (1)
		
		persona.setEstado("A");
		personaService.save(persona);
		docente.setPersona(persona);
		docente.setEstado("A");
		docenteService.save(docente);
		return ResponseEntity.ok("2");
	}

    @RequestMapping(value = "/eliminar-docente/{id_docente}")
	@ResponseBody
	public void eliminar_usuario(@PathVariable("id_docente") Long id_docente) {

		Docente docente = docenteService.findOne(id_docente);

		docente.setEstado("X");
		Persona persona = docente.getPersona();

		persona.setEstado("X");

		personaService.save(persona);
		docenteService.save(docente);

	}

}
