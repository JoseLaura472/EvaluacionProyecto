package com.example.proyecto.Controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/adm")
public class indexController {
 
	@RequestMapping(value = "/Inicio", method = RequestMethod.GET) // Pagina principal
	public String Inicio(HttpServletRequest request,Model model) {
		if (request.getSession().getAttribute("usuario") != null) {
				return "index";
		} else {
			return "redirect:loginR";
		}
	}

	@GetMapping("/cargar-datos")
    @ResponseBody
    public ResponseEntity<String> cargarDatos(HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            // La sesión ha expirado o no existe
            return new ResponseEntity<>("Sesión expirada", HttpStatus.UNAUTHORIZED);
        }
        // Si la sesión está activa, devuelve el contenido
        return new ResponseEntity<>("Datos del contenido", HttpStatus.OK);
    }

    @GetMapping("/vista-administrador")
    public String inicio(HttpServletRequest request, Model model) {
        return "vista-admin";
    }
}
