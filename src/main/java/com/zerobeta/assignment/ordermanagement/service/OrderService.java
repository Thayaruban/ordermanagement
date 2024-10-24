package com.zerobeta.assignment.ordermanagement.service;

import com.zerobeta.assignment.ordermanagement.dto.OrderRequestDTO;
import com.zerobeta.assignment.ordermanagement.entity.User;
import com.zerobeta.assignment.ordermanagement.entity.Order;
import com.zerobeta.assignment.ordermanagement.exception.EntityNotFoundException;
import com.zerobeta.assignment.ordermanagement.repository.OrderRepository;
import com.zerobeta.assignment.ordermanagement.repository.UserRepository;
import com.zerobeta.assignment.ordermanagement.enums.OrderStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;

/**
 * Service class that manages order-related operations.
 * This class provides methods to place, cancel, and retrieve orders for users.
 */
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Places a new order for a user.
     *
     * @param emailId      The email of the user placing the order.
     * @param orderRequest The order request data transfer object containing order details.
     * @return The created Order object with generated fields.
     * @throws EntityNotFoundException if the user with the given email ID is not found.
     */
    public Order placeOrder(String emailId, OrderRequestDTO orderRequest) {
        User user = userRepository.findByEmail(emailId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + emailId));

        Order order = new Order();
        // Copy properties from OrderRequest to Order
        BeanUtils.copyProperties(orderRequest, order, "user", "placementTimestamp", "status", "orderReference");

        order.setUser(user);
        order.setPlacementTimestamp(LocalDateTime.now());
        order.setStatus(OrderStatus.NEW);

        // Generate a custom order reference: ORD + timestamp + random 4 digits
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        int randomNumber = new Random().nextInt(9999);
        String orderReference = "ORD" + timestamp + String.format("%04d", randomNumber);
        order.setOrderReference(orderReference);

        orderRepository.save(order);
        return order;
    }

    /**
     * Cancels an existing order for a user.
     *
     * @param emailId      The email of the user who wants to cancel the order.
     * @param orderReference The reference of the order to be canceled.
     * @throws EntityNotFoundException if the order does not exist or does not belong to the user.
     */
    public void cancelOrder(String emailId, String orderReference) {
        Optional<Order> orderOptional = orderRepository.findByOrderReference(orderReference);

        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            // Check if the order belongs to the user and is in the NEW status
            if (order.getUser().getEmail().equals(emailId) && order.getStatus() == OrderStatus.NEW) {
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
            } else {
                throw new EntityNotFoundException("Cannot cancel order. Order status is not NEW or does not belong to the user: " + emailId);
            }
        } else {
            throw new EntityNotFoundException("Order not found with reference: " + orderReference);
        }
    }

    /**
     * Retrieves the order history for a user with pagination.
     *
     * @param emailId The email of the user whose order history is to be retrieved.
     * @param pageNo  The page number to retrieve.
     * @param pageSize The number of orders per page.
     * @return A Page object containing the user's orders.
     */
    public Page<Order> getOrderHistory(String emailId, int pageNo, int pageSize) {
        if (pageNo < 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters: pageNo must be >= 0 and pageSize must be > 0");
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return orderRepository.findByUserEmail(emailId, pageable);
    }

}
