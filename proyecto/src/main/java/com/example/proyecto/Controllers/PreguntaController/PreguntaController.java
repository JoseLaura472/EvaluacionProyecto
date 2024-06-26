package com.example.proyecto.Controllers.PreguntaController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.example.proyecto.Models.Entity.Ponderacion;
import com.example.proyecto.Models.Entity.Pregunta;
import com.example.proyecto.Models.Service.ICategoriaCriterioService;
import com.example.proyecto.Models.Service.IPreguntaService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class PreguntaController {
    
    @Autowired
    private IPreguntaService preguntaService;

    @Autowired
    private ICategoriaCriterioService categoriaCriterioService;

    @GetMapping("/PreguntaR")
    public String PreguntaR(Model model, HttpServletRequest request) {
        if (request.getSession().getAttribute("usuario") != null) {

            model.addAttribute("pre", new Pregunta());
            model.addAttribute("preguntas", preguntaService.findAll());
            model.addAttribute("categorias", categoriaCriterioService.findAll());
            return "pregunta/gestionar-pregunta";
        }else{

            return "redirect:/LoginR";
        }
    }
    
}
