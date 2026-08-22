package com.founderlink.startup;
import com.founderlink.startup.dto.StartupRequest;
import com.founderlink.startup.dto.StartupResponse;
import com.founderlink.startup.entity.Startup;
import com.founderlink.startup.event.EventPublisher;
import com.founderlink.startup.exception.ResourceNotFoundException;
import com.founderlink.startup.repository.StartupRepository;
import com.founderlink.startup.service.StartupService;
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
class StartupServiceTest {

    @Mock private StartupRepository startupRepository;
    @Mock private EventPublisher eventPublisher;
    @InjectMocks private StartupService startupService;

    private Startup startup;
    private StartupRequest request;

    @BeforeEach
    void setUp() {
        startup = new Startup();
        startup.setId(1L);
        startup.setName("TechVenture");
        startup.setStage("IDEA");
        startup.setFundingGoal(100000.0);
        startup.setIndustry("FinTech");
        startup.setFounderId(1L);

        request = new StartupRequest();
        request.setName("TechVenture");
        request.setStage("IDEA");
        request.setFundingGoal(100000.0);
        request.setIndustry("FinTech");
        request.setFounderId(1L);
    }

    // ── CREATE ──────────────────────────────────────────────

    @Test
    void createStartup_success() {
        when(startupRepository.save(any())).thenReturn(startup);
        doNothing().when(eventPublisher)
                .publishStartupCreated(any(), any(), any(), any());

        StartupResponse response = startupService.createStartup(request);

        assertNotNull(response);
        assertEquals("TechVenture", response.getName());
        verify(startupRepository, times(1)).save(any());
        verify(eventPublisher, times(1))
                .publishStartupCreated(any(), any(), any(), any());
    }

    @Test
    void createStartup_emptyName_throwsRuntimeException() {
        request.setName("");
        assertThrows(RuntimeException.class,
                () -> startupService.createStartup(request));
        verify(startupRepository, never()).save(any());
    }

    @Test
    void createStartup_nullName_throwsRuntimeException() {
        request.setName(null);
        assertThrows(RuntimeException.class,
                () -> startupService.createStartup(request));
        verify(startupRepository, never()).save(any());
    }

    @Test
    void createStartup_invalidFundingGoal_throwsRuntimeException() {
        request.setFundingGoal(-1.0);
        assertThrows(RuntimeException.class,
                () -> startupService.createStartup(request));
    }

    @Test
    void createStartup_zeroFundingGoal_throwsRuntimeException() {
        request.setFundingGoal(0.0);
        assertThrows(RuntimeException.class,
                () -> startupService.createStartup(request));
    }

    @Test
    void createStartup_emptyStage_throwsRuntimeException() {
        request.setStage("");
        assertThrows(RuntimeException.class,
                () -> startupService.createStartup(request));
    }

    // ── GET BY ID ───────────────────────────────────────────

    @Test
    void getStartupById_success() {
        when(startupRepository.findById(1L)).thenReturn(Optional.of(startup));
        StartupResponse response = startupService.getStartupById(1L);
        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getStartupById_notFound_throwsResourceNotFoundException() {
        when(startupRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> startupService.getStartupById(99L));
    }

    // ── GET ALL (PAGINATED) ─────────────────────────────────

    @Test
    void getAllStartups_paginated() {
        Pageable pageable = PageRequest.of(0, 10);
        when(startupRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(startup)));

        Page<StartupResponse> page = startupService.getAllStartups(pageable);

        assertEquals(1, page.getTotalElements());
        assertEquals("TechVenture", page.getContent().get(0).getName());
    }

    @Test
    void getAllStartups_emptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(startupRepository.findAll(pageable)).thenReturn(Page.empty());

        Page<StartupResponse> page = startupService.getAllStartups(pageable);

        assertEquals(0, page.getTotalElements());
        assertTrue(page.getContent().isEmpty());
    }

    // ── GET BY INDUSTRY ─────────────────────────────────────

    @Test
    void getByIndustry_returnsList() {
        when(startupRepository.findByIndustry("FinTech"))
                .thenReturn(List.of(startup));

        List<StartupResponse> result = startupService.getByIndustry("FinTech");

        assertEquals(1, result.size());
        assertEquals("FinTech", result.get(0).getIndustry());
    }

    @Test
    void getByIndustry_emptyList() {
        when(startupRepository.findByIndustry("BioTech"))
                .thenReturn(Collections.emptyList());

        List<StartupResponse> result = startupService.getByIndustry("BioTech");

        assertTrue(result.isEmpty());
    }

    // ── GET BY STAGE ────────────────────────────────────────

    @Test
    void getByStage_returnsList() {
        when(startupRepository.findByStage("IDEA"))
                .thenReturn(List.of(startup));

        List<StartupResponse> result = startupService.getByStage("IDEA");

        assertEquals(1, result.size());
        assertEquals("IDEA", result.get(0).getStage());
    }

    // ── GET BY FOUNDER ──────────────────────────────────────

    @Test
    void getByFounderId_returnsList() {
        when(startupRepository.findByFounderId(1L))
                .thenReturn(List.of(startup));

        List<StartupResponse> result = startupService.getByFounderId(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getFounderId());
    }

    @Test
    void getByFounderId_emptyList() {
        when(startupRepository.findByFounderId(99L))
                .thenReturn(Collections.emptyList());

        List<StartupResponse> result = startupService.getByFounderId(99L);

        assertTrue(result.isEmpty());
    }

    // ── UPDATE ──────────────────────────────────────────────

    @Test
    void updateStartup_success() {
        when(startupRepository.findById(1L)).thenReturn(Optional.of(startup));
        when(startupRepository.save(any())).thenReturn(startup);

        StartupResponse response = startupService.updateStartup(1L, request);

        assertNotNull(response);
        verify(startupRepository, times(1)).save(any());
    }

    @Test
    void updateStartup_notFound_throwsResourceNotFoundException() {
        when(startupRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> startupService.updateStartup(99L, request));
    }

    // ── DELETE ──────────────────────────────────────────────

    @Test
    void deleteStartup_success() {
        when(startupRepository.findById(1L)).thenReturn(Optional.of(startup));
        doNothing().when(startupRepository).deleteById(1L);

        String result = startupService.deleteStartup(1L);

        assertEquals("Startup deleted successfully!", result);
        verify(startupRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteStartup_notFound_throwsResourceNotFoundException() {
        when(startupRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> startupService.deleteStartup(99L));
    }
}
