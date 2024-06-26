package com.example.proyecto.Controllers.PonderacionController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;

import com.example.proyecto.Models.Entity.Ponderacion;
import com.example.proyecto.Models.Entity.Pregunta;
import com.example.proyecto.Models.Service.ICategoriaCriterioService;
import com.example.proyecto.Models.Service.IPonderacionService;
import com.example.proyecto.Models.Service.IPreguntaService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
public class PonderacionController {
    
    @Autowired
    private IPonderacionService ponderacionService;

    @Autowired
    private IPreguntaService preguntaService;

    @GetMapping("/PonderacionR")
    public String PonderacionR(Model model, HttpServletRequest request) {
        if (request.getSession().getAttribute("usuario") != null) {

            model.addAttribute("pon", new Ponderacion());
            model.addAttribute("preguntas", preguntaService.findAll());
            return "ponderacion/gestionar-ponderacion";
        }else{

            return "redirect:/LoginR";
        }
    }

    @PostMapping("/PonderacionF")
    public ResponseEntity<String> PonderacionF(@RequestParam(name = "preguntas") Long id_pregunta,
            @Validated Ponderacion ponderacion) {
        Pregunta pregunta = preguntaService.findOne(id_pregunta);
        if (pregunta == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Pregunta no encontrada");
        }

        if (ponderacion.getId_ponderacion() == null) {
            // Nueva Ponderación
            ponderacion.setEstado("A");
            ponderacion.setPreguntas(pregunta);
            ponderacionService.save(ponderacion);
            return ResponseEntity.ok("1");
        } else {
            // Actualización de Ponderación existente
            Ponderacion existingPonderacion = ponderacionService.findOne(ponderacion.getId_ponderacion());
            if (existingPonderacion != null) {
                // Actualiza solo los campos necesarios
                existingPonderacion.setPreguntas(pregunta);
                existingPonderacion.setNum_ponderacion(ponderacion.getNum_ponderacion());
                ponderacionService.save(existingPonderacion);
                return ResponseEntity.ok("2");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ponderacion no encontrada");
            }
        }
    }

    @GetMapping("/editar_ponderacion/{id_ponderacion}")
    public String editar_ponderacion(@PathVariable(name = "id_ponderacion")Long id_ponderacion, Model model , HttpServletRequest request) {
        if (request.getSession().getAttribute("usuario") != null) {

            model.addAttribute("pon", ponderacionService.findOne(id_ponderacion));
            model.addAttribute("preguntas", preguntaService.findAll());
            model.addAttribute("edit", "true");
            return "ponderacion/gestionar-ponderacion";
        }else{

            return "redirect:/LoginR";
        }
    }

    @GetMapping("/eliminar_ponderacion/{id_ponderacion}")
    @ResponseBody
    public void eliminar_ponderacion(@PathVariable(name = "id_ponderacion")Long id_ponderacion) {

        Ponderacion ponderacion = ponderacionService.findOne(id_ponderacion);
        ponderacion.setEstado("X");
        ponderacionService.save(ponderacion);
    }
    
    
}
