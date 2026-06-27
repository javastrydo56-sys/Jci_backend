package com.example.jci.dto;

import java.math.BigDecimal;
import java.util.List;

public class BuyerSpendingResponse {

    public Long buyerId;

    public BigDecimal totalSpent;
    public int totalOrders;
    public int paidOrders;

    public List<ServiceOrderResponse> orders;
}