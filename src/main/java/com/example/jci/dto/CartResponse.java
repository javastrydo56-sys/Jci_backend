package com.example.jci.dto;

import java.util.List;

public class CartResponse {

    public Long cartId;
    public Long buyerId;
    public List<CartItemResponse> items;
    public Double grandTotal;
}