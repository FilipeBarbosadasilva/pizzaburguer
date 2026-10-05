package com.pizzaburguer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pizzaburguer.model.Pedido;
import com.pizzaburguer.model.Usuario;
import com.pizzaburguer.repository.PedidoRepository;
import com.pizzaburguer.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoControllerTest {
    @Mock private PedidoRepository pedidoRepository;
    @Mock private UsuarioRepository usuarioRepository;

    private PedidoController controller;
    private Usuario customer;
    private Authentication customerAuth;

    @BeforeEach
    void setUp() {
        controller = new PedidoController(pedidoRepository, usuarioRepository, new ObjectMapper());
        customer = new Usuario();
        customer.setId(14L);
        customer.setNome("Cliente");
        customer.setEmail("cliente@example.com");
        customer.setTelefone("(11) 99999-1111");
        customer.setTipo(Usuario.TipoUsuario.CLIENTE);
        customerAuth = new UsernamePasswordAuthenticationToken(
                customer.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_CLIENTE")));
        when(usuarioRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
    }

    @Test
    void savesOrderAndReturnsItsItemsAndCurrentStatus() {
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedido = invocation.getArgument(0);
            pedido.setId(52L);
            return pedido;
        });
        List<Map<String, Object>> items = List.of(Map.of(
                "id", "calabresa", "name", "Calabresa", "price", 42.9, "quantity", 2));

        Map<String, Object> result = controller.criarPedido(
                new PedidoController.CriarPedidoRequest(
                        customer.getTelefone(), "Rua 1, 123", "Sem cebola",
                        "Pix", "Entrega", new BigDecimal("90.80"), items),
                customerAuth).getBody();

        assertNotNull(result);
        assertEquals(52L, result.get("id"));
        assertEquals(14L, result.get("idUsuario"));
        assertEquals("Pendente", result.get("status"));
        assertEquals(items, result.get("itens"));
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void listsOnlyOrdersForTheAuthenticatedCustomer() {
        Pedido pedido = makeOrder(52L, "Em preparo");
        when(pedidoRepository.findAllByUsuarioIdOrderByDataDesc(customer.getId())).thenReturn(List.of(pedido));

        List<Map<String, Object>> result = controller.meusPedidos(customerAuth).getBody();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Em preparo", result.get(0).get("status"));
        assertEquals("Pizzas", ((List<?>) result.get(0).get("itens")).get(0)
                instanceof Map<?, ?> item ? item.get("name") : null);
    }

    @Test
    void cancellationMarksPendingOrderAsCanceledInsteadOfDeletingIt() {
        Pedido pedido = makeOrder(52L, "Pendente");
        when(pedidoRepository.findByIdForUpdate(52L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        Map<String, Object> result = controller.cancelarPedido(52L, customerAuth).getBody();

        assertNotNull(result);
        assertEquals("Cancelado", result.get("status"));
        verify(pedidoRepository, never()).delete(any(Pedido.class));
    }

    @Test
    void refusesCancellationOncePreparationHasStarted() {
        Pedido pedido = makeOrder(52L, "Em preparo");
        when(pedidoRepository.findByIdForUpdate(52L)).thenReturn(Optional.of(pedido));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.cancelarPedido(52L, customerAuth));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    private Pedido makeOrder(Long id, String status) {
        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setUsuario(customer);
        pedido.setData(LocalDateTime.now());
        pedido.setStatus(status);
        pedido.setFormaPagamento("Pix");
        pedido.setFormaEntrega("Entrega");
        pedido.setTotal(new BigDecimal("45.00"));
        pedido.setTelefone(customer.getTelefone());
        pedido.setEndereco("Rua 1, 123");
        pedido.setItensJson("[{\"name\":\"Pizzas\",\"quantity\":1,\"price\":45.00}]");
        return pedido;
    }
}
