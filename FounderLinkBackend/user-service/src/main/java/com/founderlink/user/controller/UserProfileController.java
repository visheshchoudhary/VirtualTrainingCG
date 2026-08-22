package com.founderlink.user.controller;

import com.founderlink.user.dto.UserProfileRequest;
import com.founderlink.user.dto.UserProfileResponse;
import com.founderlink.user.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.List;
@RestController
@RequestMapping("/users")
public class UserProfileController {

	@Autowired
	private UserProfileService userProfileService;

	@PostMapping
	public ResponseEntity<UserProfileResponse> createProfile(@RequestBody UserProfileRequest request) {
		return ResponseEntity.ok(userProfileService.createProfile(request));
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserProfileResponse> getProfileById(@PathVariable Long id) {
		return ResponseEntity.ok(userProfileService.getProfileById(id));
	}

	@GetMapping
	public ResponseEntity<Page<UserProfileResponse>> getAllProfiles(
			@PageableDefault(size = 10, sort = "name") Pageable pageable) {
		return ResponseEntity.ok(userProfileService.getAllProfiles(pageable));
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserProfileResponse> updateProfile(@PathVariable Long id,
			@RequestBody UserProfileRequest request) {
		return ResponseEntity.ok(userProfileService.updateProfile(id, request));
	}
}