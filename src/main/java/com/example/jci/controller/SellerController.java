package com.example.jci.controller;

import com.example.jci.entity.Seller;
import com.example.jci.repository.SellerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/sellers")
@CrossOrigin(origins = "*")
public class SellerController {

    @Autowired
    private SellerRepository sellerRepository;

    @GetMapping("/{sellerId}/upi-qr")
    public ResponseEntity<?> getUpiQr(@PathVariable Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("❌ Seller not found"));

        byte[] imageData = seller.getUpiQrImageData();

        if (imageData == null || imageData.length == 0) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(imageData);
    }

    @PutMapping(value = "/{sellerId}/upi-qr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadUpiQr(
            @PathVariable Long sellerId,
            @RequestParam("file") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ File is required");
        }

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("❌ Seller not found"));

        try {
            seller.setUpiQrImageData(file.getBytes());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("❌ Failed to read file: " + e.getMessage());
        }

        sellerRepository.save(seller);
        return ResponseEntity.ok("✅ UPI QR uploaded successfully");
    }
}