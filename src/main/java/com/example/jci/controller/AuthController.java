package com.example.jci.controller;

import com.example.jci.dto.*;
import com.example.jci.entity.OrganizationMember;
import com.example.jci.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // ADMIN
    @PostMapping("/admin/add-member")
    public ResponseEntity<String> addMember(@RequestBody AdminCreateMemberRequest request) {
        return ResponseEntity.ok(authService.addOrganizationMember(request));
    }

    @GetMapping("/admin/JCI/members")
    public ResponseEntity<List<OrganizationMember>> getAllMembers() {
        return ResponseEntity.ok(authService.getAllMembers());
    }

    @GetMapping("/admin/JCI/members/{id}")
    public ResponseEntity<OrganizationMember> getMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(authService.getMemberById(id));
    }

    // REGISTER
    @PostMapping("/register/buyer")
    public ResponseEntity<String> registerBuyer(@RequestBody BuyerRegisterRequest request) {
        return ResponseEntity.ok(authService.registerBuyer(request));
    }

    @PostMapping("/register/seller")
    public ResponseEntity<String> registerSeller(@RequestBody SellerRegisterRequest request) {
        return ResponseEntity.ok(authService.registerSeller(request));
    }

    @PostMapping("/register/both")
    public ResponseEntity<String> registerBoth(@RequestBody BothRegisterRequest request) {
        return ResponseEntity.ok(authService.registerBoth(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // BUYERS
    @GetMapping("/admin/buyers")
    public ResponseEntity<List<BuyerResponse>> getAllBuyers() {
        return ResponseEntity.ok(authService.getAllBuyers());
    }

    @GetMapping("/admin/buyers/{id}")
    public ResponseEntity<BuyerResponse> getBuyerById(@PathVariable Long id) {
        return ResponseEntity.ok(authService.getBuyerById(id));
    }

    // SELLERS
    @GetMapping("/admin/sellers")
    public ResponseEntity<List<SellerResponse>> getAllSellers() {
        return ResponseEntity.ok(authService.getAllSellers());
    }

    @GetMapping("/admin/sellers/{id}")
    public ResponseEntity<SellerResponse> getSellerById(@PathVariable Long id) {
        return ResponseEntity.ok(authService.getSellerById(id));
    }
}