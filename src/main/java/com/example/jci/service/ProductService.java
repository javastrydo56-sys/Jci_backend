package com.example.jci.service;

import com.example.jci.dto.*;
import com.example.jci.entity.Product;
import com.example.jci.entity.Seller;
import com.example.jci.repository.ProductRepository;
import com.example.jci.repository.SellerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private SellerRepository sellerRepo;

    // ✅ CREATE
    public String createProduct(CreateProductRequest request) {

        Seller seller = sellerRepo.findById(request.sellerId)
                .orElseThrow(() -> new RuntimeException("❌ Seller not found"));

        Product product = new Product();
        product.setProductName(request.productName);
        product.setProductImage(request.productImage);
        product.setPrice(request.price);
        product.setDescription(request.description);
        product.setExpiryDate(request.expiryDate);
        product.setSeller(seller);

        // 🔥 STOCK LOGIC
        product.setTotalQuantity(request.totalQuantity);
        product.setAvailableQuantity(request.totalQuantity);
        product.setSoldQuantity(0);

        productRepo.save(product);

        return "✅ Product created successfully";
    }

    // ✅ GET ALL
    public List<ProductResponse> getAllProducts() {
        return productRepo.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    // ✅ GET BY ID
    public ProductResponse getProductById(Long id) {
        return map(productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Product not found")));
    }

    // ✅ GET BY SELLER
    public List<ProductResponse> getProductsBySeller(Long sellerId) {
        return productRepo.findBySellerId(sellerId)
                .stream()
                .map(this::map)
                .toList();
    }

    // ✅ UPDATE
    public String updateProduct(Long id, CreateProductRequest request) {

        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Product not found"));

        product.setProductName(request.productName);
        product.setProductImage(request.productImage);
        product.setPrice(request.price);
        product.setDescription(request.description);
        product.setExpiryDate(request.expiryDate);

        productRepo.save(product);

        return "✅ Product updated successfully";
    }

    // ✅ DELETE
    public String deleteProduct(Long id) {
        productRepo.deleteById(id);
        return "✅ Product deleted successfully";
    }

    // 🔁 MAPPING
    // private ProductResponse map(Product p) {
    //     ProductResponse r = new ProductResponse();
    //     r.id = p.getId();
    //     r.productName = p.getProductName();
    //     r.productImage = p.getProductImage();
    //     r.price = p.getPrice();
    //     r.description = p.getDescription();
    //     r.expiryDate = p.getExpiryDate();

    //     r.sellerName = p.getSeller().getOwnerName();
    //     r.sellerCompany = p.getSeller().getCompanyName();
    //     r.sellerId = p.getSeller().getId();

    //     r.totalQuantity = p.getTotalQuantity();
    //     r.availableQuantity = p.getAvailableQuantity();
    //     r.soldQuantity = p.getSoldQuantity();

    //     return r;
    // }
private ProductResponse map(Product p) {
    ProductResponse r = new ProductResponse();
    r.id = p.getId();
    r.productName = p.getProductName();
    r.productImage = p.getProductImage();
    r.price = p.getPrice();
    r.description = p.getDescription();
    r.expiryDate = p.getExpiryDate();

    r.sellerName = p.getSeller().getOwnerName();
    r.sellerCompany = p.getSeller().getCompanyName();
    r.sellerPhone = p.getSeller().getCompanyPhone();  // ✅ add this
    r.sellerId = p.getSeller().getId();

    r.totalQuantity = p.getTotalQuantity();
    r.availableQuantity = p.getAvailableQuantity();
    r.soldQuantity = p.getSoldQuantity();

    return r;
}
    public String sellProduct(Long productId, int quantity) {

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("❌ Product not found"));

        if (product.getAvailableQuantity() < quantity) {
            throw new RuntimeException("❌ Not enough stock");
        }

        product.setAvailableQuantity(product.getAvailableQuantity() - quantity);
        product.setSoldQuantity(product.getSoldQuantity() + quantity);

        productRepo.save(product);

        return "✅ Product sold successfully";
    }
}