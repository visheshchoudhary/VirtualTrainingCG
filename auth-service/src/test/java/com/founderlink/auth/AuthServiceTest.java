package com.founderlink.auth;

import com.founderlink.auth.dto.AuthResponse;
import com.founderlink.auth.dto.LoginRequest;
import com.founderlink.auth.dto.RegisterRequest;
import com.founderlink.auth.entity.Role;
import com.founderlink.auth.entity.User;
import com.founderlink.auth.exception.DuplicateResourceException;
import com.founderlink.auth.exception.InvalidInputException;
import com.founderlink.auth.repository.RoleRepository;
import com.founderlink.auth.repository.UserRepository;
import com.founderlink.auth.security.JwtUtil;
import com.founderlink.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @InjectMocks private AuthService authService;

    private User user;
    private Role role;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName("ROLE_FOUNDER");

        user = new User();
        user.setId(1L);
        user.setName("Bob");
        user.setEmail("bob@test.com");
        user.setPassword("encodedPassword");
        user.setRoles(Set.of(role));

        registerRequest = new RegisterRequest();
        registerRequest.setName("Bob");
        registerRequest.setEmail("bob@test.com");
        registerRequest.setPassword("password123");
        registerRequest.setRole("ROLE_FOUNDER");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("bob@test.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void register_success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(user);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("bob@test.com", response.getEmail());
    }

    @Test
    void register_duplicateEmail_throwsDuplicateResourceException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_emptyEmail_throwsInvalidInputException() {
        registerRequest.setEmail("");
        assertThrows(InvalidInputException.class,
                () -> authService.register(registerRequest));
    }

    @Test
    void register_shortPassword_throwsInvalidInputException() {
        registerRequest.setPassword("123");
        assertThrows(InvalidInputException.class,
                () -> authService.register(registerRequest));
    }

    @Test
    void login_success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void login_wrongPassword_throwsInvalidInputException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(InvalidInputException.class,
                () -> authService.login(loginRequest));
    }

    @Test
    void login_userNotFound_throwsInvalidInputException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(Exception.class,
                () -> authService.login(loginRequest));
    }
}
