package com.example.jci.controller;

import com.example.jci.dto.OrderResponse;
import com.example.jci.dto.ProductPaymentDetailResponse;
import com.example.jci.dto.ProductPaymentResponse;
import com.example.jci.service.OrderService;
import com.example.jci.service.ReceiptService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@CrossOrigin("*")
public class OrderController {

    @Autowired 
    private OrderService orderService;

    // ✅ PLACE ORDER
    @PostMapping("/place/{buyerId}")
    public ResponseEntity<String> placeOrder(@PathVariable Long buyerId) {
        return ResponseEntity.ok(orderService.placeOrder(buyerId));
    }

    // ✅ GET ALL ORDERS
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAll() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // ✅ GET ORDER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    // ✅ DELETE ORDER
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.deleteOrder(id));
    }

    // ✅ ORDER HISTORY
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<OrderResponse>> history(@PathVariable Long buyerId) {
        return ResponseEntity.ok(orderService.getOrdersByBuyer(buyerId));
    }

    // ✅ UPDATE STATUS
    @PutMapping("/status/{orderId}")
    public ResponseEntity<String> updateStatus(@PathVariable Long orderId,
                                               @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateStatus(orderId, status));
    }

    // ✅ LEGACY PAYMENT (kept for backward compat)
    @PostMapping("/pay/{orderId}")
    public ResponseEntity<String> pay(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.makePayment(orderId));
    }

    // ✅ UPI: GET SELLER QR FOR AN ORDER (buyer fetches before paying)
    @GetMapping("/{orderId}/upi-qr")
    public ResponseEntity<?> getOrderUpiQr(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderUpiQr(orderId));
    }

    // ✅ UPI: BUYER SUBMITS TRANSACTION ID
    @PostMapping("/{orderId}/submit-transaction")
    public ResponseEntity<String> submitTransaction(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> body) {
        String txnId = body.get("upiTransactionId");
        return ResponseEntity.ok(orderService.submitUpiTransaction(orderId, txnId));
    }

    // ✅ UPI: SELLER CONFIRMS OR REJECTS TRANSACTION
    @PostMapping("/{orderId}/confirm-payment")
    public ResponseEntity<String> confirmPayment(
            @PathVariable Long orderId,
            @RequestParam String action,
            @RequestParam Long sellerId) {
        return ResponseEntity.ok(orderService.confirmUpiPayment(orderId, action, sellerId));
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<OrderResponse>> getSellerOrders(@PathVariable Long sellerId) {
        return ResponseEntity.ok(orderService.getOrdersBySeller(sellerId));
    }
    
    @GetMapping("/payments")
    public ResponseEntity<List<ProductPaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(orderService.getAllProductPayments());
    }
    
    @GetMapping("/payments/{orderId}")
    public ResponseEntity<ProductPaymentDetailResponse> getPaymentDetail(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getPaymentDetail(orderId));
    }

    @Autowired
    private ReceiptService receiptService;

    @GetMapping("/receipt/{orderId}")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable Long orderId) {

        byte[] pdf = receiptService.generateReceipt(orderId);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=receipt_" + orderId + ".pdf")
                .header("Content-Type", "application/pdf")
                .body(pdf);
    }
}
