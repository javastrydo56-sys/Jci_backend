package com.example.jci.dto;

import java.time.LocalDate;

public class CreateProductRequest {

    public String productName;
    public String productImage;
    public Double price;
    public String description;
    public LocalDate expiryDate;
    public Long sellerId;
    public Integer totalQuantity;
}