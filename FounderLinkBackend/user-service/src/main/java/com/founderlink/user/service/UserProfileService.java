package com.founderlink.user.service;

import com.founderlink.user.dto.UserProfileRequest;
import com.founderlink.user.dto.UserProfileResponse;
import com.founderlink.user.entity.UserProfile;
import com.founderlink.user.exception.DuplicateResourceException;
import com.founderlink.user.exception.ResourceNotFoundException;
import com.founderlink.user.repository.UserProfileRepository;
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
public class UserProfileService {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);

	@Autowired
	private UserProfileRepository userProfileRepository;

	@CacheEvict(value = "users", allEntries = true)
	public UserProfileResponse createProfile(UserProfileRequest request) {

		logger.info("Creating profile for email: {}", request.getEmail());

		if (request.getName() == null || request.getName().isEmpty()) {
			throw new RuntimeException("Name cannot be empty!");
		}

		if (request.getEmail() == null || request.getEmail().isEmpty()) {
			throw new RuntimeException("Email cannot be empty!");
		}

		// Check if profile already exists
		if (userProfileRepository.findByEmail(request.getEmail()).isPresent()) {
			logger.warn("Profile already exists for email: {}", request.getEmail());
			throw new DuplicateResourceException("Profile already exists for this email!");
		}

		UserProfile profile = new UserProfile();
		profile.setUserId(request.getUserId());
		profile.setName(request.getName());
		profile.setEmail(request.getEmail());
		profile.setBio(request.getBio());
		profile.setSkills(request.getSkills());
		profile.setExperience(request.getExperience());
		profile.setPortfolioLink(request.getPortfolioLink());
		profile.setRole(request.getRole());

		UserProfile saved = userProfileRepository.save(profile);

		logger.info("Profile created successfully for email: {}", request.getEmail());

		return mapToResponse(saved);
	}

	@Cacheable(value = "users", key = "#id")

	public UserProfileResponse getProfileById(Long id) {

		logger.info("Fetching profile with id: {}", id);

		UserProfile profile = userProfileRepository.findById(id).orElseThrow(() -> {
			logger.error("Profile not found with id: {}", id);
			return new ResourceNotFoundException("Profile not found with id: " + id);
		});

		return mapToResponse(profile);
	}

	public Page<UserProfileResponse> getAllProfiles(Pageable pageable) {
		logger.info("Fetching all profiles");
		return userProfileRepository.findAll(pageable).map(this::mapToResponse);
	}

	@CacheEvict(value = "users", allEntries = true)
	public UserProfileResponse updateProfile(Long id, UserProfileRequest request) {

		logger.info("Updating profile with id: {}", id);

		UserProfile profile = userProfileRepository.findById(id).orElseThrow(() -> {
			logger.error("Profile not found with id: {}", id);
			return new ResourceNotFoundException("Profile not found with id: " + id);
		});

		profile.setName(request.getName());
		profile.setBio(request.getBio());
		profile.setSkills(request.getSkills());
		profile.setExperience(request.getExperience());
		profile.setPortfolioLink(request.getPortfolioLink());

		UserProfile updated = userProfileRepository.save(profile);

		logger.info("Profile updated successfully for id: {}", id);

		return mapToResponse(updated);
	}

	// Helper method
	private UserProfileResponse mapToResponse(UserProfile profile) {
		return new UserProfileResponse(profile.getId(), profile.getUserId(), profile.getName(), profile.getEmail(),
				profile.getBio(), profile.getSkills(), profile.getExperience(), profile.getPortfolioLink(),
				profile.getRole());
	}
}