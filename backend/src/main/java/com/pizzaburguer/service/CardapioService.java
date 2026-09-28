package com.pizzaburguer.service;

import com.pizzaburguer.model.Bebida;
import com.pizzaburguer.model.Pizza;
import com.pizzaburguer.repository.BebidaRepository;
import com.pizzaburguer.repository.PizzaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardapioService {
    private final PizzaRepository pizzaRepository;
    private final BebidaRepository bebidaRepository;

    public CardapioService(PizzaRepository pizzaRepository, BebidaRepository bebidaRepository) {
        this.pizzaRepository = pizzaRepository;
        this.bebidaRepository = bebidaRepository;
    }

    public List<Pizza> listarPizzas() {
        return pizzaRepository.findAll();
    }

    public List<Bebida> listarBebidas() {
        return bebidaRepository.findAll();
    }

    public Pizza salvarPizza(Pizza pizza) {
        return pizzaRepository.save(pizza);
    }

    public Bebida salvarBebida(Bebida bebida) {
        return bebidaRepository.save(bebida);
    }

    public void removerPizza(Long id) {
        pizzaRepository.deleteById(id);
    }

    public void removerBebida(Long id) {
        bebidaRepository.deleteById(id);
    }
}
