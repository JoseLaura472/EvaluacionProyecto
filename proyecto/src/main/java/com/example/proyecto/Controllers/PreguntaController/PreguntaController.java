package com.example.proyecto.Controllers.PreguntaController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;

import com.example.proyecto.Models.Entity.CategoriaCriterio;
import com.example.proyecto.Models.Entity.Ponderacion;
import com.example.proyecto.Models.Entity.Pregunta;
import com.example.proyecto.Models.Service.ICategoriaCriterioService;
import com.example.proyecto.Models.Service.IPreguntaService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




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

    @PostMapping("/PreguntaF")
    public ResponseEntity<String> postMethodName(@Validated Pregunta pregunta,@RequestParam(name = "categoriaCriterio")Long id_categoria_criterio) {
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
    public String editar_pregunta(Model model, HttpServletRequest request,@PathVariable(name = "id_pregunta")Long id_pregunta) {
        if (request.getSession().getAttribute("usuario") != null) {

            model.addAttribute("pre", preguntaService.findOne(id_pregunta));
            model.addAttribute("preguntas", preguntaService.findAll());
            model.addAttribute("categorias", categoriaCriterioService.findAll());
            return "pregunta/gestionar-pregunta";
        }else{

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
