package com.example.jci.controller;


import com.example.jci.dto.*;
import com.example.jci.service.ServiceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceService service;

    // ✅ FIX: Constructor Injection
    public ServiceController(ServiceService service) {
        this.service = service;
    }

    @PostMapping
    public ServiceResponse create(@Valid @RequestBody ServiceRequest request) {
        return service.createService(request);
    }

    @GetMapping("/{id}")
    public ServiceResponse get(@PathVariable Long id) {
        return service.getService(id);
    }

    @GetMapping
    public List<ServiceResponse> getAll() {
        return service.getAllServices();
    }

    @GetMapping("/category/{category}")
    public List<ServiceResponse> getByCategory(@PathVariable String category) {
        return service.getByCategory(category);
    }

    @PutMapping("/{id}")
    public ServiceResponse update(@PathVariable Long id,
                                 @Valid @RequestBody ServiceRequest request) {
        return service.updateService(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteService(id);
    }
    @GetMapping("/seller/{sellerId}")
    public List<ServiceResponse> getBySeller(@PathVariable Long sellerId) {
        return service.getBySeller(sellerId);
    }
}