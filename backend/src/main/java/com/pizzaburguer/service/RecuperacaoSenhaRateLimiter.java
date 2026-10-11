package com.pizzaburguer.service;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class RecuperacaoSenhaRateLimiter {
    private static final int MAX_REQUESTS = 3;
    private static final long WINDOW_NANOS = Duration.ofMinutes(15).toNanos();

    private final ConcurrentHashMap<String, RequestWindow> windows = new ConcurrentHashMap<>();
    private final AtomicLong requestsSeen = new AtomicLong();

    public boolean podeSolicitar(String endereco, String email) {
        if (!registrar("ip:" + hash(endereco))) {
            return false;
        }
        boolean permitido = registrar("email:" + hash(email));
        if ((requestsSeen.incrementAndGet() & 255) == 0) {
            removerJanelasExpiradas();
        }
        return permitido;
    }

    private boolean registrar(String chave) {
        long agora = System.nanoTime();
        AtomicBoolean permitido = new AtomicBoolean();
        windows.compute(chave, (ignored, janela) -> {
            if (janela == null || agora - janela.iniciadaEm() >= WINDOW_NANOS) {
                permitido.set(true);
                return new RequestWindow(agora, 1);
            }
            if (janela.solicitacoes() >= MAX_REQUESTS) {
                return janela;
            }
            permitido.set(true);
            return new RequestWindow(janela.iniciadaEm(), janela.solicitacoes() + 1);
        });
        return permitido.get();
    }

    private void removerJanelasExpiradas() {
        long agora = System.nanoTime();
        windows.entrySet().removeIf(entry ->
                agora - entry.getValue().iniciadaEm() >= WINDOW_NANOS);
    }

    private static String hash(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 não está disponível.", exception);
        }
    }

    private record RequestWindow(long iniciadaEm, int solicitacoes) {}
}
