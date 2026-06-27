package com.example.jci;

import com.example.jci.dto.*;
import com.example.jci.entity.Buyer;
import com.example.jci.entity.OrganizationMember;
import com.example.jci.entity.Seller;
import com.example.jci.repository.BuyerRepository;
import com.example.jci.repository.OrganizationMemberRepository;
import com.example.jci.repository.SellerRepository;
import com.example.jci.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private OrganizationMemberRepository orgRepo;

    @Mock
    private BuyerRepository buyerRepo;

    @Mock
    private SellerRepository sellerRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private OrganizationMember member;

    @BeforeEach
    void setUp() {
        member = new OrganizationMember();
        member.setUserId("user123");
        member.setEmail("user@example.com");
        member.setOrganizationName("JCI Tech");
        member.setRegistered(false);
    }

    @Test
    void testRegisterBoth_Success() {
        BothRegisterRequest request = new BothRegisterRequest();
        request.userId = "user123";
        request.email = "user@example.com";
        request.password = "password123";
        request.username = "buyer123";
        request.companyName = "SellerCorp";

        when(orgRepo.findByUserIdAndEmail(request.userId, request.email)).thenReturn(Optional.of(member));
        when(buyerRepo.findByUserId(request.userId)).thenReturn(Optional.empty());
        when(sellerRepo.findByUserId(request.userId)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password)).thenReturn("hashed_password");

        String result = authService.registerBoth(request);

        assertEquals("✅ Registered as both Buyer and Seller successfully", result);
        verify(buyerRepo, times(1)).save(any(Buyer.class));
        verify(sellerRepo, times(1)).save(any(Seller.class));
        assertTrue(member.isRegistered());
        verify(orgRepo, times(1)).save(member);
    }

    @Test
    void testIncrementalRegister_Success() {
        // Step 1: Register Buyer
        BuyerRegisterRequest buyerRequest = new BuyerRegisterRequest();
        buyerRequest.userId = "user123";
        buyerRequest.email = "user@example.com";
        buyerRequest.password = "password123";
        buyerRequest.username = "buyer123";

        when(orgRepo.findByUserIdAndEmail(buyerRequest.userId, buyerRequest.email)).thenReturn(Optional.of(member));
        when(buyerRepo.findByUserId(buyerRequest.userId)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(buyerRequest.password)).thenReturn("hashed_password");

        String buyerResult = authService.registerBuyer(buyerRequest);
        assertEquals("✅ Buyer registered successfully", buyerResult);

        // Step 2: Register Seller under same credentials (relax checks)
        SellerRegisterRequest sellerRequest = new SellerRegisterRequest();
        sellerRequest.userId = "user123";
        sellerRequest.email = "user@example.com";
        sellerRequest.password = "password123";
        sellerRequest.companyName = "SellerCorp";

        member.setRegistered(true); // Simulate state after first registration
        when(sellerRepo.findByUserId(sellerRequest.userId)).thenReturn(Optional.empty());

        String sellerResult = authService.registerSeller(sellerRequest);
        assertEquals("✅ Seller registered successfully", sellerResult);

        verify(buyerRepo, times(1)).save(any(Buyer.class));
        verify(sellerRepo, times(1)).save(any(Seller.class));
    }

    @Test
    void testLogin_DualRole() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.userId = "user123";
        loginRequest.password = "password123";

        Buyer buyer = new Buyer();
        buyer.setUserId("user123");
        buyer.setPassword("hashed_password");

        Seller seller = new Seller();
        seller.setUserId("user123");
        seller.setPassword("hashed_password");

        when(buyerRepo.findByUserId("user123")).thenReturn(Optional.of(buyer));
        when(sellerRepo.findByUserId("user123")).thenReturn(Optional.of(seller));
        when(passwordEncoder.matches("password123", "hashed_password")).thenReturn(true);

        LoginResponse loginResponse = authService.login(loginRequest);

        assertEquals("BOTH", loginResponse.type);
        assertEquals("✅ Login successful as both Buyer and Seller", loginResponse.message);
        assertNotNull(loginResponse.data);
        assertTrue(loginResponse.data instanceof java.util.Map);

        java.util.Map<?, ?> dataMap = (java.util.Map<?, ?>) loginResponse.data;
        assertNotNull(dataMap.get("buyer"));
        assertNotNull(dataMap.get("seller"));
    }
}
