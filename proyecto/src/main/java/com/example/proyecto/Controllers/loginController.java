package com.example.proyecto.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.proyecto.Models.Entity.Proyecto;
import com.example.proyecto.Models.Entity.Usuario;
import com.example.proyecto.Models.Service.IProyectoService;
import com.example.proyecto.Models.Service.ITipoProyectoService;
import com.example.proyecto.Models.Service.IUsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class loginController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IProyectoService proyectoService;

    @Autowired
    private ITipoProyectoService tipoProyectoService;

    // Funcion de visualizacion de iniciar sesiòn administrador
    @RequestMapping(value = "/LoginR", method = RequestMethod.GET)
    public String LoginR(Model model) {

        return "index";
    }
    @RequestMapping(value = "/Login", method = RequestMethod.GET)
    public String LoginRr(Model model) {

        return "index2";
    }

    // Funciòn de iniciar sesiòn administrador
    @RequestMapping(value = "/LogearseF", method = RequestMethod.POST)
    public String logearseF(@RequestParam(value = "usuario") String user,
            @RequestParam(value = "contrasena") String contrasena, Model model, HttpServletRequest request,
            RedirectAttributes flash) {
        // los dos parametros de usuario, contraseña vienen del formulario html
        Usuario usuario = usuarioService.getUsuarioContraseña(user, contrasena);

        if (usuario != null) {
            if (usuario.getEstado().equals("X")) {
                return "redirect:/cerrar_sesionAdm";
            }
            HttpSession sessionAdministrador = request.getSession(true);

            sessionAdministrador.setAttribute("usuario", usuario);
            sessionAdministrador.setAttribute("persona", usuario.getPersona());

            flash.addAttribute("success", usuario.getPersona().getNombres());

            return "redirect:/AdmPG";

        } else {
            return "redirect:/LoginR";
        }

    }

    // Funcion de visualizaciòn de la pagina principal
    @RequestMapping(value = "/AdmPG", method = RequestMethod.GET) // Pagina principal
	public String Inicio(HttpServletRequest request, Model model) {
		if (request.getSession().getAttribute("usuario") != null) {

            HttpSession sessionAdministrador = request.getSession();
            Usuario usuario = (Usuario) sessionAdministrador.getAttribute("usuario");
            
            List<Proyecto> PrimerL = proyectoService.Primerlugar();
            List<Proyecto> SegundoL = proyectoService.Segundolugar();
            List<Proyecto> TercerL = proyectoService.Tercerlugar();
            // List<Proyecto> Ranking = proyectoService.proyectosRanking();

            model.addAttribute("tiposProyectos", tipoProyectoService.findAll());
            model.addAttribute("PrimerL", PrimerL);
			model.addAttribute("SegundoL", SegundoL);
            model.addAttribute("TercerL", TercerL);
            // model.addAttribute("Ranking", Ranking);

            model.addAttribute("usuario", usuario);
			return "Inicio";
		} else {
			return "redirect:LoginR";
		}
	}

    @PostMapping("/tabla-ranking-tipo-proyecto/{idTp}")
	public String cargarTablaRegistrosEstudiante(Model model, @PathVariable Long idTp) throws Exception {
		// List<Estudiante> listaEstudiantes = iEstudianteService.findAll();
		List<Proyecto> listaProyectos = proyectoService.RankingPorTipoProyecto(idTp);

		model.addAttribute("listaProyectos", listaProyectos);

		return "proyecto/tabla-registro-proyecto-tp";
	}

    @RequestMapping(value = "/Tecnologia", method = RequestMethod.GET) // Pagina principal
	public String InicioA(HttpServletRequest request, Model model) {
		if (request.getSession().getAttribute("usuario") != null) {

            HttpSession sessionAdministrador = request.getSession();
            Usuario usuario = (Usuario) sessionAdministrador.getAttribute("usuario");
            
            List<Proyecto> Ranking = proyectoService.proyectosRankingTecnologia();

            model.addAttribute("RankingT", Ranking);
            model.addAttribute("usuario", usuario);

			return "ranking/ranking-tecnologia";
		} else {
			return "redirect:LoginR";
		}
	}

    @RequestMapping(value = "/Empredimiento", method = RequestMethod.GET) // Pagina principal
	public String InicioB(HttpServletRequest request, Model model) {
		if (request.getSession().getAttribute("usuario") != null) {

            HttpSession sessionAdministrador = request.getSession();
            Usuario usuario = (Usuario) sessionAdministrador.getAttribute("usuario");
            
            List<Proyecto> Ranking = proyectoService.proyectosRankingEmprendimiento();

            model.addAttribute("RankingE", Ranking);
            model.addAttribute("usuario", usuario);

			return "ranking/ranking-emprendimiento";
		} else {
			return "redirect:LoginR";
		}
	}

    @RequestMapping(value = "/Salud", method = RequestMethod.GET) // Pagina principal
	public String InicioC(HttpServletRequest request, Model model) {
		if (request.getSession().getAttribute("usuario") != null) {

            HttpSession sessionAdministrador = request.getSession();
            Usuario usuario = (Usuario) sessionAdministrador.getAttribute("usuario");
            
            List<Proyecto> Ranking = proyectoService.proyectosRankingSalud();

            model.addAttribute("RankingS", Ranking);
            model.addAttribute("usuario", usuario);

			return "ranking/ranking-salud";
		} else {
			return "redirect:LoginR";
		}
	}

    // Funcion de cerrar sesion de administrador
    @RequestMapping("/cerrar_sesionAdm")
    public String cerrarSesion2(HttpServletRequest request, RedirectAttributes flash) {
        HttpSession sessionAdministrador = request.getSession();
        if (sessionAdministrador != null) {
            sessionAdministrador.invalidate();
            flash.addAttribute("validado", "Se cerro sesion con exito!");
        }
        return "redirect:/LoginR";
    }

}
