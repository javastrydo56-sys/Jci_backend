package com.example.jci.dto;

public class ServiceOrderResponse {

    public Long orderId;
    public Long buyerId;

    public Long serviceId; // 🔥 ADD THIS
    public String serviceName;

    public String sellerName;
    public String sellerEmail;

    public String status;
    public String paymentStatus;
    public String upiTransactionId;
    public Long sellerId;
}