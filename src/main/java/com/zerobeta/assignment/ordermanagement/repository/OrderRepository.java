package com.zerobeta.assignment.ordermanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.zerobeta.assignment.ordermanagement.entity.Order;
import com.zerobeta.assignment.ordermanagement.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatus(OrderStatus status);

    @Modifying
    @Query("UPDATE Order o SET o.status = :status WHERE o.status = :currentStatus")
    void updateOrderStatus(@Param("currentStatus") OrderStatus currentStatus, @Param("status") OrderStatus status);


    @Query("SELECT o FROM Order o WHERE o.user.email = :email")
    Page<Order> findByUserEmail(String email, Pageable pageable);

    Optional<Order> findByOrderReference(String orderReference);
}
