package com.example.jci.dto;

import java.math.BigDecimal;

public class ProductPaymentResponse {

    public Long orderId;
    public Long buyerId;

    public Long productId;
    public String productName;

    public String sellerName;
    public String sellerEmail;

    public int quantity;
    public BigDecimal totalAmount;

    public String orderStatus;
    public String paymentStatus;
}