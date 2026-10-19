package com.pizzaburguer.controller;

import com.pizzaburguer.model.Bebida;
import com.pizzaburguer.model.Pizza;
import com.pizzaburguer.service.CardapioService;
import com.pizzaburguer.service.UploadStorageService;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CardapioController {
    private final CardapioService cardapioService;
    private final UploadStorageService uploadStorageService;

    public CardapioController(CardapioService cardapioService, UploadStorageService uploadStorageService) {
        this.cardapioService = cardapioService;
        this.uploadStorageService = uploadStorageService;
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
    public ResponseEntity<Pizza> criarPizza(@Valid @RequestBody Pizza pizza) {
        pizza.setId(null);
        return ResponseEntity.ok(cardapioService.salvarPizza(pizza));
    }

    @PutMapping("/admin/pizzas/{id}")
    public ResponseEntity<Pizza> atualizarPizza(@PathVariable Long id, @Valid @RequestBody Pizza pizza) {
        return ResponseEntity.ok(cardapioService.atualizarPizza(id, pizza));
    }

    @DeleteMapping("/admin/pizzas/{id}")
    public ResponseEntity<Map<String, String>> removerPizza(@PathVariable Long id) {
        cardapioService.removerPizza(id);
        Map<String, String> response = new HashMap<>();
        response.put("status", "desativado");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/bebidas")
    public ResponseEntity<Bebida> criarBebida(@Valid @RequestBody Bebida bebida) {
        bebida.setId(null);
        return ResponseEntity.ok(cardapioService.salvarBebida(bebida));
    }

    @PutMapping("/admin/bebidas/{id}")
    public ResponseEntity<Bebida> atualizarBebida(@PathVariable Long id, @Valid @RequestBody Bebida bebida) {
        return ResponseEntity.ok(cardapioService.atualizarBebida(id, bebida));
    }

    @DeleteMapping("/admin/bebidas/{id}")
    public ResponseEntity<Map<String, String>> removerBebida(@PathVariable Long id) {
        cardapioService.removerBebida(id);
        return ResponseEntity.ok(Map.of("status", "desativado"));
    }

    @PostMapping("/admin/uploads")
    public ResponseEntity<Map<String, String>> enviarImagem(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(Map.of("url", uploadStorageService.storeImage(file)));
    }
}
