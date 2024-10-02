package com.pdf.backend.payload.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
public class UpdateRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String username;

    private String password;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String phone;

    @NotBlank
    private String hiringDate;

    @NotBlank
    private String address;

    private Set<String> role;

    // Getters and Setters
}
