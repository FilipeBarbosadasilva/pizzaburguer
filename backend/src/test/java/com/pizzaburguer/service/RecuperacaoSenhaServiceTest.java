package com.pizzaburguer.service;

import com.pizzaburguer.model.TokenRecuperacao;
import com.pizzaburguer.model.Usuario;
import com.pizzaburguer.repository.TokenRecuperacaoRepository;
import com.pizzaburguer.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecuperacaoSenhaServiceTest {
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private TokenRecuperacaoRepository tokenRepository;
    @Mock private JavaMailSender mailSender;

    private BCryptPasswordEncoder passwordEncoder;
    private RecuperacaoSenhaService service;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        service = new RecuperacaoSenhaService(
                usuarioRepository, tokenRepository, mailSender, passwordEncoder,
                "http://localhost:8080/", "no-reply@pizzaburguer.local");
    }

    @Test
    void sendsTimeLimitedRecoveryLinkForKnownEmail() {
        Usuario usuario = new Usuario();
        usuario.setEmail("cliente@example.com");
        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));

        service.solicitarRecuperacao(usuario.getEmail());

        ArgumentCaptor<TokenRecuperacao> tokenCaptor = ArgumentCaptor.forClass(TokenRecuperacao.class);
        verify(tokenRepository).save(tokenCaptor.capture());
        TokenRecuperacao token = tokenCaptor.getValue();
        assertEquals(usuario, token.getUsuario());
        assertFalse(token.isUsado());
        assertTrue(token.getExpiraEm().isAfter(LocalDateTime.now()));
        assertTrue(token.getExpiraEm().isBefore(LocalDateTime.now().plusMinutes(31)));

        ArgumentCaptor<SimpleMailMessage> mailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(mailCaptor.capture());
        assertNotNull(mailCaptor.getValue().getTo());
        assertEquals("cliente@example.com", mailCaptor.getValue().getTo()[0]);
        assertNotNull(mailCaptor.getValue().getText());
        assertTrue(mailCaptor.getValue().getText().contains("/login.html?token=" + token.getToken()));
    }

    @Test
    void resetsPasswordAndConsumesValidToken() {
        Usuario usuario = new Usuario();
        usuario.setSenha("old-password-hash");
        TokenRecuperacao token = new TokenRecuperacao();
        token.setUsuario(usuario);
        token.setToken("valid-token");
        token.setExpiraEm(LocalDateTime.now().plusMinutes(10));
        when(tokenRepository.findByTokenForUpdate("valid-token")).thenReturn(Optional.of(token));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertTrue(service.redefinirSenha("valid-token", "new-password"));

        assertTrue(passwordEncoder.matches("new-password", usuario.getSenha()));
        assertTrue(token.isUsado());
        verify(usuarioRepository).save(usuario);
        verify(tokenRepository).save(token);
    }

    @Test
    void refusesExpiredOrConsumedTokens() {
        TokenRecuperacao token = new TokenRecuperacao();
        token.setToken("expired-token");
        token.setExpiraEm(LocalDateTime.now().minusSeconds(1));
        when(tokenRepository.findByTokenForUpdate("expired-token")).thenReturn(Optional.of(token));

        assertFalse(service.redefinirSenha("expired-token", "new-password"));
        verify(usuarioRepository, never()).save(any());
        verify(tokenRepository, never()).save(any());
    }
}
