package com.example.jci.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ServiceOrderDetailResponse {

    public Long orderId;
    public Long buyerId;

    public Long serviceId;
    public String serviceName;
    public String description;
    public BigDecimal cost;
    public String imageUrl;

    public String sellerName;
    public String sellerEmail;

    public String status;
    public String paymentStatus;

    public LocalDateTime createdAt;
}