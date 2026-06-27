package com.example.jci.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    public Long orderId;
    public Long buyerId;
    public LocalDateTime createdAt;
    public String status;
    public String paymentStatus;
    public String upiTransactionId;
    public Double totalAmount;

    public List<OrderItemResponse> items;
}