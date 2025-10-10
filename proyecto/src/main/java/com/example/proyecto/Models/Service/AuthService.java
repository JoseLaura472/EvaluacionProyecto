package com.example.proyecto.Models.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IUsuarioDao;
import com.example.proyecto.Models.Entity.Usuario;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final IUsuarioDao usuarioRepo;
    private final PasswordEncoder passwordEncoder;

    public Usuario autenticar(String usuarioNom, String contrasenaPlano) {
        return usuarioRepo.findByUsuario(usuarioNom)
                .filter(u -> passwordEncoder.matches(contrasenaPlano, u.getContrasena()))
                .orElse(null);
    }
}
