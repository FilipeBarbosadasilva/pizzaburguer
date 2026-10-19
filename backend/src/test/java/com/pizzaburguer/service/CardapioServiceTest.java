package com.pizzaburguer.service;

import com.pizzaburguer.model.Pizza;
import com.pizzaburguer.repository.BebidaRepository;
import com.pizzaburguer.repository.PizzaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardapioServiceTest {
    @Mock private PizzaRepository pizzaRepository;
    @Mock private BebidaRepository bebidaRepository;

    @Test
    void updatingPizzaKeepsItsDatabaseIdentifier() {
        Pizza existing = new Pizza();
        existing.setId(42L);
        existing.setNome("Pizza original");
        existing.setDescricao("Descrição original");
        existing.setCategoria("tradicionais");
        existing.setPreco(30.0);
        existing.setImagem("original.png");
        when(pizzaRepository.findById(42L)).thenReturn(Optional.of(existing));
        when(pizzaRepository.save(existing)).thenReturn(existing);

        Pizza update = new Pizza();
        update.setId(999L);
        update.setNome("Pizza atualizada");
        update.setDescricao("Nova descrição");
        update.setCategoria("especiais");
        update.setPreco(40.0);
        update.setImagem("/uploads/new.png");
        update.setDisponivel(false);

        Pizza saved = new CardapioService(pizzaRepository, bebidaRepository).atualizarPizza(42L, update);

        assertEquals(42L, saved.getId());
        assertEquals("Pizza atualizada", saved.getNome());
        assertEquals("/uploads/new.png", saved.getImagem());
        verify(pizzaRepository).save(existing);
    }

    @Test
    void removingPizzaDeactivatesItInsteadOfDeletingTheRecord() {
        Pizza existing = new Pizza();
        existing.setId(42L);
        existing.setDisponivel(true);
        when(pizzaRepository.findById(42L)).thenReturn(Optional.of(existing));

        new CardapioService(pizzaRepository, bebidaRepository).removerPizza(42L);

        assertEquals(false, existing.isDisponivel());
        verify(pizzaRepository).save(existing);
    }
}
