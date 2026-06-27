package com.example.jci.dto;

import java.math.BigDecimal;

public class ServicePaymentResponse {

    public Long orderId;

    public Long buyerId;

    public Long serviceId;
    public String serviceName;

    public String sellerName;
    public String sellerEmail;

    public BigDecimal amount;

    public String status;
    public String paymentStatus;
}