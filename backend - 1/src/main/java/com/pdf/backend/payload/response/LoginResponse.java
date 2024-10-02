package com.pdf.backend.payload.response;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {
    private String username;
    private String name;
    private String id;
    private String email;
    private String avatar;
    private String status;
    private String roles;

    public LoginResponse(String username, String name, String id, String email, String avatar, String status, String roles) {
        this.username = username;
        this.name = name;
        this.id = id;
        this.email = email;
        this.avatar = avatar;
        this.status = status;
        this.roles = roles;
    }
}
