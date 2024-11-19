package com.example.proyecto.Controllers.PonderacionController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.proyecto.Models.Entity.Ponderacion;
import com.example.proyecto.Models.Entity.Pregunta;
import com.example.proyecto.Models.Service.IPonderacionService;
import com.example.proyecto.Models.Service.IPreguntaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

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
        } else {

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

    @PostMapping("/PonderacionF2")
    public ResponseEntity<String> PonderacionF2(@RequestParam(name = "preguntas") Long id_pregunta,
            @Validated Ponderacion pond, @RequestParam("valorPond") String valores)
            throws JsonMappingException, JsonProcessingException {
        Pregunta pregunta = preguntaService.findOne(id_pregunta);
        if (pregunta == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Pregunta no encontrada");
        }

        // Convertir el String JSON a una lista de Mapas
        ObjectMapper objectMapper = new ObjectMapper();

        // Deserializamos el JSON recibido en un List de Map (cada uno con un campo
        // "value")
        List<Object> parsedList = objectMapper.readValue(valores, List.class);

        // Extraemos solo los valores de "value" usando stream
        List<String> values = parsedList.stream()
                .map(item -> (String) ((java.util.Map<?, ?>) item).get("value"))
                .collect(Collectors.toList());

        for (String string : values) {
            System.out.println(string);
            Ponderacion ponderacion = new Ponderacion();
            ponderacion.setNum_ponderacion(Integer.parseInt(string));
            ponderacion.setPreguntas(pond.getPreguntas());
            ponderacion.setEstado("A");
            ponderacion.setPreguntas(pregunta);
            ponderacionService.save(ponderacion);
        }

        return ResponseEntity.ok("2");
    }

    @GetMapping("/editar_ponderacion/{id_ponderacion}")
    public String editar_ponderacion(@PathVariable(name = "id_ponderacion") Long id_ponderacion, Model model,
            HttpServletRequest request) {
        if (request.getSession().getAttribute("usuario") != null) {

            model.addAttribute("pon", ponderacionService.findOne(id_ponderacion));
            model.addAttribute("preguntas", preguntaService.findAll());
            model.addAttribute("edit", "true");
            return "ponderacion/gestionar-ponderacion";
        } else {

            return "redirect:/LoginR";
        }
    }

    @GetMapping("/eliminar_ponderacion/{id_ponderacion}")
    @ResponseBody
    public void eliminar_ponderacion(@PathVariable(name = "id_ponderacion") Long id_ponderacion) {

        Ponderacion ponderacion = ponderacionService.findOne(id_ponderacion);
        ponderacion.setEstado("X");
        ponderacionService.save(ponderacion);
    }

}
