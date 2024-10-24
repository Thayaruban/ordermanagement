package com.zerobeta.assignment.ordermanagement.controller;

import com.zerobeta.assignment.ordermanagement.dto.OrderRequestDTO;
import com.zerobeta.assignment.ordermanagement.dto.APIResponseDTO;
import com.zerobeta.assignment.ordermanagement.entity.Order;
import com.zerobeta.assignment.ordermanagement.service.OrderService;
import com.zerobeta.assignment.ordermanagement.service.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing orders in the order management system.
 * This class handles requests related to order placement, cancellation,
 * and fetching order history.
 */
@RestController
@RequestMapping("/order-management/orders")
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    private final JwtService jwtUtil;

    public OrderController(OrderService orderService, JwtService jwtUtil) {
        this.orderService = orderService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Places a new order for the authenticated user.
     *
     * @param token        The JWT token for authentication.
     * @param orderRequest The details of the order to be placed.
     * @return ResponseEntity containing the order details.
     */
    @PostMapping("/place")
    public ResponseEntity<APIResponseDTO<Order>> placeOrder(@RequestHeader("Authorization") String token,
                                                            @Valid @RequestBody OrderRequestDTO orderRequest) {
        String email = jwtUtil.extractEmail(token.substring(7));
        Order order = orderService.placeOrder(email, orderRequest);
        APIResponseDTO<Order> response = new APIResponseDTO<>(true, "Order Placed Successfully", order);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancels an existing order for the authenticated user.
     *
     * @param token        The JWT token for authentication.
     * @param orderReference The reference of the order to be canceled.
     * @return ResponseEntity indicating success or failure of the cancellation.
     */
    @PutMapping("/cancel/{orderReference}")
    public ResponseEntity<APIResponseDTO<String>> cancelOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable String orderReference) {

        String emailId = jwtUtil.extractEmail(token.substring(7));
        orderService.cancelOrder(emailId, orderReference);
        return ResponseEntity.ok(new APIResponseDTO<>(true, "Order Cancelled Successfully"));
    }

    /**
     * Fetches the order history for the authenticated user with pagination.
     *
     * @param token   The JWT token for authentication.
     * @param pageNo  The page number for pagination.
     * @param pageSize The number of orders per page.
     * @return ResponseEntity containing the paginated order history.
     */
    @GetMapping("/history")
    public ResponseEntity<APIResponseDTO<Page<Order>>> getOrderHistory(
            @RequestHeader("Authorization") String token,
            @RequestParam int pageNo,
            @RequestParam int pageSize) {

        String jwtToken = token.substring(7);
        String emailId = jwtUtil.extractEmail(jwtToken);

        LOGGER.info("Fetching order history for Email: {} with pageNo: {} and pageSize: {}", emailId, pageNo, pageSize);
        Page<Order> orderHistory = orderService.getOrderHistory(emailId, pageNo, pageSize);

        if (orderHistory.isEmpty()) {
            LOGGER.warn("No orders found for Email: {}", emailId);
            return ResponseEntity.ok(new APIResponseDTO<>(true, "No orders found", orderHistory));
        } else {
            LOGGER.info("Fetched {} orders for Email: {}", orderHistory.getTotalElements(), emailId);
            return ResponseEntity.ok(new APIResponseDTO<>(true, "Fetched Orders Successfully", orderHistory));
        }
    }
}
