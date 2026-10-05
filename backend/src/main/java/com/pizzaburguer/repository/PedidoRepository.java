package com.pizzaburguer.repository;

import com.pizzaburguer.model.Pedido;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    @Query("select p from Pedido p join fetch p.usuario where p.usuario.id = :usuarioId order by p.data desc")
    List<Pedido> findAllByUsuarioIdOrderByDataDesc(Long usuarioId);

    @Query("select p from Pedido p join fetch p.usuario order by p.data desc")
    List<Pedido> findAllByOrderByDataDesc();

    @org.springframework.data.jpa.repository.Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Pedido p join fetch p.usuario where p.id = :id")
    java.util.Optional<Pedido> findByIdForUpdate(@Param("id") Long id);
}
