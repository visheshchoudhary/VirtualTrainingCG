package com.founderlink.investment;

import com.founderlink.investment.client.PaymentClient;
import com.founderlink.investment.dto.InvestmentRequest;
import com.founderlink.investment.dto.InvestmentResponse;
import com.founderlink.investment.entity.Investment;
import com.founderlink.investment.event.EventPublisher;
import com.founderlink.investment.exception.InvalidInputException;
import com.founderlink.investment.exception.ResourceNotFoundException;
import com.founderlink.investment.repository.InvestmentRepository;
import com.founderlink.investment.service.InvestmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvestmentServiceTest {

    @Mock private InvestmentRepository investmentRepository;
    @Mock private EventPublisher eventPublisher;
    @Mock private PaymentClient paymentClient;
    @InjectMocks private InvestmentService investmentService;

    private Investment investment;
    private InvestmentRequest request;

    @BeforeEach
    void setUp() {
        investment = new Investment();
        investment.setId(1L);
        investment.setStartupId(1L);
        investment.setInvestorId(2L);
        investment.setAmount(50000.0);
        investment.setStatus("PENDING");

        request = new InvestmentRequest();
        request.setStartupId(1L);
        request.setInvestorId(2L);
        request.setAmount(50000.0);
    }

    // ── CREATE ──────────────────────────────────────────────

    @Test
    void createInvestment_success() {
        when(investmentRepository.save(any())).thenReturn(investment);
        when(paymentClient.initiatePayment(any(), any(), any(), any()))
                .thenReturn("SUCCESS");
        doNothing().when(eventPublisher)
                .publishInvestmentCreated(any(), any(), any(), any());

        InvestmentResponse response = investmentService.createInvestment(request);

        assertNotNull(response);
        assertEquals("PENDING", response.getStatus());
        assertEquals(50000.0, response.getAmount());
        verify(paymentClient, times(1))
                .initiatePayment(any(), any(), any(), any());
        verify(eventPublisher, times(1))
                .publishInvestmentCreated(any(), any(), any(), any());
    }

    @Test
    void createInvestment_nullStartupId_throwsInvalidInputException() {
        request.setStartupId(null);
        assertThrows(InvalidInputException.class,
                () -> investmentService.createInvestment(request));
    }

    @Test
    void createInvestment_nullInvestorId_throwsInvalidInputException() {
        request.setInvestorId(null);
        assertThrows(InvalidInputException.class,
                () -> investmentService.createInvestment(request));
    }

    @Test
    void createInvestment_zeroAmount_throwsInvalidInputException() {
        request.setAmount(0.0);
        assertThrows(InvalidInputException.class,
                () -> investmentService.createInvestment(request));
    }

    @Test
    void createInvestment_negativeAmount_throwsInvalidInputException() {
        request.setAmount(-100.0);
        assertThrows(InvalidInputException.class,
                () -> investmentService.createInvestment(request));
    }

    @Test
    void createInvestment_nullAmount_throwsInvalidInputException() {
        request.setAmount(null);
        assertThrows(InvalidInputException.class,
                () -> investmentService.createInvestment(request));
    }

    // ── GET BY STARTUP ──────────────────────────────────────

    @Test
    void getByStartupId_returnsList() {
        when(investmentRepository.findByStartupId(1L))
                .thenReturn(List.of(investment));

        List<InvestmentResponse> result = investmentService.getByStartupId(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getStartupId());
    }

    @Test
    void getByStartupId_emptyList() {
        when(investmentRepository.findByStartupId(99L))
                .thenReturn(Collections.emptyList());

        List<InvestmentResponse> result = investmentService.getByStartupId(99L);

        assertTrue(result.isEmpty());
    }

    // ── GET BY INVESTOR ─────────────────────────────────────

    @Test
    void getByInvestorId_returnsList() {
        when(investmentRepository.findByInvestorId(2L))
                .thenReturn(List.of(investment));

        List<InvestmentResponse> result = investmentService.getByInvestorId(2L);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getInvestorId());
    }

    @Test
    void getByInvestorId_emptyList() {
        when(investmentRepository.findByInvestorId(99L))
                .thenReturn(Collections.emptyList());

        List<InvestmentResponse> result = investmentService.getByInvestorId(99L);

        assertTrue(result.isEmpty());
    }

    // ── APPROVE ─────────────────────────────────────────────

    @Test
    void approveInvestment_success() {
        when(investmentRepository.findById(1L)).thenReturn(Optional.of(investment));
        investment.setStatus("APPROVED");
        when(investmentRepository.save(any())).thenReturn(investment);

        InvestmentResponse response = investmentService.approveInvestment(1L);

        assertEquals("APPROVED", response.getStatus());
        verify(investmentRepository, times(1)).save(any());
    }

    @Test
    void approveInvestment_notFound_throwsResourceNotFoundException() {
        when(investmentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> investmentService.approveInvestment(99L));
    }

    // ── REJECT ──────────────────────────────────────────────

    @Test
    void rejectInvestment_success() {
        when(investmentRepository.findById(1L)).thenReturn(Optional.of(investment));
        investment.setStatus("REJECTED");
        when(investmentRepository.save(any())).thenReturn(investment);

        InvestmentResponse response = investmentService.rejectInvestment(1L);

        assertEquals("REJECTED", response.getStatus());
        verify(investmentRepository, times(1)).save(any());
    }

    @Test
    void rejectInvestment_notFound_throwsResourceNotFoundException() {
        when(investmentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> investmentService.rejectInvestment(99L));
    }

    // ── GET ALL (PAGINATED) ─────────────────────────────────

    @Test
    void getAllInvestments_paginated() {
        Pageable pageable = PageRequest.of(0, 10);
        when(investmentRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(investment)));

        Page<InvestmentResponse> page = investmentService.getAllInvestments(pageable);

        assertEquals(1, page.getTotalElements());
        assertEquals(50000.0, page.getContent().get(0).getAmount());
    }

    @Test
    void getAllInvestments_emptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(investmentRepository.findAll(pageable))
                .thenReturn(Page.empty());

        Page<InvestmentResponse> page = investmentService.getAllInvestments(pageable);

        assertEquals(0, page.getTotalElements());
        assertTrue(page.getContent().isEmpty());
    }

    // ── FALLBACK ────────────────────────────────────────────

    @Test
    void paymentFallback_savesWithFailedStatus() {
        Investment failedInvestment = new Investment();
        failedInvestment.setId(2L);
        failedInvestment.setStartupId(1L);
        failedInvestment.setInvestorId(2L);
        failedInvestment.setAmount(50000.0);
        failedInvestment.setStatus("FAILED");

        when(investmentRepository.save(any())).thenReturn(failedInvestment);

        InvestmentResponse response = investmentService.paymentFallback(
                request, new RuntimeException("Payment service down"));

        assertNotNull(response);
        assertEquals("FAILED", response.getStatus());
        assertEquals(50000.0, response.getAmount());
        verify(investmentRepository, times(1)).save(any());
    }
}
