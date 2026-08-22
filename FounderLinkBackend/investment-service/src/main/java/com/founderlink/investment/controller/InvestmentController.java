package com.founderlink.investment.controller;

import com.founderlink.investment.dto.InvestmentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import com.founderlink.investment.dto.InvestmentResponse;
import com.founderlink.investment.service.InvestmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/investments")
public class InvestmentController {

	@Autowired
	private InvestmentService investmentService;

	// Create investment
	@PostMapping
	public ResponseEntity<InvestmentResponse> createInvestment(@RequestBody InvestmentRequest request) {
		return ResponseEntity.ok(investmentService.createInvestment(request));
	}

	@GetMapping
	public ResponseEntity<Page<InvestmentResponse>> getAllInvestments(
			@PageableDefault(size = 10, sort = "id") Pageable pageable) {
		return ResponseEntity.ok(investmentService.getAllInvestments(pageable));
	}

	// Get investments by startup
	@GetMapping("/startup/{startupId}")
	public ResponseEntity<List<InvestmentResponse>> getByStartupId(@PathVariable Long startupId) {
		return ResponseEntity.ok(investmentService.getByStartupId(startupId));
	}

	// Get investments by investor
	@GetMapping("/investor/{investorId}")
	public ResponseEntity<List<InvestmentResponse>> getByInvestorId(@PathVariable Long investorId) {
		return ResponseEntity.ok(investmentService.getByInvestorId(investorId));
	}

	// Approve investment
	@PutMapping("/{id}/approve")
	public ResponseEntity<InvestmentResponse> approveInvestment(@PathVariable Long id) {
		return ResponseEntity.ok(investmentService.approveInvestment(id));
	}

	// Reject investment
	@PutMapping("/{id}/reject")
	public ResponseEntity<InvestmentResponse> rejectInvestment(@PathVariable Long id) {
		return ResponseEntity.ok(investmentService.rejectInvestment(id));
	}
}