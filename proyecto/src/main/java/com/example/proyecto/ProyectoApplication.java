package com.example.proyecto;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.proyecto.Models.Entity.Persona;
import com.example.proyecto.Models.Entity.Usuario;
import com.example.proyecto.Models.IService.IPersonaService;
import com.example.proyecto.Models.IService.IUsuarioService;

import lombok.RequiredArgsConstructor;

@SpringBootApplication
@RequiredArgsConstructor
public class ProyectoApplication {

	private final PasswordEncoder passwordEncoder;

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
				usuario.setUsuario("admin1");
				usuario.setContrasena(passwordEncoder.encode("123"));
				usuario.setPersona(persona);
				usuario.setEstado("A");
				usuarioService.save(usuario);
			}
		};

	}
}
