package com.example.proyecto.Models.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.proyecto.Models.Dto.CronometroDTO;

@Service
public class CronometroSseService {
    // Lista thread-safe de emitters conectados
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    /**
     * Crear nueva conexión SSE
     */
    public SseEmitter crearEmitter() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // Sin timeout
        
        emitters.add(emitter);
        
        // Remover cuando se complete o expire
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));
        
        return emitter;
    }

    /**
     * Broadcast a todos los clientes conectados
     */
    public void broadcast(CronometroDTO data) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("cronometro")
                        .data(data));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }); 
        
        // Remover emitters muertos
        emitters.removeAll(deadEmitters);
    }

    /**
     * Obtener número de clientes conectados
     */
    public int getClientesConectados() {
        return emitters.size();
    }
}
