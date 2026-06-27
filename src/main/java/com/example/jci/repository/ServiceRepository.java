package com.example.jci.repository;

import com.example.jci.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    List<ServiceEntity> findByCategory(String category);

    // 🔥 NEW (correct)
    List<ServiceEntity> findBySellerId(Long sellerId);
}