package com.pizzaburguer.service;

import com.pizzaburguer.model.Bebida;
import com.pizzaburguer.model.Pizza;
import com.pizzaburguer.repository.BebidaRepository;
import com.pizzaburguer.repository.PizzaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public Pizza atualizarPizza(Long id, Pizza atualizacao) {
        Pizza pizza = pizzaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        pizza.setNome(atualizacao.getNome());
        pizza.setDescricao(atualizacao.getDescricao());
        pizza.setCategoria(atualizacao.getCategoria());
        pizza.setPreco(atualizacao.getPreco());
        pizza.setImagem(atualizacao.getImagem());
        pizza.setDisponivel(atualizacao.isDisponivel());
        return pizzaRepository.save(pizza);
    }

    public Bebida salvarBebida(Bebida bebida) {
        return bebidaRepository.save(bebida);
    }

    public Bebida atualizarBebida(Long id, Bebida atualizacao) {
        Bebida bebida = bebidaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        bebida.setNome(atualizacao.getNome());
        bebida.setDescricao(atualizacao.getDescricao());
        bebida.setPreco(atualizacao.getPreco());
        bebida.setImagem(atualizacao.getImagem());
        bebida.setDisponivel(atualizacao.isDisponivel());
        return bebidaRepository.save(bebida);
    }

    public void removerPizza(Long id) {
        Pizza pizza = pizzaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        pizza.setDisponivel(false);
        pizzaRepository.save(pizza);
    }

    public void removerBebida(Long id) {
        Bebida bebida = bebidaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        bebida.setDisponivel(false);
        bebidaRepository.save(bebida);
    }
}
