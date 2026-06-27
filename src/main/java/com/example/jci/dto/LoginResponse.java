package com.example.jci.dto;

public class LoginResponse {

    public String message;
    public String type; // BUYER or SELLER
    public Object data;

    public LoginResponse(String message, String type, Object data) {
        this.message = message;
        this.type = type;
        this.data = data;
    }
}