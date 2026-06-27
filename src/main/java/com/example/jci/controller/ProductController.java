package com.example.jci.controller;

import com.example.jci.dto.*;
import com.example.jci.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin("*")
public class ProductController {

    @Autowired
    private ProductService productService;

    // CREATE
    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // GET BY SELLER
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<ProductResponse>> getBySeller(@PathVariable Long sellerId) {
        return ResponseEntity.ok(productService.getProductsBySeller(sellerId));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable Long id,
                                         @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(productService.deleteProduct(id));
    }
    
    @PostMapping("/sell/{productId}")
    public ResponseEntity<String> sellProduct(@PathVariable Long productId,
                                              @RequestParam int quantity) {
        return ResponseEntity.ok(productService.sellProduct(productId, quantity));
    }
}