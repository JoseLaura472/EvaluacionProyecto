package com.example.proyecto.Models.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.proyecto.Models.Dto.CronometroDTO;

@Service
public class CronometroSseService {
    private static final Logger logger = LoggerFactory.getLogger(CronometroSseService.class);
    // Lista thread-safe de emitters conectados
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final AtomicReference<CronometroDTO> lastState = new AtomicReference<>(null);

    public CronometroSseService() {
        // Enviar heartbeat cada 25 segundos (ajusta según infra)
        scheduler.scheduleAtFixedRate(this::sendHeartbeat, 25, 25, TimeUnit.SECONDS);
    }

    public SseEmitter crearEmitter() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        CronometroDTO snapshot = lastState.get();
        if (snapshot != null) {
            try {
                emitter.send(SseEmitter.event().name("cronometro").data(snapshot));
                logger.info("[SSE] enviado lastState al nuevo emitter");
            } catch (IOException e) {
                logger.warn("[SSE] fallo al enviar lastState al nuevo emitter: {}", e.getMessage());
            }
        }

        return emitter;
    }

    public void broadcast(CronometroDTO data) {
        lastState.set(data);
        List<SseEmitter> deadEmitters = new ArrayList<>();
        System.out.println("[SSE] Broadcast -> emitters actuales: " + emitters.size());

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("cronometro").data(data));
                System.out.println("[SSE] enviado ok a un emitter");
            } catch (IOException e) {
                System.err.println("[SSE] fallo al enviar a un emitter: " + e.getMessage());
                deadEmitters.add(emitter);
            } catch (Exception ex) {
                System.err.println("[SSE] excepción al enviar: " + ex.getMessage());
                deadEmitters.add(emitter);
            }
        }

        if (!deadEmitters.isEmpty()) {
            emitters.removeAll(deadEmitters);
            System.out.println("[SSE] removed dead emitters, ahora: " + emitters.size());
        }
    }

    private void sendHeartbeat() {
        List<SseEmitter> dead = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("heartbeat").data("ping"));
            } catch (IOException e) {
                dead.add(emitter);
            }
        }
        emitters.removeAll(dead);
    }

    public int getClientesConectados() {
        return emitters.size();
    }
}
