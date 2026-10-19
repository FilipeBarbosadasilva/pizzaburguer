package com.pizzaburguer.repository;

import com.pizzaburguer.model.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PizzaRepository extends JpaRepository<Pizza, Long> {
    java.util.Optional<Pizza> findFirstByNome(String nome);
}
