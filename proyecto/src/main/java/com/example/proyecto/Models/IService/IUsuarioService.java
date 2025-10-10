package com.example.proyecto.Models.IService;

import java.util.List;
import java.util.Optional;

import com.example.proyecto.Models.Entity.Persona;
import com.example.proyecto.Models.Entity.Usuario;

import jakarta.annotation.Nullable;

public interface IUsuarioService {
    
    public List<Usuario> findAll();
    
    public void save(Usuario usuario);

	public Usuario findOne(Long id);

	public void delete(Long id);

    public Usuario getUsuarioContraseña(String correo, String password);
	
    public Usuario getUsuarioPersona(Long idPersona);

    Usuario buscarPorNombreUser(String nombre);

    Optional<Usuario> findByUsuario(String usuarioNom);
    boolean existsByPersona_IdPersona(Long idPersona);
    Optional<Usuario> findByPersona_IdPersona(Long idPersona);
    
    Usuario crearUsuarioParaJuradoSiNoExiste(Persona persona,
                                             @Nullable String usuarioNomDeseado,
                                             String contrasenaPlano);
}
