package com.pizzaburguer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pizzaburguer.model.Pedido;
import com.pizzaburguer.model.Usuario;
import com.pizzaburguer.repository.PedidoRepository;
import com.pizzaburguer.repository.UsuarioRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class PedidoController {
    private static final Set<String> VALID_STATUSES =
            Set.of("Pendente", "Em preparo", "Saiu para entrega", "Entregue", "Cancelado");
    private static final TypeReference<List<Map<String, Object>>> ITEMS_TYPE = new TypeReference<>() {};

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;

    public PedidoController(
            PedidoRepository pedidoRepository,
            UsuarioRepository usuarioRepository,
            ObjectMapper objectMapper) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/pedidos")
    public ResponseEntity<Map<String, Object>> criarPedido(
            @Valid @RequestBody CriarPedidoRequest request,
            Authentication authentication) {
        Usuario usuario = buscarUsuarioAutenticado(authentication);

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setData(LocalDateTime.now());
        pedido.setStatus("Pendente");
        pedido.setFormaPagamento(request.formaPagamento());
        pedido.setFormaEntrega(request.formaEntrega());
        pedido.setTelefone(request.telefone());
        pedido.setEndereco(request.endereco());
        pedido.setObservacoes(request.observacoes());
        pedido.setTotal(request.total());
        pedido.setItensJson(serializeItems(request.itens()));

        return ResponseEntity.ok(toResponse(pedidoRepository.save(pedido)));
    }

    @GetMapping("/pedidos/meus")
    public ResponseEntity<List<Map<String, Object>>> meusPedidos(Authentication authentication) {
        Usuario usuario = buscarUsuarioAutenticado(authentication);
        List<Map<String, Object>> pedidos = pedidoRepository
                .findAllByUsuarioIdOrderByDataDesc(usuario.getId())
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(pedidos);
    }

    @DeleteMapping("/pedidos/{id}")
    @Transactional
    public ResponseEntity<Map<String, Object>> cancelarPedido(
            @PathVariable Long id,
            Authentication authentication) {
        Usuario usuario = buscarUsuarioAutenticado(authentication);
        Pedido pedido = pedidoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!pedido.getUsuario().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (!pedido.getStatus().equals("Pendente")) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "O pedido só pode ser cancelado enquanto estiver Pendente.");
        }

        pedido.setStatus("Cancelado");
        return ResponseEntity.ok(toResponse(pedidoRepository.save(pedido)));
    }

    @GetMapping("/pedidos/{id}")
    public ResponseEntity<Map<String, Object>> consultarPedido(
            @PathVariable Long id,
            Authentication authentication) {
        Usuario usuario = buscarUsuarioAutenticado(authentication);
        boolean admin = isAdmin(authentication);
        return pedidoRepository.findById(id)
                .filter(pedido -> admin || pedido.getUsuario().getId().equals(usuario.getId()))
                .map(pedido -> ResponseEntity.ok(toResponse(pedido)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/pedidos/usuario/{idUsuario}")
    public ResponseEntity<List<Map<String, Object>>> historicoUsuario(
            @PathVariable Long idUsuario,
            Authentication authentication) {
        Usuario usuario = buscarUsuarioAutenticado(authentication);
        if (!isAdmin(authentication) && !usuario.getId().equals(idUsuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        List<Map<String, Object>> pedidos = pedidoRepository.findAllByUsuarioIdOrderByDataDesc(idUsuario)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/admin/pedidos")
    public ResponseEntity<List<Map<String, Object>>> listarPedidos() {
        return ResponseEntity.ok(pedidoRepository.findAllByOrderByDataDesc()
                .stream()
                .map(this::toResponse)
                .toList());
    }

    @DeleteMapping("/admin/pedidos")
    @Transactional
    public ResponseEntity<Map<String, Long>> limparPedidos() {
        long quantidadeRemovida = pedidoRepository.count();
        pedidoRepository.deleteAll();
        pedidoRepository.flush();
        long quantidadeRestante = pedidoRepository.count();
        if (quantidadeRestante != 0) {
            throw new IllegalStateException("A limpeza não removeu todos os pedidos.");
        }
        return ResponseEntity.ok(Map.of(
                "quantidadeRemovida", quantidadeRemovida,
                "quantidadeRestante", quantidadeRestante));
    }

    @PutMapping("/admin/pedidos/{id}/status")
    public ResponseEntity<Map<String, Object>> atualizarStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        String status = payload.get("status");
        if (!VALID_STATUSES.contains(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status do pedido inválido.");
        }
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        pedido.setStatus(status);
        return ResponseEntity.ok(toResponse(pedidoRepository.save(pedido)));
    }

    @GetMapping("/admin/relatorio")
    public ResponseEntity<Map<String, Object>> relatorio() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        List<Pedido> activeOrders = pedidos.stream()
                .filter(pedido -> !pedido.getStatus().equals("Cancelado"))
                .toList();

        double faturamento = activeOrders.stream()
                .mapToDouble(pedido -> pedido.getTotal().doubleValue())
                .sum();
        long pedidosHoje = activeOrders.stream()
                .filter(pedido -> pedido.getData().toLocalDate().equals(now.toLocalDate()))
                .count();
        double faturamentoHoje = activeOrders.stream()
                .filter(pedido -> pedido.getData().toLocalDate().equals(now.toLocalDate()))
                .mapToDouble(pedido -> pedido.getTotal().doubleValue())
                .sum();
        double faturamentoMes = activeOrders.stream()
                .filter(pedido -> pedido.getData().getYear() == now.getYear()
                        && pedido.getData().getMonth() == now.getMonth())
                .mapToDouble(pedido -> pedido.getTotal().doubleValue())
                .sum();

        Map<String, Object> relatorio = new HashMap<>();
        relatorio.put("quantidadePedidos", pedidosHoje);
        relatorio.put("faturamentoTotal", faturamento);
        relatorio.put("faturamentoHoje", faturamentoHoje);
        relatorio.put("faturamentoMes", faturamentoMes);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/admin/relatorio/mais-pedidos")
    public ResponseEntity<List<Map<String, Object>>> relatorioMaisPedidos() {
        Map<String, Long> quantidadesPorItem = new HashMap<>();
        for (Pedido pedido : pedidoRepository.findAllByOrderByDataDesc()) {
            if (pedido.getStatus().equals("Cancelado")) {
                continue;
            }
            for (Map<String, Object> item : parseItems(pedido.getItensJson())) {
                Object nome = item.get("name");
                Object quantidade = item.get("quantity");
                if (!(nome instanceof String itemName) || itemName.isBlank()
                        || !(quantidade instanceof Number itemQuantity)) {
                    throw new IllegalStateException("Os itens gravados do pedido têm dados inválidos.");
                }
                quantidadesPorItem.put(itemName,
                        quantidadesPorItem.getOrDefault(itemName, 0L) + itemQuantity.longValue());
            }
        }

        List<Map<String, Object>> relatorio = quantidadesPorItem.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER)))
                .map(entry -> Map.<String, Object>of(
                        "nome", entry.getKey(),
                        "quantidade", entry.getValue()))
                .toList();
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/admin/clientes")
    public ResponseEntity<List<Map<String, Object>>> listarClientes() {
        List<Map<String, Object>> clientes = usuarioRepository.findAll().stream()
                .filter(usuario -> usuario.getTipo() == Usuario.TipoUsuario.CLIENTE)
                .map(usuario -> Map.<String, Object>of(
                        "id", usuario.getId(),
                        "nome", usuario.getNome(),
                        "email", usuario.getEmail(),
                        "telefone", usuario.getTelefone() == null ? "" : usuario.getTelefone()))
                .toList();
        return ResponseEntity.ok(clientes);
    }

    private Usuario buscarUsuarioAutenticado(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    private String serializeItems(List<Map<String, Object>> items) {
        try {
            return objectMapper.writeValueAsString(items);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Os itens do pedido não puderam ser processados.", exception);
        }
    }

    private Map<String, Object> toResponse(Pedido pedido) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", pedido.getId());
        response.put("idUsuario", pedido.getUsuario().getId());
        response.put("nome", pedido.getUsuario().getNome());
        response.put("email", pedido.getUsuario().getEmail());
        response.put("telefone", pedido.getTelefone());
        response.put("endereco", pedido.getEndereco());
        response.put("observacoes", pedido.getObservacoes());
        response.put("formaPagamento", pedido.getFormaPagamento());
        response.put("formaEntrega", pedido.getFormaEntrega());
        response.put("total", pedido.getTotal());
        response.put("status", pedido.getStatus());
        response.put("data", pedido.getData());
        response.put("itens", parseItems(pedido.getItensJson()));
        return response;
    }

    private List<Map<String, Object>> parseItems(String itensJson) {
        try {
            return objectMapper.readValue(itensJson, ITEMS_TYPE);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Os itens gravados do pedido não puderam ser lidos.", exception);
        }
    }

    public record CriarPedidoRequest(
            @NotBlank @Size(max = 20) String telefone,
            @Size(max = 500) String endereco,
            @Size(max = 1000) String observacoes,
            @NotBlank @Size(max = 60) String formaPagamento,
            @NotBlank @Size(max = 30) String formaEntrega,
            @NotNull @DecimalMin("0.01") BigDecimal total,
            @NotEmpty List<Map<String, Object>> itens) {}
}
