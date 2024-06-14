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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.proyecto.Models.Entity.Jurado;
import com.example.proyecto.Models.Entity.Persona;
import com.example.proyecto.Models.Service.IJuradoService;
import com.example.proyecto.Models.Service.IPersonaService;

@Controller
public class JuradoController {
    
    @Autowired
    private IPersonaService personaService;

    @Autowired
    private IJuradoService juradoService;


         // FUNCION PARA LA VISUALIZACION DE REGISTRO DE MNACIONALIDAD
	@RequestMapping(value = "/JuradoR", method = RequestMethod.GET) // Pagina principal
	public String Jurado(HttpServletRequest request, Model model) {
		if (request.getSession().getAttribute("usuario") != null) {


			return "persona/gestionar-jurado";
		} else {
			return "redirect:LoginR";
		}
	}

	@RequestMapping(value = "/JuradoF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
	public ResponseEntity<String> DocenteF(@Validated Jurado jurado,@Validated Persona persona, RedirectAttributes redirectAttrs) { // validar los datos capturados (1)
		
		if (jurado.getId_jurado() == null && persona.getId_persona() == null) {
			persona.setEstado("E");
			personaService.save(persona);
			jurado.setPersona(persona);
			jurado.setEstado("A");
			juradoService.save(jurado);
			return ResponseEntity.ok("1");
		}else{
			Persona p = personaService.getPersonaCI(persona.getCi());
			if (p != null) {
				System.out.println("ya Existe un Registro Igual");
			}
			return ResponseEntity.ok("3");
		}
		
	}

	@GetMapping("/nuevo_registroJ")
	public String nuevo_registroJ(Model model) {

		model.addAttribute("persona", new Persona());
		model.addAttribute("jurado", new Jurado());
		return "persona/contentPersona :: Jurado";
	}
	
	@GetMapping("/tabla_jurados")
	public String tabla_jurados(Model model) {

		model.addAttribute("jurados", juradoService.findAll());

		return "persona/contentPersona :: Tabla_Jurados";
	}

	@RequestMapping(value = "/editar-jurado/{id_jurado}")
	public String editar_jurado(@PathVariable("id_jurado") Long id_jurado, Model model) {

        Jurado jurado = juradoService.findOne(id_jurado);

		model.addAttribute("jurado", jurado);
		model.addAttribute("persona", jurado.getPersona());
		model.addAttribute("edit", "true");
		return "persona/contentPersona :: Jurado";
	}

	@RequestMapping(value = "/JuradoModF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
	public ResponseEntity<String> JuradoModF(@Validated Jurado jurado, @Validated Persona persona,
			RedirectAttributes redirectAttrs) { // validar los datos capturados (1)
		
		persona.setEstado("A");
		personaService.save(persona);
		jurado.setPersona(persona);
		jurado.setEstado("A");
		juradoService.save(jurado);
		return ResponseEntity.ok("2");
	}

	@RequestMapping(value = "/eliminar-jurado/{id_jurado}")
	@ResponseBody
	public void eliminar_jurado(@PathVariable("id_jurado") Long id_jurado) {

		Jurado jurado = juradoService.findOne(id_jurado);

		jurado.setEstado("X");
		Persona persona = jurado.getPersona();

		persona.setEstado("X");

		personaService.save(persona);

		juradoService.save(jurado);

	}

}
