package com.example.jci.repository;

import com.example.jci.entity.OrganizationMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, Long> {
    Optional<OrganizationMember> findByUserIdAndEmail(String userId, String email);
}