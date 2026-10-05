package com.pizzaburguer.controller;

import com.pizzaburguer.model.Usuario;
import com.pizzaburguer.service.RecuperacaoSenhaService;
import com.pizzaburguer.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final UsuarioService usuarioService;
    private final RecuperacaoSenhaService recuperacaoSenhaService;
    private final SecurityContextRepository securityContextRepository;

    public AuthController(
            UsuarioService usuarioService,
            RecuperacaoSenhaService recuperacaoSenhaService,
            SecurityContextRepository securityContextRepository) {
        this.usuarioService = usuarioService;
        this.recuperacaoSenhaService = recuperacaoSenhaService;
        this.securityContextRepository = securityContextRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> payload,
            HttpServletRequest request,
            HttpServletResponse response) {
        String email = payload.get("email");
        String senha = payload.get("senha");

        Optional<Usuario> usuario = usuarioService.autenticar(email, senha);
        if (usuario.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("erro", "Credenciais inválidas"));
        }

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(
                usuario.get().getEmail(),
                null,
                java.util.List.of(new SimpleGrantedAuthority("ROLE_" + usuario.get().getTipo().name()))));
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        Map<String, Object> body = new HashMap<>();
        body.put("id", usuario.get().getId());
        body.put("nome", usuario.get().getNome());
        body.put("email", usuario.get().getEmail());
        body.put("tipo", usuario.get().getTipo().name());
        body.put("telefone", usuario.get().getTelefone() == null ? "" : usuario.get().getTelefone());
        return ResponseEntity.ok(body);
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Map<String, Object>> cadastro(@Valid @RequestBody CadastroRequest request) {
        try {
            Usuario usuario = new Usuario();
            usuario.setNome(request.nome());
            usuario.setEmail(request.email().trim().toLowerCase());
            usuario.setSenha(request.senha());
            usuario.setTelefone(request.telefone().trim());
            Usuario salvo = usuarioService.cadastrar(usuario);
            Map<String, Object> response = new HashMap<>();
            response.put("id", salvo.getId());
            response.put("nome", salvo.getNome());
            response.put("email", salvo.getEmail());
            response.put("tipo", salvo.getTipo().name());
            response.put("telefone", salvo.getTelefone());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<Map<String, String>> recuperarSenha(@Valid @RequestBody EmailRequest request) {
        recuperacaoSenhaService.solicitarRecuperacao(request.email().trim().toLowerCase());
        return ResponseEntity.ok(Map.of(
                "mensagem", "Se o e-mail estiver cadastrado, enviamos um link de recuperação"));
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<Map<String, String>> redefinirSenha(@Valid @RequestBody RedefinirSenhaRequest request) {
        boolean redefinida = recuperacaoSenhaService.redefinirSenha(request.token(), request.novaSenha());
        if (!redefinida) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Link de recuperação inválido ou expirado."));
        }
        return ResponseEntity.ok(Map.of("mensagem", "Senha alterada com sucesso"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        var session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }

    public record EmailRequest(@NotBlank @Email String email) {}

    public record CadastroRequest(
            @NotBlank @Size(max = 100) String nome,
            @NotBlank @Email @Size(max = 254) String email,
            @NotBlank @Size(min = 6, max = 100) String senha,
            @NotBlank @Size(max = 20) @Pattern(regexp = "[+0-9() .-]+") String telefone) {}

    public record RedefinirSenhaRequest(
            @NotBlank String token,
            @NotBlank @Size(min = 6) String novaSenha) {}
}
