package com.example.proyecto.Controllers.CategoriaCriteriosControllers;

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

import com.example.proyecto.Models.Entity.CategoriaCriterio;
import com.example.proyecto.Models.Entity.TipoProyecto;
import com.example.proyecto.Models.IService.ICategoriaCriterioService;
import com.example.proyecto.Models.IService.ITipoProyectoService;

import jakarta.servlet.http.HttpServletRequest;



@Controller
public class CategoriaCriterioController {
    
    @Autowired
    private ITipoProyectoService tipoProyectoService;

    @Autowired
    private ICategoriaCriterioService categoriaCriterioService;
    @GetMapping("/Categoria_CriterioR")
    public String Categoria_CriterioR(Model model, HttpServletRequest request) {
        if (request.getSession().getAttribute("usuario") != null) {

            model.addAttribute("categoria", new CategoriaCriterio());
            model.addAttribute("tipoProyectos", tipoProyectoService.findAll());
            return "categoriaCriterio/gestionar-categoriaCriterio";
        }else{
            return "redirect:/LoginR";
        }
    }

    @PostMapping("/CategoriaCriterioF")
    public ResponseEntity<String> CategoriaCriterioF(@Validated CategoriaCriterio categoriaCriterio,
            @RequestParam(name = "tipoProyecto") Long id_tipoProyecto) {
         TipoProyecto tipoProyecto = tipoProyectoService.findOne(id_tipoProyecto);
        if (tipoProyecto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tipo Proyecto no encontrada");
        }

        if (categoriaCriterio.getId_categoria_criterio() == null) {
            // Nueva 
            categoriaCriterio.setEstado("A");
            categoriaCriterio.setTipoProyecto(tipoProyecto);
            categoriaCriterioService.save(categoriaCriterio);
            return ResponseEntity.ok("1");
        } else {
            // Actualización 
            CategoriaCriterio existingCategoriaCriterio = categoriaCriterioService.findOne(categoriaCriterio.getId_categoria_criterio());
            if (existingCategoriaCriterio != null) {
                // Actualiza solo los campos necesarios
                existingCategoriaCriterio.setTipoProyecto(tipoProyecto);
                existingCategoriaCriterio.setNombre_cat_criterio(categoriaCriterio.getNombre_cat_criterio().toUpperCase());
                categoriaCriterioService.save(existingCategoriaCriterio);
                return ResponseEntity.ok("2");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Categoria Criterio no encontrada");
            }
        }
    }
    
    
    @GetMapping("/editar_categoria/{id_categoria_criterio}")
    public String editar_categoria(Model model, HttpServletRequest request,
            @PathVariable(name = "id_categoria_criterio") Long id_categoria_criterio) {
        if (request.getSession().getAttribute("usuario") != null) {

            model.addAttribute("categoria", categoriaCriterioService.findOne(id_categoria_criterio));
            model.addAttribute("tipoProyectos", tipoProyectoService.findAll());
            model.addAttribute("edit", "true");
            return "categoriaCriterio/gestionar-categoriaCriterio";
        } else {

            return "redirect:/LoginR";
        }
    }

    @GetMapping("/eliminar_criterio/{id_categoria_criterio}")
    @ResponseBody
    public void getMethodName(@PathVariable(name = "id_categoria_criterio")Long id_categoria_criterio) {
        CategoriaCriterio categoriaCriterio = categoriaCriterioService.findOne(id_categoria_criterio);
        categoriaCriterio.setEstado("X");
        categoriaCriterioService.save(categoriaCriterio);
    }
}