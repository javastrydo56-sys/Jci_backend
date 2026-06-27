package com.example.jci.dto;

import java.math.BigDecimal;
import java.util.List;

public class SellerEarningsResponse {

    public Long sellerId;
    public String sellerName;

    public BigDecimal totalEarnings;
    public int totalOrders;

    public List<ServiceOrderResponse> orders;
}