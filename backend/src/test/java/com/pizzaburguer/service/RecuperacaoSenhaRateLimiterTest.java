package com.pizzaburguer.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecuperacaoSenhaRateLimiterTest {
    @Test
    void limitsRecoveryRequestsByAddressAndEmail() {
        RecuperacaoSenhaRateLimiter limiter = new RecuperacaoSenhaRateLimiter();

        assertTrue(limiter.podeSolicitar("127.0.0.1", "cliente@example.com"));
        assertTrue(limiter.podeSolicitar("127.0.0.1", "cliente@example.com"));
        assertTrue(limiter.podeSolicitar("127.0.0.1", "cliente@example.com"));
        assertFalse(limiter.podeSolicitar("127.0.0.1", "cliente@example.com"));
        assertFalse(limiter.podeSolicitar("192.0.2.1", "cliente@example.com"));
    }

    @Test
    void allowsRequestsFromDifferentAddressesForAnotherEmailKey() {
        RecuperacaoSenhaRateLimiter limiter = new RecuperacaoSenhaRateLimiter();

        assertTrue(limiter.podeSolicitar("127.0.0.1", "cliente@example.com"));
        assertTrue(limiter.podeSolicitar("127.0.0.2", "CLIENTE@example.com"));
    }
}
