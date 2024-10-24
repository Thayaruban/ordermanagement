package com.zerobeta.assignment.ordermanagement.service;

import com.zerobeta.assignment.ordermanagement.dto.OrderRequestDTO;
import com.zerobeta.assignment.ordermanagement.entity.Order;
import com.zerobeta.assignment.ordermanagement.entity.User;
import com.zerobeta.assignment.ordermanagement.enums.OrderStatus;
import com.zerobeta.assignment.ordermanagement.exception.EntityNotFoundException;
import com.zerobeta.assignment.ordermanagement.repository.OrderRepository;
import com.zerobeta.assignment.ordermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    private User user;
    private OrderRequestDTO orderRequestDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setEmail("user@example.com");
        orderRequestDTO = new OrderRequestDTO("Test Item",2,"Point Pedro");

    }

    @Test
    void placeOrder_Success() {

        String emailId = "user@example.com";
        when(userRepository.findByEmail(emailId)).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Return the order itself

        Order order = orderService.placeOrder(emailId, orderRequestDTO);

        assertNotNull(order);
        assertEquals(OrderStatus.NEW, order.getStatus());
        assertEquals(user, order.getUser());
        assertEquals(orderRequestDTO.getItemName(), order.getItemName());
        assertEquals(orderRequestDTO.getQuantity(), order.getQuantity());
        assertEquals(orderRequestDTO.getShippingAddress(), order.getShippingAddress());
        assertNotNull(order.getOrderReference());
        assertTrue(order.getOrderReference().startsWith("ORD"));
    }

    @Test
    void placeOrder_UserNotFound() {

        String emailId = "nonexistent@example.com";
        when(userRepository.findByEmail(emailId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                orderService.placeOrder(emailId, orderRequestDTO)
        );
        assertEquals("User not found: " + emailId, exception.getMessage());
    }

    @Test
    void cancelOrder_Success() {
        // Arrange
        String emailId = "user@example.com";
        String orderReference = "ORD1234567890";
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.NEW);
        order.setOrderReference(orderReference);

        when(orderRepository.findByOrderReference(orderReference)).thenReturn(Optional.of(order));

        orderService.cancelOrder(emailId, orderReference);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void cancelOrder_OrderNotFound() {
        // Arrange
        String emailId = "user@example.com";
        String orderReference = "ORD1234567890";

        when(orderRepository.findByOrderReference(orderReference)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                orderService.cancelOrder(emailId, orderReference)
        );
        assertEquals("Order not found with reference: " + orderReference, exception.getMessage());
    }

    @Test
    void cancelOrder_OrderDoesNotBelongToUser() {
        String emailId = "user@example.com";
        String orderReference = "ORD1234567890";

        User orderUser = new User();
        orderUser.setEmail("orderUser@example.com");

        Order order = new Order();
        order.setUser(orderUser);
        order.setStatus(OrderStatus.NEW);
        order.setOrderReference(orderReference);

        when(orderRepository.findByOrderReference(orderReference)).thenReturn(Optional.of(order));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                orderService.cancelOrder(emailId, orderReference)
        );

        assertEquals("Cannot cancel order. Order status is not NEW or does not belong to the user: " + emailId, exception.getMessage());
    }


    @Test
    void getOrderHistory_Success() {

        String emailId = "user@example.com";
        Pageable pageable = PageRequest.of(0, 10);
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.NEW);
        order.setItemName("Test Item");
        order.setQuantity(1);
        order.setShippingAddress("Point Pedro");

        Page<Order> orderPage = new PageImpl<>(List.of(order), pageable, 1);
        when(orderRepository.findByUserEmail(emailId, pageable)).thenReturn(orderPage);

        Page<Order> result = orderService.getOrderHistory(emailId, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(orderRepository, times(1)).findByUserEmail(emailId, pageable);
    }

    @Test
    void getOrderHistory_NoOrdersFound() {
        String emailId = "user@example.com";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(List.of(), pageable, 0);
        when(orderRepository.findByUserEmail(emailId, pageable)).thenReturn(orderPage);

        Page<Order> result = orderService.getOrderHistory(emailId, 0, 10);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(orderRepository, times(1)).findByUserEmail(emailId, pageable);
    }

    @Test
    void getOrderHistory_InvalidPaginationOrPageSize() {
        String emailId = "user@example.com";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                orderService.getOrderHistory(emailId, -1, 10));
        assertEquals("Invalid pagination parameters: pageNo must be >= 0 and pageSize must be > 0", exception.getMessage());


        exception = assertThrows(IllegalArgumentException.class, () ->
                orderService.getOrderHistory(emailId, 0, 0));
        assertEquals("Invalid pagination parameters: pageNo must be >= 0 and pageSize must be > 0", exception.getMessage());
    }

}
