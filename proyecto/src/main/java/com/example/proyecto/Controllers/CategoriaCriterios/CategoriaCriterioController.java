package com.example.proyecto.Controllers.CategoriaCriterios;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class CategoriaCriterioController {
    
    @GetMapping("/categoria_criterio")
    public String categoria_criterio(@RequestParam String param) {

        
        return "categoriaCriterio/categoria_criterio";
    }
    
}
