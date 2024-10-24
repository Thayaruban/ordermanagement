package com.zerobeta.assignment.ordermanagement.dto;

import com.zerobeta.assignment.ordermanagement.util.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class SignUpRequestDTO {

    @NotBlank(message = "Email is Mandatory")
    @Email(message = "Invalid Email Format")
    private String email;

    @NotBlank(message = "Password is Mandatory")
    @ValidPassword
    private String password;

    @NotBlank(message = "First Name is Mandatory")
    @Pattern(regexp = "^[A-Za-z]+$", message = "First Name Must Only Contain Letters")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Last Name Must Only Contain Letters")
    private String lastName;

}
