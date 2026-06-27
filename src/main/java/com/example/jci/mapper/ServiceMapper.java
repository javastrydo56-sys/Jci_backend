package com.example.jci.mapper;

import com.example.jci.dto.ServiceRequest;
import com.example.jci.dto.ServiceResponse;
import com.example.jci.entity.ServiceEntity;
import com.example.jci.entity.Seller;

public class ServiceMapper {

    private ServiceMapper() {}

    public static ServiceEntity toEntity(ServiceRequest request) {

        if (request == null) return null;

        ServiceEntity entity = new ServiceEntity();

        entity.setServiceName(request.getServiceName());
        entity.setImageUrl(request.getImageUrl());
        entity.setDescription(request.getDescription());
        entity.setCategory(request.getCategory());
        entity.setUsefulness(request.getUsefulness());
        entity.setCost(request.getCost());
        entity.setDeliveryDurationInDays(request.getDeliveryDurationInDays());
        entity.setPortfolioLink(request.getPortfolioLink());

        return entity;
    }

    public static ServiceResponse toResponse(ServiceEntity entity) {

        if (entity == null) return null;

        ServiceResponse response = new ServiceResponse();

        response.setId(entity.getId());
        response.setServiceName(entity.getServiceName());
        response.setImageUrl(entity.getImageUrl());
        response.setDescription(entity.getDescription());
        response.setCategory(entity.getCategory());
        response.setUsefulness(entity.getUsefulness());
        response.setCost(entity.getCost());
        response.setDeliveryDurationInDays(entity.getDeliveryDurationInDays());
        response.setPortfolioLink(entity.getPortfolioLink());

        Seller seller = entity.getSeller();

        if (seller != null) {
            response.setSellerName(seller.getCompanyName());
            response.setSellerEmail(seller.getEmail());
        }

        return response;
    }
}