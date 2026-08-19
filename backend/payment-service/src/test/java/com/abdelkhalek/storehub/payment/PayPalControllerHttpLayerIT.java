package com.abdelkhalek.storehub.payment;

import com.abdelkhalek.storehub.payment.entity.PaymentEntity;
import com.abdelkhalek.storehub.payment.enums.PaymentStatus;
import com.abdelkhalek.storehub.payment.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PayPalControllerHttpLayerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getPayments_noFilters_returnsOkWithPagedResponse() throws Exception {
        PaymentEntity payment = buildPayment();
        Page<PaymentEntity> page = new PageImpl<>(
                List.of(payment),
                PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt")),
                1
        );

        when(paymentService.getPayments(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/payments/paypal")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_SERVICE")))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(payment.getId().toString()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getPayments_withCaptureIdFilter_passesSpecToService() throws Exception {
        Page<PaymentEntity> emptyPage = Page.empty();
        when(paymentService.getPayments(any(Specification.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/payments/paypal")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_SERVICE")))
                        .param("captureId", "cap_123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));

        verify(paymentService).getPayments(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getPayments_withCustomerIdFilter_returnsOk() throws Exception {
        UUID customerId = UUID.randomUUID();
        when(paymentService.getPayments(any(Specification.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/payments/paypal")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_SERVICE")))
                        .param("customerId", customerId.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getPayments_withInvalidCustomerId_returnsBadRequest() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/payments/paypal")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_SERVICE")))
                        .param("customerId", "not-a-uuid")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPayments_withDateRange_returnsOk() throws Exception {
        when(paymentService.getPayments(any(Specification.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/payments/paypal")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_SERVICE")))
                        .param("startDate", "2024-01-01T00:00:00")
                        .param("endDate", "2024-12-31T23:59:59")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getPayments_withInvalidDateFormat_returnsBadRequest() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/payments/paypal")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_SERVICE")))
                        .param("startDate", "01-01-2024")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPayments_withCustomPaging_passesCorrectPageable() throws Exception {
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(paymentService.getPayments(any(Specification.class), pageableCaptor.capture()))
                .thenReturn(Page.empty());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/payments/paypal")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_SERVICE"))).param("page", "2")
                        .param("size", "5")
                        .param("sort", "amount,asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Pageable captured = pageableCaptor.getValue();
        assertThat(captured.getPageNumber()).isEqualTo(2);
        assertThat(captured.getPageSize()).isEqualTo(5);
        assertThat(captured.getSort().getOrderFor("amount").getDirection())
                .isEqualTo(Sort.Direction.ASC);
    }

    @Test
    void getPayments_serviceThrowsException_returnsMappedErrorResponse() throws Exception {
        when(paymentService.getPayments(any(Specification.class), any(Pageable.class)))
                .thenThrow(new RuntimeException("db error"));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/payments/paypal")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_SERVICE")))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    private PaymentEntity buildPayment() {
        PaymentEntity payment = new PaymentEntity();
        payment.setId(UUID.randomUUID());
        payment.setCaptureId("cap_123");
        payment.setStatus(PaymentStatus.CAPTURED);
        payment.setCreatedAt(LocalDateTime.now());
        return payment;
    }
}