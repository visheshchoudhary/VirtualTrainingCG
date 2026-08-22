package com.founderlink.startup.service;

import com.founderlink.startup.dto.StartupRequest;
import com.founderlink.startup.dto.StartupResponse;
import com.founderlink.startup.entity.Startup;
import com.founderlink.startup.event.EventPublisher;
import com.founderlink.startup.exception.ResourceNotFoundException;
import com.founderlink.startup.repository.StartupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class StartupService {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(StartupService.class);

    @Autowired
    private StartupRepository startupRepository;
    
    @Autowired
    private EventPublisher eventPublisher;

    @CacheEvict(value = "startups", allEntries = true)
    public StartupResponse createStartup(StartupRequest request) {

        logger.info("Creating startup: {}", request.getName());

        // Validate name
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new RuntimeException("Startup name cannot be empty!");
        }

        // Validate funding goal
        if (request.getFundingGoal() == null || request.getFundingGoal() <= 0) {
            throw new RuntimeException("Funding goal must be greater than 0!");
        }

        // Validate stage
        if (request.getStage() == null || request.getStage().isEmpty()) {
            throw new RuntimeException("Stage cannot be empty!");
        }

        Startup startup = new Startup();
        startup.setName(request.getName());
        startup.setDescription(request.getDescription());
        startup.setIndustry(request.getIndustry());
        startup.setProblemStatement(request.getProblemStatement());
        startup.setSolution(request.getSolution());
        startup.setFundingGoal(request.getFundingGoal());
        startup.setStage(request.getStage());
        startup.setFounderId(request.getFounderId());

        Startup saved = startupRepository.save(startup);
        
        eventPublisher.publishStartupCreated(
        	    saved.getId(),
        	    saved.getFounderId(),
        	    saved.getIndustry(),
        	    saved.getFundingGoal()
        	);
        logger.info("Startup created successfully with id: {}", saved.getId());
        return mapToResponse(saved);
    }
    public Page<StartupResponse> getAllStartups(Pageable pageable) {
        logger.info("Fetching all startups");
        return startupRepository.findAll(pageable)
                .map(this::mapToResponse);
    }


    // Get startup by ID
    @Cacheable(value = "startups", key = "#id")
    public StartupResponse getStartupById(Long id) {
        logger.info("Fetching startup with id: {}", id);
        Startup startup = startupRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Startup not found with id: {}", id);
                    return new ResourceNotFoundException("Startup not found with id: " + id);
                });
        return mapToResponse(startup);
    }

    // Update startup
    @CacheEvict(value = "startups", allEntries = true)
    public StartupResponse updateStartup(Long id, StartupRequest request) {
        logger.info("Updating startup with id: {}", id);
        Startup startup = startupRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Startup not found with id: {}", id);
                    return new ResourceNotFoundException("Startup not found with id: " + id);
                });

        startup.setName(request.getName());
        startup.setDescription(request.getDescription());
        startup.setIndustry(request.getIndustry());
        startup.setProblemStatement(request.getProblemStatement());
        startup.setSolution(request.getSolution());
        startup.setFundingGoal(request.getFundingGoal());
        startup.setStage(request.getStage());

        Startup updated = startupRepository.save(startup);
        logger.info("Startup updated successfully with id: {}", id);
        return mapToResponse(updated);
    }

    // Delete startup
    @CacheEvict(value = "startups", allEntries = true)
    public String deleteStartup(Long id) {
        logger.info("Deleting startup with id: {}", id);
        startupRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Startup not found with id: {}", id);
                    return new ResourceNotFoundException("Startup not found with id: " + id);
                });
        startupRepository.deleteById(id);
        logger.info("Startup deleted successfully with id: {}", id);
        return "Startup deleted successfully!";
    }

    // Search by industry
    public List<StartupResponse> getByIndustry(String industry) {
        logger.info("Fetching startups by industry: {}", industry);
        return startupRepository.findByIndustry(industry)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Search by stage
    public List<StartupResponse> getByStage(String stage) {
        logger.info("Fetching startups by stage: {}", stage);
        return startupRepository.findByStage(stage)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get startups by founder
    public List<StartupResponse> getByFounderId(Long founderId) {
        logger.info("Fetching startups by founderId: {}", founderId);
        return startupRepository.findByFounderId(founderId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Helper method
    private StartupResponse mapToResponse(Startup startup) {
        return new StartupResponse(
                startup.getId(),
                startup.getName(),
                startup.getDescription(),
                startup.getIndustry(),
                startup.getProblemStatement(),
                startup.getSolution(),
                startup.getFundingGoal(),
                startup.getStage(),
                startup.getFounderId()
        );
    }
}