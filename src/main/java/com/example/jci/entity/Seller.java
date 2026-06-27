package com.example.jci.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sellers")
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String email;
    private String password;

    private String companyName;
    private String companyPhone;
    private String ownerName;
    private String companyAddress;

    private String productOrService;
    private String location;

    private String companyImage;
    private String companyDescription;

    private String organizationName;

    private String upiQrImageUrl;

    @Lob
    @Column(name = "upi_qr_image_data")
    private byte[] upiQrImageData;

    // Getters & Setters
    public Long getId() { return id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCompanyPhone() { return companyPhone; }
    public void setCompanyPhone(String companyPhone) { this.companyPhone = companyPhone; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getCompanyAddress() { return companyAddress; }
    public void setCompanyAddress(String companyAddress) { this.companyAddress = companyAddress; }

    public String getProductOrService() { return productOrService; }
    public void setProductOrService(String productOrService) { this.productOrService = productOrService; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCompanyImage() { return companyImage; }
    public void setCompanyImage(String companyImage) { this.companyImage = companyImage; }

    public String getCompanyDescription() { return companyDescription; }
    public void setCompanyDescription(String companyDescription) { this.companyDescription = companyDescription; }

    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public String getUpiQrImageUrl() { return upiQrImageUrl; }
    public void setUpiQrImageUrl(String upiQrImageUrl) { this.upiQrImageUrl = upiQrImageUrl; }

    public byte[] getUpiQrImageData() { return upiQrImageData; }
    public void setUpiQrImageData(byte[] upiQrImageData) { this.upiQrImageData = upiQrImageData; }
}