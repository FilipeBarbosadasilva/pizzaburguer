package com.pizzaburguer.service;

import com.pizzaburguer.model.Usuario;
import com.pizzaburguer.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock private UsuarioRepository usuarioRepository;

    @Test
    void publicRegistrationAlwaysCreatesANewClientWithHashedPassword() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        UsuarioService service = new UsuarioService(usuarioRepository, passwordEncoder);
        Usuario submitted = new Usuario();
        submitted.setId(999L);
        submitted.setNome("Cliente");
        submitted.setEmail("cliente@example.com");
        submitted.setSenha("secret123");
        submitted.setTipo(Usuario.TipoUsuario.ADMIN);
        when(usuarioRepository.findByEmail(submitted.getEmail())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario created = service.cadastrar(submitted);

        assertNull(created.getId());
        assertEquals(Usuario.TipoUsuario.CLIENTE, created.getTipo());
        assertNotEquals("secret123", created.getSenha());
        assertTrue(passwordEncoder.matches("secret123", created.getSenha()));
        verify(usuarioRepository).save(submitted);
    }
}
