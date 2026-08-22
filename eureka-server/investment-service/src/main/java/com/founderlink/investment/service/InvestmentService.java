package com.founderlink.investment.service;

import com.founderlink.investment.dto.InvestmentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.founderlink.investment.dto.InvestmentResponse;
import com.founderlink.investment.entity.Investment;
import com.founderlink.investment.event.EventPublisher;
import com.founderlink.investment.exception.InvalidInputException;
import com.founderlink.investment.exception.ResourceNotFoundException;
import com.founderlink.investment.repository.InvestmentRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvestmentService {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(InvestmentService.class);

    @Autowired
    private InvestmentRepository investmentRepository;
    
    @Autowired
    private EventPublisher eventPublisher;
    
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private com.founderlink.investment.client.PaymentClient paymentClient;

    // Create investment
    @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
    public InvestmentResponse createInvestment(InvestmentRequest request) {

        logger.info("Creating investment for startupId: {}", request.getStartupId());

        // Validate startupId
        if (request.getStartupId() == null) {
            throw new InvalidInputException("Startup ID cannot be null!");
        }

        // Validate investorId
        if (request.getInvestorId() == null) {
            throw new InvalidInputException("Investor ID cannot be null!");
        }

        // Validate amount
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new InvalidInputException("Investment amount must be greater than 0!");
        }

        Investment investment = new Investment();
        investment.setStartupId(request.getStartupId());
        investment.setInvestorId(request.getInvestorId());
        investment.setAmount(request.getAmount());
        investment.setStatus("PENDING");
        //For Circuit Breaker
        restTemplate.getForObject("http://PAYMENT-SERVICE/pay", String.class);
        
        Investment saved = investmentRepository.save(investment);
        
     // Circuit Breaker call to payment-service
        String paymentStatus = paymentClient.initiatePayment(
            saved.getId(),
            saved.getInvestorId(),
            saved.getStartupId(),
            saved.getAmount()
        );
        logger.info("Payment status for investmentId {}: {}", saved.getId(), paymentStatus);

     // Publish event to RabbitMQ
     eventPublisher.publishInvestmentCreated(
         saved.getId(),
         saved.getStartupId(),
         saved.getInvestorId(),
         saved.getAmount()
     );
     logger.info("Investment created with id: {}", saved.getId());
     return mapToResponse(saved);
    }

    // Get investments by startup
    public List<InvestmentResponse> getByStartupId(Long startupId) {
        logger.info("Fetching investments for startupId: {}", startupId);
        return investmentRepository.findByStartupId(startupId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get investments by investor
    public List<InvestmentResponse> getByInvestorId(Long investorId) {
        logger.info("Fetching investments for investorId: {}", investorId);
        return investmentRepository.findByInvestorId(investorId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Approve investment
    public InvestmentResponse approveInvestment(Long id) {
        logger.info("Approving investment with id: {}", id);
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Investment not found with id: {}", id);
                    return new ResourceNotFoundException("Investment not found with id: " + id);
                });
        investment.setStatus("APPROVED");
        Investment updated = investmentRepository.save(investment);
        logger.info("Investment approved with id: {}", id);
        return mapToResponse(updated);
    }

    // Reject investment
    public InvestmentResponse rejectInvestment(Long id) {
        logger.info("Rejecting investment with id: {}", id);
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Investment not found with id: {}", id);
                    return new ResourceNotFoundException("Investment not found with id: " + id);
                });
        investment.setStatus("REJECTED");
        Investment updated = investmentRepository.save(investment);
        logger.info("Investment rejected with id: {}", id);
        return mapToResponse(updated);
    }

    // Get all investments
    public Page<InvestmentResponse> getAllInvestments(Pageable pageable) {
        logger.info("Fetching all investments");
        return investmentRepository.findAll(pageable).map(this::mapToResponse);
    }


    // Helper method
    private InvestmentResponse mapToResponse(Investment investment) {
        return new InvestmentResponse(
                investment.getId(),
                investment.getStartupId(),
                investment.getInvestorId(),
                investment.getAmount(),
                investment.getStatus()
        );
    }
    public InvestmentResponse paymentFallback(InvestmentRequest request, Exception e) {
        logger.error("Payment service failed");

        Investment investment = new Investment();
        investment.setStartupId(request.getStartupId());
        investment.setInvestorId(request.getInvestorId());
        investment.setAmount(request.getAmount());
        investment.setStatus("FAILED");

        Investment saved = investmentRepository.save(investment);

        return mapToResponse(saved);
    }
}