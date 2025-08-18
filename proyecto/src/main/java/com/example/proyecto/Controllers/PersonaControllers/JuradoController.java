package com.example.proyecto.Controllers.PersonaControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.proyecto.Models.Entity.Jurado;
import com.example.proyecto.Models.Entity.Persona;
import com.example.proyecto.Models.Service.IJuradoService;
import com.example.proyecto.Models.Service.IPersonaService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/jurado")
public class JuradoController {
    
    @Autowired
    private IPersonaService personaService;

    @Autowired
    private IJuradoService juradoService;

	@GetMapping("/vista")
    public String inicio() {
        return "jurado/vista";
    }

	@PostMapping("/tabla-registros")
    public String tablaRegistros(Model model) throws Exception {
        List<Jurado> listaJurados = juradoService.findAll();
        model.addAttribute("listaJurados", listaJurados);
        return "jurado/tabla_registro";
    }

	@GetMapping("/formulario")
    public String formulario(Model model, Jurado jurado) {
		model.addAttribute("persona", new Persona());
		model.addAttribute("jurado", new Jurado());
        return "jurado/formulario";
    }

	@GetMapping("/formulario-edit/{id_jurado}")
    public String formularioEdit(Model model, @PathVariable("id_jurado") Long idJurado) throws Exception{
        model.addAttribute("jurado", juradoService.findOne(idJurado));
		model.addAttribute("persona", new Persona());
        model.addAttribute("edit", "true");
        return "jurado/formulario";
    }

	@PostMapping("/registrar-jurado")
    public ResponseEntity<String> RegistrarPersona(HttpServletRequest request, @Validated Jurado jurado) {
        if (juradoService.findByNombreCompleto(jurado.getPersona().getNombreCompleto()) == null) {
            jurado.setEstado("A");
            juradoService.save(jurado);
            return ResponseEntity.ok("Se realizó el registro correctamente");
        } else {
            return ResponseEntity.ok("Ya existe un rol con este nombre");
        }
    }

	@PostMapping(value = "/modificar-jurado")
    public ResponseEntity<String> modificar(HttpServletRequest request, Jurado jurado,
            RedirectAttributes redirectAttrs) {
        jurado.setEstado("A");
        juradoService.save(jurado);
        return ResponseEntity.ok("Se realizó el registro correctamente");
    }

	@PostMapping("/eliminar/{id_rol}")
    public ResponseEntity<String> eliminar(Model model, @PathVariable("id_rol") Long idJurado) throws Exception {
        Jurado jurado = juradoService.findOne(idJurado);
        jurado.setEstado("ELIMINADO");
        juradoService.save(jurado);
        return ResponseEntity.ok("Registro Eliminado");
    }

	/* codigo antiguoa  */
	// FUNCION PARA LA VISUALIZACION DE REGISTRO DE MNACIONALIDAD
	@RequestMapping(value = "/JuradoR", method = RequestMethod.GET) // Pagina principal
	public String Jurado(HttpServletRequest request, Model model) {
		if (request.getSession().getAttribute("usuario") != null) {


			return "persona/gestionar-jurado";
		} else {
			return "redirect:LoginR";
		}
	}

	@RequestMapping(value = "/JuradoF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
	public ResponseEntity<String> DocenteF(@Validated Jurado jurado,@Validated Persona persona, RedirectAttributes redirectAttrs) { // validar los datos capturados (1)
		
		if (jurado.getId_jurado() == null && persona.getId_persona() == null) {
			persona.setEstado("E");
			personaService.save(persona);
			jurado.setPersona(persona);
			jurado.setEstado("A");
			juradoService.save(jurado);
			return ResponseEntity.ok("1");
		}else{
			Persona p = personaService.getPersonaCI(persona.getCi());
			if (p != null) {
				System.out.println("ya Existe un Registro Igual");
			}
			return ResponseEntity.ok("3");
		}
		
	}

	@GetMapping("/nuevo_registroJ")
	public String nuevo_registroJ(Model model) {

		model.addAttribute("persona", new Persona());
		model.addAttribute("jurado", new Jurado());
		return "persona/contentPersona :: Jurado";
	}
	
	@GetMapping("/tabla_jurados")
	public String tabla_jurados(Model model) {

		model.addAttribute("jurados", juradoService.findAll());

		return "persona/contentPersona :: Tabla_Jurados";
	}

	@RequestMapping(value = "/editar-jurado/{id_jurado}")
	public String editar_jurado(@PathVariable("id_jurado") Long id_jurado, Model model) {

        Jurado jurado = juradoService.findOne(id_jurado);

		model.addAttribute("jurado", jurado);
		model.addAttribute("persona", jurado.getPersona());
		model.addAttribute("edit", "true");
		return "persona/contentPersona :: Jurado";
	}

	@RequestMapping(value = "/JuradoModF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
	public ResponseEntity<String> JuradoModF(@Validated Jurado jurado, @Validated Persona persona,
			RedirectAttributes redirectAttrs) { // validar los datos capturados (1)
		
		persona.setEstado("A");
		personaService.save(persona);
		jurado.setPersona(persona);
		jurado.setEstado("A");
		juradoService.save(jurado);
		return ResponseEntity.ok("2");
	}

	@RequestMapping(value = "/eliminar-jurado/{id_jurado}")
	@ResponseBody
	public void eliminar_jurado(@PathVariable("id_jurado") Long id_jurado) {

		Jurado jurado = juradoService.findOne(id_jurado);

		jurado.setEstado("X");
		Persona persona = jurado.getPersona();

		persona.setEstado("X");

		personaService.save(persona);

		juradoService.save(jurado);

	}

}
