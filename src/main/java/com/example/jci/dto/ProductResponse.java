package com.example.jci.dto;

import java.time.LocalDate;

public class ProductResponse {

    public Long id;
    public String productName;
    public String productImage;
    public Double price;
    public String description;
    public LocalDate expiryDate;

    public String sellerName;
    public String sellerCompany;
    public String sellerPhone;      // ✅ add this
    public Long sellerId;

    public Integer totalQuantity;
    public Integer availableQuantity;
    public Integer soldQuantity;
}