package com.example.jci.controller;

import com.example.jci.dto.*;
import com.example.jci.entity.Cart;
import com.example.jci.service.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@CrossOrigin("*")
public class CartController {

    @Autowired private CartService cartService;

    // ADD
    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addToCart(request));
    }

    // VIEW
    @GetMapping("/{buyerId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable Long buyerId) {
        return ResponseEntity.ok(cartService.getCart(buyerId));
    }

    // UPDATE
    @PutMapping("/update")
    public ResponseEntity<String> update(@RequestBody UpdateCartRequest request) {
        return ResponseEntity.ok(cartService.updateCartItem(request));
    }

    // REMOVE ITEM
    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<String> remove(@PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItem(itemId));
    }

    // CLEAR CART
    @DeleteMapping("/clear/{buyerId}")
    public ResponseEntity<String> clear(@PathVariable Long buyerId) {
        return ResponseEntity.ok(cartService.clearCart(buyerId));
    }
}