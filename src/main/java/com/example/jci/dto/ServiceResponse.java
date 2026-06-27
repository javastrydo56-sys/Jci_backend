package com.example.jci.dto;

import java.math.BigDecimal;

public class ServiceResponse {

    private Long id;
    private String serviceName;
    private String imageUrl;
    private String description;
    public String getSellerName() {
		return sellerName;
	}
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}
	public String getSellerEmail() {
		return sellerEmail;
	}
	public void setSellerEmail(String sellerEmail) {
		this.sellerEmail = sellerEmail;
	}
	private String sellerName;
    private String sellerEmail;
    private String category;
    private String usefulness;
    private BigDecimal cost;
    private Integer deliveryDurationInDays;
    private String portfolioLink;

    // ✅ GETTERS & SETTERS

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getUsefulness() { return usefulness; }
    public void setUsefulness(String usefulness) { this.usefulness = usefulness; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }

    public Integer getDeliveryDurationInDays() { return deliveryDurationInDays; }
    public void setDeliveryDurationInDays(Integer deliveryDurationInDays) { this.deliveryDurationInDays = deliveryDurationInDays; }

    public String getPortfolioLink() { return portfolioLink; }
    public void setPortfolioLink(String portfolioLink) { this.portfolioLink = portfolioLink; }
}