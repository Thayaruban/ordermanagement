package com.zerobeta.assignment.ordermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class APIResponseDTO<T> {
    private boolean success;
    private String message;
    private T data;

    public APIResponseDTO() {}

    public APIResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

}
