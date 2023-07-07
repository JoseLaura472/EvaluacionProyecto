package com.example.proyecto.Controllers;


import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class indexController {
 

	@RequestMapping(value = "/Inicio", method = RequestMethod.GET) // Pagina principal
	public String Inicio(HttpServletRequest request,Model model) {
		if (request.getSession().getAttribute("usuario") != null) {
				return "index";
		} else {
			return "redirect:LoginR";
		}

	}
}
