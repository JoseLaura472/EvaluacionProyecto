package com.example.proyecto.Controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.proyecto.Models.Entity.Usuario;
import com.example.proyecto.Models.Service.IUsuarioService;

@Controller
public class loginController {

    @Autowired
    private IUsuarioService usuarioService;

    
    // Funcion de visualizacion de iniciar sesiòn administrador
    @RequestMapping(value = "/LoginR", method = RequestMethod.GET)
    public String LoginR(Model model) {

        return "index";
    }

    // Funciòn de iniciar sesiòn administrador
    @RequestMapping(value = "/LogearseF", method = RequestMethod.POST)
    public String logearseF(@RequestParam(value = "usuario") String user,
            @RequestParam(value = "contrasena") String contrasena, Model model, HttpServletRequest request,
            RedirectAttributes flash) {
        // los dos parametros de usuario, contraseña vienen del formulario html
        Usuario usuario = usuarioService.getUsuarioContraseña(user, contrasena);

        if (usuario != null) {
            if (usuario.getEstado().equals("X")) {
                return "redirect:/cerrar_sesionAdm";
            }
            HttpSession sessionAdministrador = request.getSession(true);

            sessionAdministrador.setAttribute("usuario", usuario);
            sessionAdministrador.setAttribute("persona", usuario.getPersona());

            flash.addAttribute("success", usuario.getPersona().getNombres());

            return "redirect:/AdmPG/";

        } else {
            return "redirect:/LoginR";
        }

    }

    // Funcion de visualizaciòn de la pagina principal
	@RequestMapping(value = "/AdmPG", method = RequestMethod.GET) // Pagina principal
	public String Inicio(HttpServletRequest request, Model model) {
		if (request.getSession().getAttribute("usuario") != null) {
		
		
			
			return "Inicio";
		} else {
			return "redirect:LoginR";
		}
	}

    // Funcion de cerrar sesion de administrador
	@RequestMapping("/cerrar_sesionAdm")
	public String cerrarSesion2(HttpServletRequest request, RedirectAttributes flash) {
		HttpSession sessionAdministrador = request.getSession();
		if (sessionAdministrador != null) {
			sessionAdministrador.invalidate();
			flash.addAttribute("validado", "Se cerro sesion con exito!");
		}
		return "redirect:/LoginR";
	}

}
