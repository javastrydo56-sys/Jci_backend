package com.example.jci.service;

import com.example.jci.dto.*;
import com.example.jci.entity.*;
import com.example.jci.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    @Autowired private OrganizationMemberRepository orgRepo;
    @Autowired private BuyerRepository buyerRepo;
    @Autowired private SellerRepository sellerRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    // ================= ADMIN =================

    public String addOrganizationMember(AdminCreateMemberRequest request) {

        if (orgRepo.findByUserIdAndEmail(request.userId, request.email).isPresent()) {
            throw new RuntimeException("❌ Member already exists");
        }

        OrganizationMember member = new OrganizationMember();
        member.setUserId(request.userId);
        member.setEmail(request.email);
        member.setOrganizationName(request.organizationName);
        member.setRegistered(false);

        orgRepo.save(member);

        return "✅ Member added successfully";
    }

    public List<OrganizationMember> getAllMembers() {
        return orgRepo.findAll();
    }

    public OrganizationMember getMemberById(Long id) {
        return orgRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Member not found"));
    }

    // ================= REGISTER =================

    public String registerBuyer(BuyerRegisterRequest request) {

        OrganizationMember member = orgRepo
                .findByUserIdAndEmail(request.userId, request.email)
                .orElseThrow(() -> new RuntimeException("❌ Not authorized"));

        if (buyerRepo.findByUserId(request.userId).isPresent()) {
            throw new RuntimeException("❌ Already registered as a Buyer");
        }

        Buyer buyer = new Buyer();
        buyer.setUserId(request.userId);
        buyer.setUsername(request.username);
        buyer.setEmail(request.email);
        buyer.setPassword(passwordEncoder.encode(request.password));
        buyer.setPhoneNumber(request.phoneNumber);
        buyer.setAddress(request.address);
        buyer.setCity(request.city);
        buyer.setState(request.state);
        buyer.setCountry(request.country);
        buyer.setOrganizationName(member.getOrganizationName());

        buyerRepo.save(buyer);

        member.setRegistered(true);
        orgRepo.save(member);

        return "✅ Buyer registered successfully";
    }

    public String registerSeller(SellerRegisterRequest request) {

        OrganizationMember member = orgRepo
                .findByUserIdAndEmail(request.userId, request.email)
                .orElseThrow(() -> new RuntimeException("❌ Not authorized"));

        if (sellerRepo.findByUserId(request.userId).isPresent()) {
            throw new RuntimeException("❌ Already registered as a Seller");
        }

        Seller seller = new Seller();
        seller.setUserId(request.userId);
        seller.setEmail(request.email);
        seller.setPassword(passwordEncoder.encode(request.password));
        seller.setCompanyName(request.companyName);
        seller.setCompanyPhone(request.companyPhone);
        seller.setOwnerName(request.ownerName);
        seller.setCompanyAddress(request.companyAddress);
        seller.setProductOrService(request.productOrService);
        seller.setLocation(request.location);
        seller.setCompanyImage(request.companyImage);
        seller.setCompanyDescription(request.companyDescription);
        seller.setOrganizationName(member.getOrganizationName());

        sellerRepo.save(seller);

        member.setRegistered(true);
        orgRepo.save(member);

        return "✅ Seller registered successfully";
    }

    public String registerBoth(BothRegisterRequest request) {

        if (buyerRepo.findByUserId(request.userId).isPresent()
                && sellerRepo.findByUserId(request.userId).isPresent()) {
            throw new RuntimeException("❌ Already registered as both Buyer and Seller");
        }

        String encodedPassword = passwordEncoder.encode(request.password);

        if (buyerRepo.findByUserId(request.userId).isEmpty()) {
            Buyer buyer = new Buyer();
            buyer.setUserId(request.userId);
            buyer.setUsername(request.username);
            buyer.setEmail(request.email);
            buyer.setPassword(encodedPassword);
            buyer.setPhoneNumber(request.phoneNumber);
            buyer.setAddress(request.address);
            buyer.setCity(request.city);
            buyer.setState(request.state);
            buyer.setCountry(request.country);
            buyer.setOrganizationName(request.organizationName);
            buyerRepo.save(buyer);
        }

        if (sellerRepo.findByUserId(request.userId).isEmpty()) {
            Seller seller = new Seller();
            seller.setUserId(request.userId);
            seller.setEmail(request.email);
            seller.setPassword(encodedPassword);
            seller.setCompanyName(request.companyName);
            seller.setCompanyPhone(request.companyPhone);
            seller.setOwnerName(request.ownerName);
            seller.setCompanyAddress(request.companyAddress);
            seller.setProductOrService(request.productOrService);
            seller.setLocation(request.location);
            seller.setCompanyImage(request.companyImage);
            seller.setCompanyDescription(request.companyDescription);
            seller.setOrganizationName(request.organizationName);
            sellerRepo.save(seller);
        }

        return "✅ Registered as both Buyer and Seller successfully";
    }

    // ================= LOGIN =================

    public LoginResponse login(LoginRequest request) {

        var buyerOpt = buyerRepo.findByUserId(request.userId);
        var sellerOpt = sellerRepo.findByUserId(request.userId);

        if (buyerOpt.isPresent() && sellerOpt.isPresent()) {
            Buyer buyer = buyerOpt.get();
            Seller seller = sellerOpt.get();

            if (!passwordEncoder.matches(request.password, buyer.getPassword())) {
                throw new RuntimeException("❌ Invalid password");
            }

            BuyerResponse buyerRes = mapBuyer(buyer);
            SellerResponse sellerRes = mapSeller(seller);

            java.util.Map<String, Object> bothData = new java.util.HashMap<>();
            bothData.put("buyer", buyerRes);
            bothData.put("seller", sellerRes);

            return new LoginResponse(
                    "✅ Login successful as both Buyer and Seller",
                    "BOTH",
                    bothData
            );
        }

        // 🔍 Check Buyer
        if (buyerOpt.isPresent()) {
            Buyer buyer = buyerOpt.get();

            if (!passwordEncoder.matches(request.password, buyer.getPassword())) {
                throw new RuntimeException("❌ Invalid password");
            }

            BuyerResponse response = mapBuyer(buyer);

            return new LoginResponse(
                    "✅ Buyer login successful",
                    "BUYER",
                    response
            );
        }

        // 🔍 Check Seller
        if (sellerOpt.isPresent()) {
            Seller seller = sellerOpt.get();

            if (!passwordEncoder.matches(request.password, seller.getPassword())) {
                throw new RuntimeException("❌ Invalid password");
            }

            SellerResponse response = mapSeller(seller);

            return new LoginResponse(
                    "✅ Seller login successful",
                    "SELLER",
                    response
            );
        }

        throw new RuntimeException("❌ User not found");
    }
    // ================= MAPPING =================

    private BuyerResponse mapBuyer(Buyer b) {
        BuyerResponse r = new BuyerResponse();
        r.id = b.getId();
        r.userId = b.getUserId();
        r.username = b.getUsername();
        r.email = b.getEmail();
        r.phoneNumber = b.getPhoneNumber();
        r.address = b.getAddress();
        r.city = b.getCity();
        r.state = b.getState();
        r.country = b.getCountry();
        r.organizationName = b.getOrganizationName();
        return r;
    }

    private SellerResponse mapSeller(Seller s) {
        SellerResponse r = new SellerResponse();
        r.id = s.getId();
        r.userId = s.getUserId();
        r.email = s.getEmail();
        r.companyName = s.getCompanyName();
        r.companyPhone = s.getCompanyPhone();
        r.ownerName = s.getOwnerName();
        r.companyAddress = s.getCompanyAddress();
        r.productOrService = s.getProductOrService();
        r.location = s.getLocation();
        r.companyImage = s.getCompanyImage();
        r.companyDescription = s.getCompanyDescription();
        r.organizationName = s.getOrganizationName();
        return r;
    }

    // ================= FETCH =================

    public List<BuyerResponse> getAllBuyers() {
        return buyerRepo.findAll().stream().map(this::mapBuyer).toList();
    }

    public BuyerResponse getBuyerById(Long id) {
        return mapBuyer(
                buyerRepo.findById(id)
                        .orElseThrow(() -> new RuntimeException("❌ Buyer not found"))
        );
    }

    public List<SellerResponse> getAllSellers() {
        return sellerRepo.findAll().stream().map(this::mapSeller).toList();
    }

    public SellerResponse getSellerById(Long id) {
        return mapSeller(
                sellerRepo.findById(id)
                        .orElseThrow(() -> new RuntimeException("❌ Seller not found"))
        );
    }
}
