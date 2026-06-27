package com.example.jci.dto;

import java.math.BigDecimal;

public class ServiceRequest {

    private String serviceName;
    private String imageUrl;
    private String description;
    public Long getSellerId() {
		return sellerId;
	}
	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}
	private Long sellerId;
    private String category;
    private String usefulness;
    private BigDecimal cost;
    private Integer deliveryDurationInDays;
    private String portfolioLink;

    // ✅ GETTERS & SETTERS

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