package com.example.proyecto;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.proyecto.Models.Entity.Persona;
import com.example.proyecto.Models.Entity.Usuario;
import com.example.proyecto.Models.Service.IPersonaService;
import com.example.proyecto.Models.Service.IUsuarioService;

@SpringBootApplication
public class ProyectoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoApplication.class, args);
	}

	@Bean
	ApplicationRunner init(IUsuarioService usuarioService, IPersonaService personaService) {

		return args -> {
			Persona persona = personaService.getPersonaCI("123");
			if (persona == null) {
				persona = new Persona();
				persona.setNombres("JEFE DE SISTEMA");
				persona.setPaterno("JFS1");
				persona.setMaterno("JFS2");
				persona.setCi("123");
				persona.setEstado("A");
				personaService.save(persona);
			}
			Usuario usuario = usuarioService.buscarPorNombreUser("admin1");
			if (usuario == null) {
				usuario = new Usuario();
				usuario.setUsuario_nom("admin1");
				usuario.setContrasena("123");
				usuario.setPersona(persona);
				usuario.setEstado("A");
				usuarioService.save(usuario);
			}
		};

	}
}
