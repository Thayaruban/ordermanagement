package com.zerobeta.assignment.ordermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class OrderRequestDTO {

    @NotBlank(message = "Item name is mandatory")
    private String itemName;

    @NotNull(message = "Quantity is mandatory")
    private Integer quantity;

    @NotBlank(message = "Shipping address is mandatory")
    private String shippingAddress;

    public OrderRequestDTO() {

    }
}
