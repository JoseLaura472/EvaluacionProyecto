package com.example.proyecto.Controllers;


import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class indexController {
    @RequestMapping(value = "/Inicio", method = RequestMethod.GET) // Pagina principal
	public String Inicio(Model model) {
		
		return "index";

	}
}
