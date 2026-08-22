package com.founderlink.investment;

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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvestmentServiceTest {

    @Mock private InvestmentRepository investmentRepository;
    @Mock private EventPublisher eventPublisher;
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

    @Test
    void createInvestment_success() {
        when(investmentRepository.save(any())).thenReturn(investment);
        doNothing().when(eventPublisher)
                .publishInvestmentCreated(any(), any(), any(), any());

        InvestmentResponse response = investmentService.createInvestment(request);

        assertNotNull(response);
        assertEquals("PENDING", response.getStatus());
        assertEquals(50000.0, response.getAmount());
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
    void createInvestment_zeroAmount_throwsInvalidInputException() {
        request.setAmount(0.0);
        assertThrows(InvalidInputException.class,
                () -> investmentService.createInvestment(request));
    }

    @Test
    void approveInvestment_success() {
        when(investmentRepository.findById(1L)).thenReturn(Optional.of(investment));
        when(investmentRepository.save(any())).thenReturn(investment);

        InvestmentResponse response = investmentService.approveInvestment(1L);

        assertEquals("APPROVED", response.getStatus());
    }

    @Test
    void rejectInvestment_notFound_throwsResourceNotFoundException() {
        when(investmentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> investmentService.rejectInvestment(99L));
    }

    @Test
    void getAllInvestments_paginated() {
        Pageable pageable = PageRequest.of(0, 10);
        when(investmentRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(investment)));

        Page<InvestmentResponse> page = investmentService.getAllInvestments(pageable);

        assertEquals(1, page.getTotalElements());
        assertEquals(50000.0, page.getContent().get(0).getAmount());
    }
}
