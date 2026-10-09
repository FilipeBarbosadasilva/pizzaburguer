package com.pizzaburguer.controller;

import com.pizzaburguer.model.Usuario;
import com.pizzaburguer.service.RecuperacaoSenhaService;
import com.pizzaburguer.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.SecurityContextRepository;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock private UsuarioService usuarioService;
    @Mock private RecuperacaoSenhaService recuperacaoSenhaService;
    @Mock private SecurityContextRepository securityContextRepository;

    private AuthController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthController(usuarioService, recuperacaoSenhaService, securityContextRepository);
    }

    @Test
    void loginPreservesResponseAndCreatesRoleBasedHttpSession() {
        Usuario admin = new Usuario();
        admin.setId(7L);
        admin.setNome("Admin");
        admin.setEmail("admin@example.com");
        admin.setTipo(Usuario.TipoUsuario.ADMIN);
        when(usuarioService.autenticar("admin@example.com", "secret")).thenReturn(Optional.of(admin));
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        ResponseEntity<Map<String, Object>> result = controller.login(
                Map.of("email", "admin@example.com", "senha", "secret"), request, response);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(Map.of(
                        "id", 7L,
                        "nome", "Admin",
                        "email", "admin@example.com",
                        "tipo", "ADMIN",
                        "telefone", ""),
                result.getBody());
        ArgumentCaptor<SecurityContext> contextCaptor = ArgumentCaptor.forClass(SecurityContext.class);
        verify(securityContextRepository).saveContext(contextCaptor.capture(), same(request), same(response));
        assertTrue(contextCaptor.getValue().getAuthentication().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void failedLoginDoesNotCreateAnAuthenticatedSession() {
        when(usuarioService.autenticar(anyString(), anyString())).thenReturn(Optional.empty());

        ResponseEntity<Map<String, Object>> result = controller.login(
                Map.of("email", "wrong@example.com", "senha", "wrong"),
                new MockHttpServletRequest(), new MockHttpServletResponse());

        assertEquals(401, result.getStatusCode().value());
        assertEquals(Map.of("erro", "Credenciais inválidas"), result.getBody());
        verifyNoInteractions(securityContextRepository);
    }

    @Test
    void reportsWhenRecoveryEmailCannotBeSent() {
        doThrow(new MailSendException("SMTP unavailable"))
                .when(recuperacaoSenhaService).solicitarRecuperacao("cliente@example.com");

        ResponseEntity<Map<String, String>> result =
                controller.recuperarSenha(new AuthController.EmailRequest("cliente@example.com"));

        assertEquals(503, result.getStatusCode().value());
        assertEquals(
                "Não foi possível enviar o e-mail de recuperação agora. Tente novamente mais tarde.",
                result.getBody().get("erro"));
    }
}
