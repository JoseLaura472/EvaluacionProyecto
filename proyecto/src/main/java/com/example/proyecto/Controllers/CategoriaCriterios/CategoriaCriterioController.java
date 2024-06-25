package com.example.proyecto.Controllers.CategoriaCriterios;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;


@Controller
public class CategoriaCriterioController {
    
    @GetMapping("/Categoria_CriterioR")
    public String Categoria_CriterioR(Model model, HttpServletRequest request) {
        if (request.getSession().getAttribute("usuario") != null) {

            
            return "categoriaCriterio/gestionar-categoriaCriterio";
        }else{

            return "redirect:/LoginR";
        }
    }
    
}
