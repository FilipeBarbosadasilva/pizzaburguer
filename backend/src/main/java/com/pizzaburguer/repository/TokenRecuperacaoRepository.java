package com.pizzaburguer.repository;

import com.pizzaburguer.model.TokenRecuperacao;
import com.pizzaburguer.model.Usuario;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenRecuperacaoRepository extends JpaRepository<TokenRecuperacao, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TokenRecuperacao t where t.token = :token")
    Optional<TokenRecuperacao> findByTokenForUpdate(@Param("token") String token);

    void deleteAllByUsuario(Usuario usuario);
}
