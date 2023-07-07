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

import com.example.proyecto.Models.Entity.Jurado;
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

			model.addAttribute("jurado", new Jurado());
			model.addAttribute("jurados", juradoService.findAll());
            model.addAttribute("personas", personaService.findAll());

			return "persona/gestionar-jurado";
		} else {
			return "redirect:LoginR";
		}
	}

     @RequestMapping(value = "/JuradoF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
	public String JuradoF(@Validated Jurado jurado, RedirectAttributes redirectAttrs) { // validar los datos capturados (1)

		jurado.setEstado("A");
		juradoService.save(jurado);
		redirectAttrs
				.addFlashAttribute("mensaje", "Registro Exitoso del País")
				.addFlashAttribute("clase", "success alert-dismissible fade show");

		return "redirect:/JuradoR";
	}

    @RequestMapping(value = "/editar-jurado/{id_jurado}")
	public String editar_jurado(@PathVariable("id_jurado") Long id_jurado, Model model) {

		
        Jurado jurado = juradoService.findOne(id_jurado);

		model.addAttribute("jurado", jurado);
		model.addAttribute("jurados", juradoService.findAll());
        model.addAttribute("personas", personaService.findAll());
		model.addAttribute("edit", "true");

		return "persona/gestionar-jurado";

	}

     @RequestMapping(value = "/JuradoModF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
	public String JuradoModF(@Validated Jurado jurado, RedirectAttributes redirectAttrs) { // validar los datos capturados (1)

		juradoService.save(jurado);
		redirectAttrs
				.addFlashAttribute("mensaje", "Datos del País Actualizados Correctamente")
				.addFlashAttribute("clase", "success alert-dismissible fade show");

		return "redirect:/JuradoR";
	}

     @RequestMapping(value = "/eliminar-jurado/{id_jurado}")
	public String eliminar_jurado(@PathVariable("id_jurado") Long id_jurado) {

		Jurado jurado = juradoService.findOne(id_jurado);

		jurado.setEstado("X");

		juradoService.save(jurado);
		return "redirect:/JuradoR";

	}

}
