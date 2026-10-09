package com.pizzaburguer.service;

import com.pizzaburguer.model.TokenRecuperacao;
import com.pizzaburguer.model.Usuario;
import com.pizzaburguer.repository.TokenRecuperacaoRepository;
import com.pizzaburguer.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RecuperacaoSenhaService {
    private static final Logger logger = LoggerFactory.getLogger(RecuperacaoSenhaService.class);
    private static final int TOKEN_VALIDITY_MINUTES = 30;

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacaoRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final BCryptPasswordEncoder passwordEncoder;
    private final String frontendBaseUrl;
    private final String mailFrom;

    public RecuperacaoSenhaService(
            UsuarioRepository usuarioRepository,
            TokenRecuperacaoRepository tokenRepository,
            JavaMailSender mailSender,
            BCryptPasswordEncoder passwordEncoder,
            @Value("${app.frontend.base-url}") String frontendBaseUrl,
            @Value("${app.mail.from}") String mailFrom) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
        this.frontendBaseUrl = frontendBaseUrl.replaceAll("/+$", "");
        this.mailFrom = mailFrom;
    }

    public void solicitarRecuperacao(String email) {
        usuarioRepository.findByEmail(email).ifPresent(usuario -> enviarLink(usuario, email));
    }

    @Transactional
    public boolean redefinirSenha(String tokenValue, String novaSenha) {
        TokenRecuperacao token = tokenRepository.findByTokenForUpdate(tokenValue).orElse(null);
        if (token == null || token.isUsado() || !LocalDateTime.now().isBefore(token.getExpiraEm())) {
            return false;
        }

        Usuario usuario = token.getUsuario();
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
        token.setUsado(true);
        tokenRepository.save(token);
        return true;
    }

    private void enviarLink(Usuario usuario, String email) {
        tokenRepository.deleteAllByUsuario(usuario);

        TokenRecuperacao token = new TokenRecuperacao();
        token.setUsuario(usuario);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiraEm(LocalDateTime.now().plusMinutes(TOKEN_VALIDITY_MINUTES));
        token.setUsado(false);
        tokenRepository.save(token);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(email);
        message.setSubject("Recuperação de senha - Pizzaburguer");
        message.setText("Use este link para criar uma nova senha (válido por 30 minutos):\n"
                + frontendBaseUrl + "/login.html?token=" + token.getToken());

        try {
            mailSender.send(message);
        } catch (MailException exception) {
            tokenRepository.delete(token);
            logger.error("Não foi possível enviar o e-mail de recuperação para a conta solicitada.", exception);
            throw exception;
        }
    }
}
