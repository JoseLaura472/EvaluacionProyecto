package com.example.proyecto.Controllers.PreguntaController;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;

import com.example.proyecto.Models.Entity.CategoriaCriterio;
import com.example.proyecto.Models.Entity.Pregunta;
import com.example.proyecto.Models.Service.ICategoriaCriterioService;
import com.example.proyecto.Models.Service.IPreguntaService;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PostMapping;




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
            List<CategoriaCriterio> categorias = categoriaCriterioService.findAll();

            // Ordenar preguntas dentro de cada categoría
            for (CategoriaCriterio categoria : categorias) {
                List<Pregunta> preguntasOrdenadas = categoria.getPreguntas().stream()
                        .filter(pre -> !pre.getEstado().equals("X")) // Filtrar por estado
                        .sorted((p1, p2) -> p1.getId_pregunta().compareTo(p2.getId_pregunta())) // Ordenar por id
                        .collect(Collectors.toList());
                categoria.setPreguntas(preguntasOrdenadas);
            }

            model.addAttribute("categorias", categorias);
            return "pregunta/gestionar-pregunta";
        }else{

            return "redirect:/LoginR";
        }
    }

    @PostMapping("/PreguntaF")
    public ResponseEntity<String> PreguntaF(@Validated Pregunta pregunta,
            @RequestParam(name = "categoriaCriterio") Long id_categoria_criterio) {
        CategoriaCriterio categoriaCriterio = categoriaCriterioService.findOne(id_categoria_criterio);
        if (categoriaCriterio == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Categoria No Encontrada");
        }

        if (pregunta.getId_pregunta() == null) {
            // Nueva Ponderación
            pregunta.setEstado("A");
            pregunta.setCategoriaCriterio(categoriaCriterio);
            preguntaService.save(pregunta);
            return ResponseEntity.ok("1");
        } else {
            // Actualización de Ponderación existente
            Pregunta existingPregunta = preguntaService.findOne(pregunta.getId_pregunta());
            if (existingPregunta != null) {
                // Actualiza solo los campos necesarios
                existingPregunta.setCategoriaCriterio(categoriaCriterio);
                existingPregunta.setNom_pregunta(pregunta.getNom_pregunta());
                preguntaService.save(existingPregunta);
                return ResponseEntity.ok("2");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pregunta no encontrada");
            }
        }
    }
    

    @GetMapping("/editar_pregunta/{id_pregunta}")
    public String editar_pregunta(Model model, HttpServletRequest request,
            @PathVariable(name = "id_pregunta") Long id_pregunta) {
        if (request.getSession().getAttribute("usuario") != null) {

            model.addAttribute("pre", preguntaService.findOne(id_pregunta));

            List<CategoriaCriterio> categorias = categoriaCriterioService.findAll();

            // Ordenar preguntas dentro de cada categoría
            for (CategoriaCriterio categoria : categorias) {
                List<Pregunta> preguntasOrdenadas = categoria.getPreguntas().stream()
                        .filter(pre -> !pre.getEstado().equals("X")) // Filtrar por estado
                        .sorted((p1, p2) -> p1.getId_pregunta().compareTo(p2.getId_pregunta())) // Ordenar por id
                        .collect(Collectors.toList());
                categoria.setPreguntas(preguntasOrdenadas);
            }

            model.addAttribute("categorias", categorias);

            return "pregunta/gestionar-pregunta";
        } else {

            return "redirect:/LoginR";
        }
    }
    
    @GetMapping("/eliminar_pregunta/{id_pregunta}")
    @ResponseBody
    public void eliminar_pregunta(@PathVariable(name = "id_pregunta")Long id_pregunta) {
        Pregunta pregunta = preguntaService.findOne(id_pregunta);
        pregunta.setEstado("X");
        preguntaService.save(pregunta);
    }
    
}
