package com.example.jci.service;

import com.example.jci.dto.ServiceRequest;
import com.example.jci.dto.ServiceResponse;
import com.example.jci.entity.Seller;
import com.example.jci.entity.ServiceEntity;
import com.example.jci.mapper.ServiceMapper;
import com.example.jci.repository.SellerRepository;
import com.example.jci.repository.ServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceService {

    private final ServiceRepository repository;
    private final SellerRepository sellerRepository;

    public ServiceService(ServiceRepository repository, SellerRepository sellerRepository) {
        this.repository = repository;
        this.sellerRepository = sellerRepository;
    }

    // ✅ CREATE
    public ServiceResponse createService(ServiceRequest request) {

        Seller seller = sellerRepository.findById(request.getSellerId())
                .orElseThrow(() -> new RuntimeException("❌ Seller not found"));

        ServiceEntity entity = ServiceMapper.toEntity(request);

        entity.setSeller(seller); // 🔥 LINK SELLER

        ServiceEntity saved = repository.save(entity);

        return ServiceMapper.toResponse(saved); // ✅ FIXED
    }

    // ✅ GET BY ID
    public ServiceResponse getService(Long id) {

        ServiceEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Service not found with id: " + id));

        return ServiceMapper.toResponse(entity);
    }

    // ✅ GET ALL
    public List<ServiceResponse> getAllServices() {

        return repository.findAll()
                .stream()
                .map(ServiceMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ✅ GET BY CATEGORY
    public List<ServiceResponse> getByCategory(String category) {

        return repository.findByCategory(category)
                .stream()
                .map(ServiceMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ✅ UPDATE
    public ServiceResponse updateService(Long id, ServiceRequest request) {

        ServiceEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Service not found with id: " + id));

        entity.setServiceName(request.getServiceName());
        entity.setImageUrl(request.getImageUrl());
        entity.setDescription(request.getDescription());
        entity.setCategory(request.getCategory());
        entity.setUsefulness(request.getUsefulness());
        entity.setCost(request.getCost());
        entity.setDeliveryDurationInDays(request.getDeliveryDurationInDays());
        entity.setPortfolioLink(request.getPortfolioLink());

        // 🔥 OPTIONAL: UPDATE SELLER ALSO
        if (request.getSellerId() != null) {
            Seller seller = sellerRepository.findById(request.getSellerId())
                    .orElseThrow(() -> new RuntimeException("❌ Seller not found"));
            entity.setSeller(seller);
        }

        ServiceEntity updated = repository.save(entity);

        return ServiceMapper.toResponse(updated);
    }

    // ✅ DELETE
    public void deleteService(Long id) {

        if (!repository.existsById(id)) {
            throw new RuntimeException("❌ Service not found with id: " + id);
        }

        repository.deleteById(id);
    }
    
    public List<ServiceResponse> getBySeller(Long sellerId) {

        return repository.findBySellerId(sellerId)
                .stream()
                .map(ServiceMapper::toResponse)
                .collect(Collectors.toList());
    }
}