package com.example.proyecto.Models.IServiceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.proyecto.Models.Dao.IUsuarioDao;
import com.example.proyecto.Models.Entity.Persona;
import com.example.proyecto.Models.Entity.Usuario;
import com.example.proyecto.Models.IService.IUsuarioService;
import com.example.proyecto.Models.Utils.CredencialHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {


    private final IUsuarioDao usuarioDao;
    private final PasswordEncoder passwordEncoder; // BCryptPasswordEncoder bean
    private final CredencialHelper credencialHelper;

    @Override
    public List<Usuario> findAll() {
        return (List<Usuario>) usuarioDao.findAll();
    }

    @Override
    public void save(Usuario usuario) {
        usuarioDao.save(usuario);
    }

    @Override
    public Usuario findOne(Long id) {
        return usuarioDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        usuarioDao.deleteById(id);
    }

    @Override
    public Usuario getUsuarioContraseña(String correo, String password) {
        return usuarioDao.getUsuarioContraseña(correo, password);
    }

    @Override
    public Usuario getUsuarioPersona(Long idPersona) {
        return usuarioDao.getUsuarioPersona(idPersona);
    }

    @Override
    public Usuario buscarPorNombreUser(String nombre) {
        return usuarioDao.buscarPorNombreUser(nombre);
    }

    @Override
    public Optional<Usuario> findByUsuario(String usuarioNom) {
        return usuarioDao.findByUsuario(usuarioNom);
    }

    @Override
    public boolean existsByPersona_IdPersona(Long idPersona) {
        return usuarioDao.existsByPersona_IdPersona(idPersona);
    }

    @Override
    public Optional<Usuario> findByPersona_IdPersona(Long idPersona) {
        return usuarioDao.findByPersona_IdPersona(idPersona);
    }

    @Override
    @Transactional
    public Usuario crearUsuarioParaJuradoSiNoExiste(Persona persona,
                                                    String usuarioNomDeseado,
                                                    String contrasenaPlano) {

        return usuarioDao.findByPersona_IdPersona(persona.getIdPersona())
            .orElseGet(() -> {
                if (contrasenaPlano == null || contrasenaPlano.length() < 6) {
                    throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
                }

                // 1) Resolver username
                String username = (usuarioNomDeseado != null && !usuarioNomDeseado.isBlank())
                                  ? usuarioNomDeseado.trim().toLowerCase()
                                  : credencialHelper.sugerirUsuario(persona); // por CI/email/id

                // 2) Asegurar unicidad
                String base = username; int suf = 1;
                while (usuarioDao.findByUsuario(username).isPresent()) {
                    username = base + (++suf);
                }

                // 3) Crear usuario
                Usuario u = new Usuario();
                u.setUsuario(username);               // asegúrate que el campo en entidad sea "usuario"
                u.setContrasena(passwordEncoder.encode(contrasenaPlano));
                u.setEstado("J");                     // jurado
                u.setPersona(persona);

                return usuarioDao.save(u);
            });
    }
}
