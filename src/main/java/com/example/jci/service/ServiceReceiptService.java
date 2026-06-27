package com.example.jci.service;

import com.example.jci.entity.*;
import com.example.jci.repository.*;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import java.io.ByteArrayOutputStream;

@Service
public class ServiceReceiptService {

    private final ServiceOrderRepository orderRepo;
    private final BuyerRepository buyerRepo;
    private final TemplateEngine templateEngine;

    public ServiceReceiptService(ServiceOrderRepository orderRepo,
                                 BuyerRepository buyerRepo,
                                 TemplateEngine templateEngine) {

        this.orderRepo = orderRepo;
        this.buyerRepo = buyerRepo;
        this.templateEngine = templateEngine;
    }

    public byte[] generateServiceReceipt(Long orderId) {

        ServiceOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("❌ Service Order not found"));

        // ✅ FIX: Get service directly
        ServiceEntity service = order.getService();

        if (service == null) {
            throw new RuntimeException("❌ Service not linked with order");
        }

        Buyer buyer = buyerRepo.findById(order.getBuyerId())
                .orElseThrow(() -> new RuntimeException("❌ Buyer not found"));

        Seller seller = service.getSeller();

        Context context = new Context();

        // ORDER
        context.setVariable("orderId", order.getId());
        context.setVariable("date", order.getCreatedAt());
        context.setVariable("paymentStatus", order.getPaymentStatus());

        // BUYER
        context.setVariable("buyerName", buyer.getUsername());
        context.setVariable("buyerEmail", buyer.getEmail());
        context.setVariable("buyerPhone", buyer.getPhoneNumber());
        context.setVariable("buyerAddress", buyer.getAddress());

        // SELLER
        context.setVariable("sellerName", seller.getCompanyName());
        context.setVariable("sellerEmail", seller.getEmail());
        context.setVariable("sellerPhone", seller.getCompanyPhone());
        context.setVariable("sellerAddress", seller.getCompanyAddress());

        // SERVICE DETAILS
        context.setVariable("serviceName", service.getServiceName());
        context.setVariable("cost", String.format("₹ %.2f", service.getCost()));

        double cost = service.getCost().doubleValue();
        double tax = cost * 0.18;
        double finalTotal = cost + tax;

        context.setVariable("tax", String.format("₹ %.2f", tax));
        context.setVariable("finalTotal", String.format("₹ %.2f", finalTotal));

        String html = templateEngine.process("service-receipt", context);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();
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