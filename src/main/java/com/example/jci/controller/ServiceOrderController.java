package com.example.jci.controller;

import com.example.jci.dto.BuyerSpendingResponse;
import com.example.jci.dto.SellerEarningsResponse;
import com.example.jci.dto.ServiceOrderDetailResponse;
import com.example.jci.dto.ServiceOrderResponse;
import com.example.jci.dto.ServicePaymentResponse;
import com.example.jci.service.ServiceOrderService;
import com.example.jci.service.ServiceReceiptService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/service-orders")
@CrossOrigin(
	    origins = "http://localhost:5173",
	    allowCredentials = "true"
	)
public class ServiceOrderController {

    private final ServiceOrderService service;

    public ServiceOrderController(ServiceOrderService service) {
        this.service = service;
    }

    // ✅ BOOK
    @PostMapping("/book")
    public String book(@RequestParam Long buyerId,
                       @RequestParam Long serviceId) {
        return service.bookService(buyerId, serviceId);
    }

    // ✅ BUYER HISTORY
    @GetMapping("/buyer/{buyerId}")
    public List<ServiceOrderResponse> getBuyerOrders(@PathVariable Long buyerId) {
        return service.getBuyerOrders(buyerId);
    }

    // ✅ LEGACY PAYMENT (kept for backward compat)
    @PostMapping("/pay/{orderId}")
    public String pay(@PathVariable Long orderId) {
        return service.pay(orderId);
    }

    // ✅ STATUS UPDATE
    @PutMapping("/status/{orderId}")
    public String updateStatus(@PathVariable Long orderId,
                               @RequestParam String status) {
        return service.updateStatus(orderId, status);
    }

    @GetMapping("/{orderId}")
    public ServiceOrderDetailResponse getOrderDetail(@PathVariable Long orderId) {
        return service.getOrderDetail(orderId);
    }

    // ✅ UPI: GET SELLER QR FOR A SERVICE ORDER
    @GetMapping("/{orderId}/upi-qr")
    public ResponseEntity<?> getOrderUpiQr(@PathVariable Long orderId) {
        return ResponseEntity.ok(service.getOrderUpiQr(orderId));
    }

    // ✅ UPI: BUYER SUBMITS TRANSACTION ID
    @PostMapping("/{orderId}/submit-transaction")
    public ResponseEntity<String> submitTransaction(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> body) {
        String txnId = body.get("upiTransactionId");
        return ResponseEntity.ok(service.submitUpiTransaction(orderId, txnId));
    }

    // ✅ UPI: SELLER CONFIRMS OR REJECTS TRANSACTION
    @PostMapping("/{orderId}/confirm-payment")
    public ResponseEntity<String> confirmPayment(
            @PathVariable Long orderId,
            @RequestParam String action,
            @RequestParam Long sellerId) {
        return ResponseEntity.ok(service.confirmUpiPayment(orderId, action, sellerId));
    }

    @GetMapping("/seller/{sellerId}/earnings")
    public SellerEarningsResponse getSellerEarnings(@PathVariable Long sellerId) {
        return service.getSellerEarnings(sellerId);
    }
    
    @GetMapping("/buyer/{buyerId}/summary")
    public BuyerSpendingResponse getBuyerSummary(@PathVariable Long buyerId) {
        return service.getBuyerSpending(buyerId);
    }
    
    @GetMapping("/payments")
    public List<ServicePaymentResponse> getAllPayments() {
        return service.getAllPayments();
    }
    
    @GetMapping("/payments/paid")
    public List<ServicePaymentResponse> getPaidPayments() {
        return service.getPaidPayments();
    }

    // ✅ SELLER: GET ALL ORDERS FOR SELLER (for payment confirmation panel)
    @GetMapping("/seller/{sellerId}/orders")
    public ResponseEntity<List<ServiceOrderResponse>> getSellerOrders(@PathVariable Long sellerId) {
        return ResponseEntity.ok(service.getSellerOrders(sellerId));
    }
    
    @Autowired
    private ServiceReceiptService receiptService;

    @GetMapping("/receipt/{orderId}")
    public ResponseEntity<byte[]> downloadServiceReceipt(@PathVariable Long orderId) {

        byte[] pdf = receiptService.generateServiceReceipt(orderId);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=service_receipt_" + orderId + ".pdf")
                .header("Content-Type", "application/pdf")
                .body(pdf);
    }
}
