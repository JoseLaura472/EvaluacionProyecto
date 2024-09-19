package com.example.proyecto.Controllers.UsuarioControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.proyecto.Models.Entity.Usuario;
import com.example.proyecto.Models.Service.IJuradoService;
import com.example.proyecto.Models.Service.IPersonaService;
import com.example.proyecto.Models.Service.IUsuarioService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UsuarioController {

    @Autowired
	private IUsuarioService usuarioService;

    
    @Autowired
    private IPersonaService personaService;

	@Autowired
	private IJuradoService juradoService;

    // FUNCION PARA LA VISUALIZACION DE REGISTRO DE MNACIONALIDAD
	@RequestMapping(value = "/UsuarioR", method = RequestMethod.GET) // Pagina principal
	public String Usuario(HttpServletRequest request, Model model) {
		if (request.getSession().getAttribute("usuario") != null) {

			model.addAttribute("usuario", new Usuario());
			model.addAttribute("usuarios", usuarioService.findAll());
            model.addAttribute("personas", juradoService.findAll());

			return "usuario/gestionar-usuario";
		} else {
			return "redirect:LoginR";
		}
	}

     @RequestMapping(value = "/UsuarioF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
	public String UsuarioF(@Validated Usuario usuario, RedirectAttributes redirectAttrs) { // validar los datos capturados (1)

		usuarioService.save(usuario);
		redirectAttrs
				.addFlashAttribute("mensaje", "Registro Exitoso del País")
				.addFlashAttribute("clase", "success alert-dismissible fade show");

		return "redirect:/UsuarioR";
	}

    @RequestMapping(value = "/editar-usuario/{id_usuario}")
	public String editar_usuario(@PathVariable("id_usuario") Long id_usuario, Model model) {

		Usuario usuario = usuarioService.findOne(id_usuario);

		model.addAttribute("usuario", usuario);
		model.addAttribute("usuarios", usuarioService.findAll());
        model.addAttribute("personas", personaService.findAll());
		model.addAttribute("edit", "true");

		return "usuario/gestionar-usuario";

	}

     @RequestMapping(value = "/UsuarioModF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
	public String UsuarioModF(@Validated Usuario usuario, RedirectAttributes redirectAttrs) { // validar los datos capturados (1)

		usuarioService.save(usuario);
		redirectAttrs
				.addFlashAttribute("mensaje", "Datos del País Actualizados Correctamente")
				.addFlashAttribute("clase", "success alert-dismissible fade show");

		return "redirect:/UsuarioR";
	}

     @RequestMapping(value = "/eliminar-usuario/{id_usuario}")
	public String eliminar_usuario(@PathVariable("id_usuario") Long id_usuario) {

		Usuario usuario = usuarioService.findOne(id_usuario);

		usuario.setEstado("X");

		usuarioService.save(usuario);
		return "redirect:/UsuarioR";

	}
}
