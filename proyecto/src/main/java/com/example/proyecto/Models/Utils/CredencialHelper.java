package com.example.proyecto.Models.Utils;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;

import com.example.proyecto.Models.Entity.Persona;

@Component
public class CredencialHelper {
        public String sugerirUsuario(Persona p) {
        if (p.getCi()!=null && !p.getCi().isBlank())
            return p.getCi().replaceAll("[^0-9A-Za-z]", "").toLowerCase();
        if (p.getEmail()!=null && !p.getEmail().isBlank())
            return p.getEmail().split("@")[0].toLowerCase();
        return "u" + p.getIdPersona();
    }
    public String generarPasswordTemporal(int len) {
        String ab = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
        StringBuilder sb = new StringBuilder();
        ThreadLocalRandom r = ThreadLocalRandom.current();
        for (int i=0;i<len;i++) sb.append(ab.charAt(r.nextInt(ab.length())));
        return sb.toString();
    }
}
