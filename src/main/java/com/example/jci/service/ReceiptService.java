package com.example.jci.service;

import com.example.jci.entity.*;
import com.example.jci.repository.*;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import java.io.ByteArrayOutputStream;
import java.util.*;

@Service
public class ReceiptService {

    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final BuyerRepository buyerRepo;
    private final TemplateEngine templateEngine;

    public ReceiptService(OrderRepository orderRepo,
                          ProductRepository productRepo,
                          BuyerRepository buyerRepo,
                          TemplateEngine templateEngine) {

        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.buyerRepo = buyerRepo;
        this.templateEngine = templateEngine;
    }

    public byte[] generateReceipt(Long orderId) {

        Order order = orderRepo.findByIdWithItems(orderId)
                .orElseThrow(() -> new RuntimeException("❌ Order not found"));

        Buyer buyer = buyerRepo.findById(order.getBuyerId())
                .orElseThrow(() -> new RuntimeException("❌ Buyer not found"));

        List<Map<String, Object>> items = new ArrayList<>();

        double subtotal = 0;
        Seller seller = null;

        for (OrderItem item : order.getItems()) {

            Product product = productRepo.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("❌ Product not found"));

            seller = product.getSeller();

            double total = product.getPrice() * item.getQuantity();
            subtotal += total;

            Map<String, Object> map = new HashMap<>();
            map.put("name", product.getProductName());
            map.put("qty", item.getQuantity());
            map.put("total", String.format("₹ %.2f", total));

            items.add(map);
        }

        double tax = subtotal * 0.18;
        double finalTotal = subtotal + tax;

        Context context = new Context();

        context.setVariable("orderId", order.getId());
        context.setVariable("date", order.getCreatedAt());
        context.setVariable("paymentStatus", order.getPaymentStatus());

        // Buyer
        context.setVariable("buyerName", buyer.getUsername());
        context.setVariable("buyerEmail", buyer.getEmail());
        context.setVariable("buyerPhone", buyer.getPhoneNumber());
        context.setVariable("buyerAddress", buyer.getAddress());

        // Seller
        if (seller != null) {
            context.setVariable("sellerName", seller.getCompanyName());
            context.setVariable("sellerEmail", seller.getEmail());
            context.setVariable("sellerPhone", seller.getCompanyPhone());
            context.setVariable("sellerAddress", seller.getCompanyAddress());
        }

        context.setVariable("items", items);
        context.setVariable("subtotal", String.format("₹ %.2f", subtotal));
        context.setVariable("tax", String.format("₹ %.2f", tax));
        context.setVariable("finalTotal", String.format("₹ %.2f", finalTotal));

        String html = templateEngine.process("receipt", context);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();

            // ✅ IMPORTANT: base path not needed because we use full path in HTML
            builder.withHtmlContent(html, null);

            builder.toStream(out);
            builder.run();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("❌ PDF generation failed: " + e.getMessage());
        }

        return out.toByteArray();
    }
}