package com.pizzaburguer.repository;

import com.pizzaburguer.model.Bebida;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BebidaRepository extends JpaRepository<Bebida, Long> {
    java.util.Optional<Bebida> findFirstByNome(String nome);
}
