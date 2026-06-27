package com.example.jci.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_orders")
public class ServiceOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long buyerId;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private ServiceEntity service;

    private String status; // PENDING, IN_PROGRESS, COMPLETED
    private String paymentStatus; // PENDING, SUBMITTED, CONFIRMED, REJECTED
    private String upiTransactionId;

    private LocalDateTime createdAt;

    // GETTERS & SETTERS

    public Long getId() { return id; }

    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }

    public ServiceEntity getService() { return service; }
    public void setService(ServiceEntity service) { this.service = service; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getUpiTransactionId() { return upiTransactionId; }
    public void setUpiTransactionId(String upiTransactionId) { this.upiTransactionId = upiTransactionId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}