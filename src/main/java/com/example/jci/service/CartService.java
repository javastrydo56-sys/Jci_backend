package com.example.jci.service;

import com.example.jci.dto.*;
import com.example.jci.entity.*;
import com.example.jci.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Transactional
@Service
public class CartService {

    @Autowired private CartRepository cartRepo;
    @Autowired private CartItemRepository cartItemRepo;
    @Autowired private ProductRepository productRepo; // 🔥 MISSING FIX

    // ✅ ADD TO CART
    public String addToCart(AddToCartRequest request) {

        Cart cart = cartRepo.findByBuyerId(request.buyerId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setBuyerId(request.buyerId);
                    return newCart;
                });

        CartItem item = new CartItem();
        item.setProductId(request.productId);
        item.setQuantity(request.quantity);
        item.setCart(cart);

        cart.getItems().add(item);

        cartRepo.save(cart);

        return "✅ Added to cart";
    }

    // ✅ VIEW CART (🔥 FIXED)
  
    public CartResponse getCart(Long buyerId) {

        Cart cart = cartRepo.findByBuyerId(buyerId).orElse(null);

        if (cart == null) {
            CartResponse empty = new CartResponse();
            empty.buyerId = buyerId;
            empty.items = java.util.List.of();
            empty.grandTotal = 0;
            return empty;
        }

        CartResponse response = new CartResponse();
        response.cartId = cart.getId();
        response.buyerId = cart.getBuyerId();

        double grandTotal = 0;

        response.items = cart.getItems().stream().map(item -> {

            Product product = productRepo.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("❌ Product not found"));

            CartItemResponse r = new CartItemResponse();
            r.itemId = item.getId();
            r.productId = product.getId();
            r.productName = product.getProductName();
            r.price = product.getPrice();
            r.quantity = item.getQuantity();
            r.totalPrice = product.getPrice() * item.getQuantity();

            return r;

        }).toList();

        // 🔥 CALCULATE GRAND TOTAL
        for (CartItemResponse item : response.items) {
            grandTotal += item.totalPrice;
        }

        response.grandTotal = grandTotal;

        return response;
    }

    // ✅ UPDATE QUANTITY
    public String updateCartItem(UpdateCartRequest request) {

        CartItem item = cartItemRepo.findById(request.itemId)
                .orElseThrow(() -> new RuntimeException("❌ Item not found"));

        item.setQuantity(request.quantity);

        cartItemRepo.save(item);

        return "✅ Cart updated";
    }

    // ✅ REMOVE ITEM
    public String removeItem(Long itemId) {

        cartItemRepo.deleteById(itemId);

        return "✅ Item removed from cart";
    }

    // ✅ CLEAR CART
    @Transactional
    public String clearCart(Long buyerId) {

        Cart cart = cartRepo.findByBuyerId(buyerId)
                .orElseThrow(() -> new RuntimeException("❌ Cart not found"));

        cart.getItems().clear();

        cartRepo.save(cart);

        return "✅ Cart cleared";
    }
}
