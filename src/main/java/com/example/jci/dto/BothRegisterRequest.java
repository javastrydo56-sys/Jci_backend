package com.example.jci.dto;

public class BothRegisterRequest {
    
    // Common fields
    public String userId;
    public String email;
    public String password;
    
    // Buyer fields
    public String username;
    public String organizationName;
    public String phoneNumber;
    public String address;
    public String city;
    public String state;
    public String country;
    
    // Seller fields
    public String companyName;
    public String companyPhone;
    public String ownerName;
    public String companyAddress;
    public String productOrService;
    public String location;
    public String companyImage;
    public String companyDescription;
}
