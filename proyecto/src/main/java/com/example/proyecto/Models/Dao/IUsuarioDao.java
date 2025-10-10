package com.example.proyecto.Models.Dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.proyecto.Models.Entity.Usuario;

public interface IUsuarioDao extends CrudRepository<Usuario, Long>{
    
    @Query("select u from Usuario u where u.usuario = ?1 and u.contrasena = ?2")
    public Usuario getUsuarioContraseña(String correo, String password);

    @Query("select u from Usuario u left join u.persona p where p.idPersona = ?1 and u.estado != 'T' ")
    public Usuario getUsuarioPersona(Long idPersona);

    @Query("SELECT u FROM Usuario u WHERE u.usuario = ?1  AND u.estado = 'A'")
    Usuario buscarPorNombreUser(String nombre);

    //* de srkiwi, lo que preciso, los demas eliminar */
    Optional<Usuario> findByUsuario(String usuarioNom);
    boolean existsByPersona_IdPersona(Long idPersona);
    Optional<Usuario> findByPersona_IdPersona(Long idPersona);
}
