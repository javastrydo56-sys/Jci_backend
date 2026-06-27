package com.example.jci.controller;

import com.example.jci.config.SecurityConfig;
import com.example.jci.repository.SellerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SellerController.class)
@Import(SecurityConfig.class)
class SellerControllerCorsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SellerRepository sellerRepository;

    @Test
    void shouldAllowLocalhost5174OriginForSellerQrRequests() throws Exception {
        mockMvc.perform(options("/sellers/1/upi-qr")
                        .header("Origin", "http://localhost:5174")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", containsString("http://localhost:5174")))
                .andExpect(header().string("Access-Control-Allow-Methods", containsString("GET")));
    }
}
