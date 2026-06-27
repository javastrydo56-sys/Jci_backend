package com.example.jci.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProductPaymentDetailResponse {

    public Long orderId;
    public Long buyerId;

    public LocalDateTime createdAt;

    public String orderStatus;
    public String paymentStatus;

    public BigDecimal totalAmount;

    public List<Item> items;

    public static class Item {
        public Long productId;
        public String productName;
        public String sellerName;
        public String sellerEmail;
        public int quantity;
        public BigDecimal price;
        public BigDecimal total;
    }
}