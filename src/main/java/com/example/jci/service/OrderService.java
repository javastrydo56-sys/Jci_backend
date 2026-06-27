package com.example.jci.service;

import com.example.jci.dto.OrderItemResponse;
import com.example.jci.dto.OrderResponse;
import com.example.jci.dto.ProductPaymentDetailResponse;
import com.example.jci.dto.ProductPaymentResponse;
import com.example.jci.entity.*;
import com.example.jci.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService {

    @Autowired private CartRepository cartRepo;
    @Autowired private ProductRepository productRepo;
    @Autowired private OrderRepository orderRepo;
    @Autowired private SellerRepository sellerRepo;

    // ✅ PLACE ORDER
    @Transactional
    public String placeOrder(Long buyerId) {

        Cart cart = cartRepo.findByBuyerId(buyerId)
                .orElseThrow(() -> new RuntimeException("❌ Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("❌ Cart is empty");
        }

        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setPaymentStatus("PENDING");

        double total = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {

            Product product = productRepo.findById(cartItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("❌ Product not found"));

            if (product.getAvailableQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("❌ Not enough stock");
            }

            product.setAvailableQuantity(product.getAvailableQuantity() - cartItem.getQuantity());
            product.setSoldQuantity(product.getSoldQuantity() + cartItem.getQuantity());
            productRepo.save(product);

            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(product.getPrice());
            item.setOrder(order);

            total += product.getPrice() * cartItem.getQuantity();
            orderItems.add(item);
        }

        order.setItems(orderItems);
        order.setTotalAmount(total);
        orderRepo.save(order);

        cart.getItems().clear();
        cartRepo.save(cart);

        return "✅ Order placed successfully";
    }

    // ✅ GET ALL ORDERS
    @Transactional
    public List<OrderResponse> getAllOrders() {
        return orderRepo.findAll().stream().map(this::mapToResponse).toList();
    }

    // ✅ GET ORDER ENTITY (INTERNAL)
    public Order getOrderEntity(Long id) {
        return orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Order not found"));
    }

    // ✅ GET ORDER BY ID (DTO)
    @Transactional
    public OrderResponse getOrder(Long id) {
        return mapToResponse(getOrderEntity(id));
    }

    // ✅ DELETE ORDER
    public String deleteOrder(Long id) {
        orderRepo.deleteById(id);
        return "✅ Order deleted";
    }

    // ✅ ORDER HISTORY
    @Transactional
    public List<OrderResponse> getOrdersByBuyer(Long buyerId) {
        return orderRepo.findAll().stream()
                .filter(o -> o.getBuyerId().equals(buyerId))
                .map(this::mapToResponse)
                .toList();
    }

    // ✅ UPDATE STATUS
    public String updateStatus(Long orderId, String status) {
        Order order = getOrderEntity(orderId);
        order.setStatus(status);
        orderRepo.save(order);
        return "✅ Status updated to " + status;
    }

    // ✅ LEGACY PAYMENT SIMULATION
    public String makePayment(Long orderId) {
        Order order = getOrderEntity(orderId);
        if ("CONFIRMED".equals(order.getPaymentStatus()) || "PAID".equals(order.getPaymentStatus())) {
            throw new RuntimeException("❌ Already paid");
        }
        order.setPaymentStatus("CONFIRMED");
        orderRepo.save(order);
        return "✅ Payment confirmed";
    }

    // ✅ UPI: GET SELLER QR FOR AN ORDER
    @Transactional
    public Map<String, Object> getOrderUpiQr(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("❌ Order not found"));

        if (order.getItems().isEmpty()) {
            throw new RuntimeException("❌ Order has no items");
        }

        Product product = productRepo.findById(order.getItems().get(0).getProductId())
                .orElseThrow(() -> new RuntimeException("❌ Product not found"));

        Seller seller = product.getSeller();

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("sellerId", seller.getId());  // ✅ return sellerId
        result.put("sellerName", seller.getCompanyName() != null ? seller.getCompanyName() : "");
        result.put("totalAmount", order.getTotalAmount());
        result.put("paymentStatus", order.getPaymentStatus());

        return result;
    }

    private String getSellerUpiQrImageUrl(Seller seller) {
        // Since we now store UPI QR as blob, return the seller ID 
        // so buyer can fetch from /sellers/{sellerId}/upi-qr endpoint
        return seller.getUpiQrImageUrl() != null ? seller.getUpiQrImageUrl() : "";
    }
    // ✅ UPI: BUYER SUBMITS TRANSACTION ID
    public String submitUpiTransaction(Long orderId, String txnId) {
        if (txnId == null || txnId.isBlank()) {
            throw new RuntimeException("❌ Transaction ID is required");
        }

        Order order = getOrderEntity(orderId);

        if ("SUBMITTED".equals(order.getPaymentStatus()) || "CONFIRMED".equals(order.getPaymentStatus())) {
            throw new RuntimeException("❌ Payment already submitted or confirmed");
        }

        order.setUpiTransactionId(txnId.trim());
        order.setPaymentStatus("SUBMITTED");
        orderRepo.save(order);

        return "✅ Transaction ID submitted. Awaiting seller confirmation.";
    }

    // ✅ UPI: SELLER CONFIRMS OR REJECTS TRANSACTION
    @Transactional
    public String confirmUpiPayment(Long orderId, String action, Long sellerId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("❌ Order not found"));

        if (!"SUBMITTED".equals(order.getPaymentStatus())) {
            throw new RuntimeException("❌ No pending transaction submission found for this order");
        }

        // Validate seller owns this order
        boolean sellerOwnsOrder = order.getItems().stream().anyMatch(item -> {
            Product product = productRepo.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("❌ Product not found"));
            return product.getSeller().getId().equals(sellerId);
        });

        if (!sellerOwnsOrder) {
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

    // 🔥 COMMON MAPPER
    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.orderId = order.getId();
        response.buyerId = order.getBuyerId();
        response.createdAt = order.getCreatedAt();
        response.status = order.getStatus();
        response.paymentStatus = order.getPaymentStatus();
        response.upiTransactionId = order.getUpiTransactionId();
        response.totalAmount = order.getTotalAmount();

        response.items = order.getItems().stream().map(item -> {
            OrderItemResponse r = new OrderItemResponse();
            r.productId = item.getProductId();
            r.quantity = item.getQuantity();
            r.price = item.getPrice();
            r.totalPrice = item.getPrice() * item.getQuantity();
            return r;
        }).toList();

        return response;
    }

    @Transactional
    public List<OrderResponse> getOrdersBySeller(Long sellerId) {
        return orderRepo.findAll().stream().filter(order ->
            order.getItems().stream().anyMatch(item -> {
                Product product = productRepo.findById(item.getProductId())
                        .orElseThrow(() -> new RuntimeException("❌ Product not found"));
                return product.getSeller().getId().equals(sellerId);
            })
        ).map(this::mapToResponse).toList();
    }

    @Transactional
    public List<ProductPaymentResponse> getAllProductPayments() {
        return orderRepo.findAll().stream()
                .flatMap(order -> order.getItems().stream().map(item -> {
                    ProductPaymentResponse res = new ProductPaymentResponse();
                    res.orderId = order.getId();
                    res.buyerId = order.getBuyerId();

                    Product product = productRepo.findById(item.getProductId())
                            .orElseThrow(() -> new RuntimeException("❌ Product not found"));
                    res.productId = product.getId();
                    res.productName = product.getProductName();
                    res.sellerName = product.getSeller().getCompanyName();
                    res.sellerEmail = product.getSeller().getEmail();
                    res.quantity = item.getQuantity();
                    res.totalAmount = java.math.BigDecimal.valueOf(product.getPrice() * item.getQuantity());
                    res.orderStatus = order.getStatus();
                    res.paymentStatus = order.getPaymentStatus();
                    return res;
                }))
                .toList();
    }

    @Transactional
    public ProductPaymentDetailResponse getPaymentDetail(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("❌ Order not found"));

        ProductPaymentDetailResponse res = new ProductPaymentDetailResponse();
        res.orderId = order.getId();
        res.buyerId = order.getBuyerId();
        res.createdAt = order.getCreatedAt();
        res.orderStatus = order.getStatus();
        res.paymentStatus = order.getPaymentStatus();
        res.totalAmount = java.math.BigDecimal.valueOf(order.getTotalAmount());

        res.items = order.getItems().stream().map(item -> {
            Product product = productRepo.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("❌ Product not found"));

            ProductPaymentDetailResponse.Item i = new ProductPaymentDetailResponse.Item();
            i.productId = product.getId();
            i.productName = product.getProductName();
            i.sellerName = product.getSeller().getCompanyName();
            i.sellerEmail = product.getSeller().getEmail();
            i.quantity = item.getQuantity();
            i.price = java.math.BigDecimal.valueOf(product.getPrice());
            i.total = java.math.BigDecimal.valueOf(product.getPrice() * item.getQuantity());
            return i;
        }).toList();

        return res;
    }
}
