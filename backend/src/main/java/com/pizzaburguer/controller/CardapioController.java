package com.pizzaburguer.controller;

import com.pizzaburguer.model.Bebida;
import com.pizzaburguer.model.Pizza;
import com.pizzaburguer.service.CardapioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CardapioController {
    private final CardapioService cardapioService;

    public CardapioController(CardapioService cardapioService) {
        this.cardapioService = cardapioService;
    }

    @GetMapping("/pizzas")
    public List<Pizza> listarPizzas() {
        return cardapioService.listarPizzas();
    }

    @GetMapping("/bebidas")
    public List<Bebida> listarBebidas() {
        return cardapioService.listarBebidas();
    }

    @PostMapping("/admin/pizzas")
    public ResponseEntity<Pizza> criarPizza(@RequestBody Pizza pizza) {
        return ResponseEntity.ok(cardapioService.salvarPizza(pizza));
    }

    @PutMapping("/admin/pizzas/{id}")
    public ResponseEntity<Pizza> atualizarPizza(@PathVariable Long id, @RequestBody Pizza pizza) {
        pizza.setId(id);
        return ResponseEntity.ok(cardapioService.salvarPizza(pizza));
    }

    @DeleteMapping("/admin/pizzas/{id}")
    public ResponseEntity<Map<String, String>> removerPizza(@PathVariable Long id) {
        cardapioService.removerPizza(id);
        Map<String, String> response = new HashMap<>();
        response.put("status", "removido");
        return ResponseEntity.ok(response);
    }
}
