package com.pizzaburguer.controller;

import com.pizzaburguer.model.Usuario;
import com.pizzaburguer.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String senha = payload.get("senha");

        Optional<Usuario> usuario = usuarioService.autenticar(email, senha);
        if (usuario.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("erro", "Credenciais inválidas"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", usuario.get().getId());
        response.put("nome", usuario.get().getNome());
        response.put("email", usuario.get().getEmail());
        response.put("tipo", usuario.get().getTipo().name());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Map<String, Object>> cadastro(@Valid @RequestBody Usuario usuario) {
        try {
            Usuario salvo = usuarioService.cadastrar(usuario);
            Map<String, Object> response = new HashMap<>();
            response.put("id", salvo.getId());
            response.put("nome", salvo.getNome());
            response.put("email", salvo.getEmail());
            response.put("tipo", salvo.getTipo().name());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }
}
