package com.zerobeta.assignment.ordermanagement.job;

import com.zerobeta.assignment.ordermanagement.entity.Order;
import com.zerobeta.assignment.ordermanagement.enums.OrderStatus;
import com.zerobeta.assignment.ordermanagement.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service that handles the automatic dispatching of orders.
 * This class contains a scheduled job that runs at fixed intervals to
 * change the status of new orders to dispatched.
 */
@Service
public class DispatchOrder {

    private static final Logger logger = LoggerFactory.getLogger(DispatchOrder.class);

    private final OrderRepository orderRepository;

    @Autowired
    public DispatchOrder(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Scheduled job that runs every hour to dispatch new orders.
     * This method fetches all orders with a status of NEW and
     * updates their status to DISPATCHED in bulk.
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void dispatchOrders() {
        orderRepository.updateOrderStatus(OrderStatus.NEW, OrderStatus.DISPATCHED);

        logger.info("Order dispatch job completed.");
    }
}
