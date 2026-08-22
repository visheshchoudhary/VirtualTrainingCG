package com.founderlink.user;

import com.founderlink.user.dto.UserProfileRequest;
import com.founderlink.user.dto.UserProfileResponse;
import com.founderlink.user.entity.UserProfile;
import com.founderlink.user.exception.DuplicateResourceException;
import com.founderlink.user.exception.ResourceNotFoundException;
import com.founderlink.user.repository.UserProfileRepository;
import com.founderlink.user.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

	@Mock
	private UserProfileRepository userProfileRepository;

	@InjectMocks
	private UserProfileService userProfileService;

	private UserProfile profile;
	private UserProfileRequest request;

	@BeforeEach
	void setUp() {
		profile = new UserProfile();
		profile.setId(1L);
		profile.setUserId(10L);
		profile.setName("Alice");
		profile.setEmail("alice@test.com");
		profile.setBio("Founder & CEO");
		profile.setSkills("Java, Spring");
		profile.setExperience("5 years");
		profile.setPortfolioLink("https://alice.dev");
		profile.setRole("ROLE_FOUNDER");

		request = new UserProfileRequest();
		request.setUserId(10L);
		request.setName("Alice");
		request.setEmail("alice@test.com");
		request.setBio("Founder & CEO");
		request.setSkills("Java, Spring");
		request.setExperience("5 years");
		request.setPortfolioLink("https://alice.dev");
		request.setRole("ROLE_FOUNDER");
	}

	// ── CREATE ──────────────────────────────────────────────

	@Test
	void createProfile_success() {
		when(userProfileRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
		when(userProfileRepository.save(any())).thenReturn(profile);

		UserProfileResponse response = userProfileService.createProfile(request);

		assertNotNull(response);
		assertEquals("Alice", response.getName());
		assertEquals("alice@test.com", response.getEmail());
		assertEquals("ROLE_FOUNDER", response.getRole());
		verify(userProfileRepository, times(1)).save(any());
	}

	@Test
	void createProfile_duplicateEmail_throwsDuplicateResourceException() {
		when(userProfileRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(profile));

		assertThrows(DuplicateResourceException.class, () -> userProfileService.createProfile(request));
		verify(userProfileRepository, never()).save(any());
	}

	@Test
	void createProfile_emptyName_throwsRuntimeException() {
		request.setName("");
		assertThrows(RuntimeException.class,
				() -> userProfileService.createProfile(request));
		verify(userProfileRepository, never()).save(any());
	}

	@Test
	void createProfile_nullName_throwsRuntimeException() {
		request.setName(null);
		assertThrows(RuntimeException.class,
				() -> userProfileService.createProfile(request));
		verify(userProfileRepository, never()).save(any());
	}

	@Test
	void createProfile_emptyEmail_throwsRuntimeException() {
		request.setEmail("");
		assertThrows(RuntimeException.class,
				() -> userProfileService.createProfile(request));
		verify(userProfileRepository, never()).save(any());
	}

	@Test
	void createProfile_nullEmail_throwsRuntimeException() {
		request.setEmail(null);
		assertThrows(RuntimeException.class,
				() -> userProfileService.createProfile(request));
		verify(userProfileRepository, never()).save(any());
	}

	// ── GET BY ID ───────────────────────────────────────────

	@Test
	void getProfileById_success() {
		when(userProfileRepository.findById(1L)).thenReturn(Optional.of(profile));

		UserProfileResponse response = userProfileService.getProfileById(1L);

		assertNotNull(response);
		assertEquals(1L, response.getId());
		assertEquals("Alice", response.getName());
	}

	@Test
	void getProfileById_notFound_throwsResourceNotFoundException() {
		when(userProfileRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> userProfileService.getProfileById(99L));
	}

	// ── GET ALL (PAGINATED) ─────────────────────────────────

	@Test
	void getAllProfiles_returnsPaginatedResult() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<UserProfile> page = new PageImpl<>(List.of(profile));
		when(userProfileRepository.findAll(pageable)).thenReturn(page);

		Page<UserProfileResponse> result = userProfileService.getAllProfiles(pageable);

		assertEquals(1, result.getTotalElements());
		assertEquals("Alice", result.getContent().get(0).getName());
	}

	@Test
	void getAllProfiles_emptyPage() {
		Pageable pageable = PageRequest.of(0, 10);
		when(userProfileRepository.findAll(pageable)).thenReturn(Page.empty());

		Page<UserProfileResponse> result = userProfileService.getAllProfiles(pageable);

		assertEquals(0, result.getTotalElements());
		assertTrue(result.getContent().isEmpty());
	}

	// ── UPDATE ──────────────────────────────────────────────

	@Test
	void updateProfile_success() {
		when(userProfileRepository.findById(1L)).thenReturn(Optional.of(profile));
		when(userProfileRepository.save(any())).thenReturn(profile);

		UserProfileResponse response = userProfileService.updateProfile(1L, request);

		assertNotNull(response);
		verify(userProfileRepository, times(1)).save(any());
	}

	@Test
	void updateProfile_notFound_throwsResourceNotFoundException() {
		when(userProfileRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> userProfileService.updateProfile(99L, request));
	}

	@Test
	void updateProfile_fieldsAreUpdated() {
		when(userProfileRepository.findById(1L)).thenReturn(Optional.of(profile));

		UserProfileRequest updateReq = new UserProfileRequest();
		updateReq.setName("Alice Updated");
		updateReq.setBio("CTO now");
		updateReq.setSkills("Java, Spring, K8s");
		updateReq.setExperience("7 years");
		updateReq.setPortfolioLink("https://alice-new.dev");

		// The save should return profile with updated fields
		when(userProfileRepository.save(any())).thenAnswer(invocation -> {
			UserProfile saved = invocation.getArgument(0);
			return saved;
		});

		UserProfileResponse response = userProfileService.updateProfile(1L, updateReq);

		assertNotNull(response);
		assertEquals("Alice Updated", response.getName());
		assertEquals("CTO now", response.getBio());
		assertEquals("Java, Spring, K8s", response.getSkills());
	}

	// ── RESPONSE MAPPING ────────────────────────────────────

	@Test
	void createProfile_responseContainsAllFields() {
		when(userProfileRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
		when(userProfileRepository.save(any())).thenReturn(profile);

		UserProfileResponse response = userProfileService.createProfile(request);

		assertEquals(1L, response.getId());
		assertEquals(10L, response.getUserId());
		assertEquals("Alice", response.getName());
		assertEquals("alice@test.com", response.getEmail());
		assertEquals("Founder & CEO", response.getBio());
		assertEquals("Java, Spring", response.getSkills());
		assertEquals("5 years", response.getExperience());
		assertEquals("https://alice.dev", response.getPortfolioLink());
		assertEquals("ROLE_FOUNDER", response.getRole());
	}
}
