package com.pizzaburguer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PedidoController {

    private final List<Map<String, Object>> pedidos = new ArrayList<>();

    @PostMapping("/pedidos")
    public ResponseEntity<Map<String, Object>> criarPedido(@RequestBody Map<String, Object> payload) {
        Map<String, Object> pedido = new HashMap<>();
        pedido.put("id", System.currentTimeMillis());
        pedido.put("nome", payload.getOrDefault("nome", "Cliente"));
        pedido.put("telefone", payload.getOrDefault("telefone", ""));
        pedido.put("formaPagamento", payload.getOrDefault("formaPagamento", "PIX"));
        pedido.put("formaEntrega", payload.getOrDefault("formaEntrega", "ENTREGA"));
        pedido.put("total", payload.getOrDefault("total", 0));
        pedido.put("status", "Pendente");
        pedido.put("data", LocalDateTime.now().toString());
        pedido.put("itens", payload.getOrDefault("itens", List.of()));
        pedidos.add(pedido);
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/pedidos/{id}")
    public ResponseEntity<Map<String, Object>> consultarPedido(@PathVariable Long id) {
        return pedidos.stream()
                .filter(p -> ((Number) p.get("id")).longValue() == id)
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pedidos/usuario/{idUsuario}")
    public ResponseEntity<List<Map<String, Object>>> historicoUsuario(@PathVariable Long idUsuario) {
        List<Map<String, Object>> historico = pedidos.stream()
                .filter(p -> p.get("id") != null)
                .toList();
        return ResponseEntity.ok(historico);
    }

    @GetMapping("/admin/pedidos")
    public ResponseEntity<List<Map<String, Object>>> listarPedidos() {
        return ResponseEntity.ok(pedidos);
    }

    @PutMapping("/admin/pedidos/{id}/status")
    public ResponseEntity<Map<String, Object>> atualizarStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        for (Map<String, Object> pedido : pedidos) {
            if (((Number) pedido.get("id")).longValue() == id) {
                pedido.put("status", payload.getOrDefault("status", "Pendente"));
                return ResponseEntity.ok(pedido);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/admin/relatorio")
    public ResponseEntity<Map<String, Object>> relatorio() {
        double faturamento = pedidos.stream()
                .mapToDouble(p -> Number.class.cast(p.get("total")).doubleValue())
                .sum();

        Map<String, Object> relatorio = new HashMap<>();
        relatorio.put("quantidadePedidos", pedidos.size());
        relatorio.put("faturamentoTotal", faturamento);
        return ResponseEntity.ok(relatorio);
    }
}
