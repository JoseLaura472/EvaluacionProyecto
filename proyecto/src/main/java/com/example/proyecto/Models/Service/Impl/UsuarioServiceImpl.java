package com.example.proyecto.Models.Service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IUsuarioDao;
import com.example.proyecto.Models.Entity.Usuario;
import com.example.proyecto.Models.Service.IUsuarioService;

@Service
public class UsuarioServiceImpl  implements IUsuarioService{
    
    
    @Autowired
    private IUsuarioDao usuarioDao;

    @Override
    public List<Usuario> findAll() {
        // TODO Auto-generated method stub
        return (List<Usuario>) usuarioDao.findAll();
    }

    @Override
    public void save(Usuario usuario) {
        // TODO Auto-generated method stub
        usuarioDao.save(usuario);
    }

    @Override
    public Usuario findOne(Long id) {
        // TODO Auto-generated method stub
        return usuarioDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        usuarioDao.deleteById(id);
    }

    @Override
    public Usuario getUsuarioContraseña(String correo, String password) {
        // TODO Auto-generated method stub
        return usuarioDao.getUsuarioContraseña(correo, password);
    }

    @Override
    public Usuario getUsuarioPersona(Long id_persona) {
        // TODO Auto-generated method stub
        return usuarioDao.getUsuarioPersona(id_persona);
    }
}
