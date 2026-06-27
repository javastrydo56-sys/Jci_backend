package com.example.jci.service;

import com.example.jci.dto.BuyerSpendingResponse;
import com.example.jci.dto.SellerEarningsResponse;
import com.example.jci.dto.ServiceOrderDetailResponse;
import com.example.jci.dto.ServiceOrderResponse;
import com.example.jci.dto.ServicePaymentResponse;
import com.example.jci.entity.*;
import com.example.jci.repository.*;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServiceOrderService {

    private final ServiceOrderRepository orderRepo;
    private final ServiceRepository serviceRepo;

    public ServiceOrderService(ServiceOrderRepository orderRepo,
                               ServiceRepository serviceRepo) {
        this.orderRepo = orderRepo;
        this.serviceRepo = serviceRepo;
    }

    // ✅ BOOK SERVICE
    public String bookService(Long buyerId, Long serviceId) {

        ServiceEntity service = serviceRepo.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("❌ Service not found"));

        ServiceOrder order = new ServiceOrder();
        order.setBuyerId(buyerId);
        order.setService(service);
        order.setStatus("PENDING");
        order.setPaymentStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        orderRepo.save(order);

        return "✅ Service booked successfully";
    }

    // ✅ GET BUYER BOOKINGS
    public List<ServiceOrderResponse> getBuyerOrders(Long buyerId) {
        return orderRepo.findByBuyerId(buyerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ✅ LEGACY PAYMENT
    public String pay(Long orderId) {
        ServiceOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("❌ Order not found"));
        order.setPaymentStatus("CONFIRMED");
        orderRepo.save(order);
        return "✅ Payment confirmed";
    }

    // ✅ UPDATE STATUS (SELLER SIDE)
    public String updateStatus(Long orderId, String status) {
        ServiceOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("❌ Order not found"));
        order.setStatus(status);
        orderRepo.save(order);
        return "✅ Status updated";
    }

    // ✅ UPI: GET SELLER QR FOR A SERVICE ORDER
    public Map<String, Object> getOrderUpiQr(Long orderId) {
        ServiceOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("❌ Order not found"));

        ServiceEntity svc = order.getService();
        Seller seller = svc.getSeller();

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("sellerId", seller.getId());  // ✅ return sellerId
        result.put("sellerName", seller.getCompanyName() != null ? seller.getCompanyName() : "");
        result.put("totalAmount", svc.getCost());
        result.put("paymentStatus", order.getPaymentStatus());

        return result;
    }

    private String getSellerUpiQrImageUrl(Seller seller) {
        return seller.getUpiQrImageUrl() != null ? seller.getUpiQrImageUrl() : "";
    }

    // ✅ UPI: BUYER SUBMITS TRANSACTION ID
    public String submitUpiTransaction(Long orderId, String txnId) {
        if (txnId == null || txnId.isBlank()) {
            throw new RuntimeException("❌ Transaction ID is required");
        }

        ServiceOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("❌ Order not found"));

        if ("SUBMITTED".equals(order.getPaymentStatus()) || "CONFIRMED".equals(order.getPaymentStatus())) {
            throw new RuntimeException("❌ Payment already submitted or confirmed");
        }

        order.setUpiTransactionId(txnId.trim());
        order.setPaymentStatus("SUBMITTED");
        orderRepo.save(order);

        return "✅ Transaction ID submitted. Awaiting seller confirmation.";
    }

    // ✅ UPI: SELLER CONFIRMS OR REJECTS TRANSACTION
    public String confirmUpiPayment(Long orderId, String action, Long sellerId) {
        ServiceOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("❌ Order not found"));

        if (!"SUBMITTED".equals(order.getPaymentStatus())) {
            throw new RuntimeException("❌ No pending transaction submission found");
        }

        // Validate seller owns this order
        if (!order.getService().getSeller().getId().equals(sellerId)) {
            throw new RuntimeException("❌ Unauthorized: You do not own this order");
        }

        if ("CONFIRM".equalsIgnoreCase(action)) {
            order.setPaymentStatus("CONFIRMED");
            orderRepo.save(order);
            return "✅ Payment confirmed successfully";
        } else if ("REJECT".equalsIgnoreCase(action)) {
            order.setPaymentStatus("REJECTED");
            order.setUpiTransactionId(null);
            orderRepo.save(order);
            return "✅ Payment rejected. Buyer can re-submit a new transaction ID.";
        } else {
            throw new RuntimeException("❌ Invalid action. Use CONFIRM or REJECT.");
        }
    }

    // ✅ GET ALL ORDERS FOR A SELLER
    public List<ServiceOrderResponse> getSellerOrders(Long sellerId) {
        return orderRepo.findAll().stream()
                .filter(o -> o.getService().getSeller().getId().equals(sellerId))
                .map(this::mapToResponse)
                .toList();
    }

    // 🔥 MAPPER
    private ServiceOrderResponse mapToResponse(ServiceOrder order) {
        ServiceOrderResponse res = new ServiceOrderResponse();
        res.orderId = order.getId();
        res.buyerId = order.getBuyerId();
        res.serviceId = order.getService().getId();
        res.serviceName = order.getService().getServiceName();
        res.sellerName = order.getService().getSeller().getCompanyName();
        res.sellerEmail = order.getService().getSeller().getEmail();
        res.sellerId = order.getService().getSeller().getId();
        res.status = order.getStatus();
        res.paymentStatus = order.getPaymentStatus();
        res.upiTransactionId = order.getUpiTransactionId();
        return res;
    }

    public ServiceOrderDetailResponse getOrderDetail(Long orderId) {
        ServiceOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("❌ Order not found"));

        ServiceEntity service = order.getService();

        ServiceOrderDetailResponse res = new ServiceOrderDetailResponse();
        res.orderId = order.getId();
        res.buyerId = order.getBuyerId();
        res.serviceId = service.getId();
        res.serviceName = service.getServiceName();
        res.description = service.getDescription();
        res.cost = service.getCost();
        res.imageUrl = service.getImageUrl();
        res.sellerName = service.getSeller().getCompanyName();
        res.sellerEmail = service.getSeller().getEmail();
        res.status = order.getStatus();
        res.paymentStatus = order.getPaymentStatus();
        res.createdAt = order.getCreatedAt();

        return res;
    }

    public SellerEarningsResponse getSellerEarnings(Long sellerId) {
        List<ServiceOrder> orders = orderRepo.findAll().stream()
                .filter(o -> o.getService().getSeller().getId().equals(sellerId))
                .toList();

        if (orders.isEmpty()) {
            throw new RuntimeException("❌ No orders found for seller");
        }

        SellerEarningsResponse res = new SellerEarningsResponse();
        res.sellerId = sellerId;
        res.sellerName = orders.get(0).getService().getSeller().getCompanyName();

        double total = orders.stream()
                .filter(o -> "CONFIRMED".equals(o.getPaymentStatus()) || "PAID".equals(o.getPaymentStatus()))
                .map(o -> o.getService().getCost())
                .mapToDouble(java.math.BigDecimal::doubleValue)
                .sum();

        res.totalEarnings = java.math.BigDecimal.valueOf(total);
        res.totalOrders = orders.size();
        res.orders = orders.stream().map(this::mapToResponse).toList();

        return res;
    }

    public BuyerSpendingResponse getBuyerSpending(Long buyerId) {
        List<ServiceOrder> orders = orderRepo.findByBuyerId(buyerId);

        if (orders.isEmpty()) {
            throw new RuntimeException("❌ No orders found for buyer");
        }

        BuyerSpendingResponse res = new BuyerSpendingResponse();
        res.buyerId = buyerId;

        double total = orders.stream()
                .filter(o -> "CONFIRMED".equals(o.getPaymentStatus()) || "PAID".equals(o.getPaymentStatus()))
                .map(o -> o.getService().getCost())
                .mapToDouble(java.math.BigDecimal::doubleValue)
                .sum();

        long paidCount = orders.stream()
                .filter(o -> "CONFIRMED".equals(o.getPaymentStatus()) || "PAID".equals(o.getPaymentStatus()))
                .count();

        res.totalSpent = java.math.BigDecimal.valueOf(total);
        res.totalOrders = orders.size();
        res.paidOrders = (int) paidCount;
        res.orders = orders.stream().map(this::mapToResponse).toList();

        return res;
    }

    public List<ServicePaymentResponse> getAllPayments() {
        return orderRepo.findAll().stream().map(order -> {
            ServicePaymentResponse res = new ServicePaymentResponse();
            res.orderId = order.getId();
            res.buyerId = order.getBuyerId();
            res.serviceId = order.getService().getId();
            res.serviceName = order.getService().getServiceName();
            res.sellerName = order.getService().getSeller().getCompanyName();
            res.sellerEmail = order.getService().getSeller().getEmail();
            res.amount = order.getService().getCost();
            res.status = order.getStatus();
            res.paymentStatus = order.getPaymentStatus();
            return res;
        }).toList();
    }

    public List<ServicePaymentResponse> getPaidPayments() {
        return orderRepo.findAll().stream()
                .filter(o -> "CONFIRMED".equals(o.getPaymentStatus()) || "PAID".equals(o.getPaymentStatus()))
                .map(order -> {
                    ServicePaymentResponse res = new ServicePaymentResponse();
                    res.orderId = order.getId();
                    res.buyerId = order.getBuyerId();
                    res.serviceId = order.getService().getId();
                    res.serviceName = order.getService().getServiceName();
                    res.sellerName = order.getService().getSeller().getCompanyName();
                    res.sellerEmail = order.getService().getSeller().getEmail();
                    res.amount = order.getService().getCost();
                    res.status = order.getStatus();
                    res.paymentStatus = order.getPaymentStatus();
                    return res;
                }).toList();
    }
}
