package com.example.jci.repository;

import com.example.jci.entity.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BuyerRepository extends JpaRepository<Buyer, Long> {
    Optional<Buyer> findByUserId(String userId);
}