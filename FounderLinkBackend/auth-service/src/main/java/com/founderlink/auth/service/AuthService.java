package com.founderlink.auth.service;

import com.founderlink.auth.dto.AuthResponse;
import com.founderlink.auth.dto.LoginRequest;
import com.founderlink.auth.dto.RegisterRequest;
import com.founderlink.auth.entity.Role;
import com.founderlink.auth.entity.User;
import com.founderlink.auth.exception.DuplicateResourceException;
import com.founderlink.auth.exception.InvalidInputException;
import com.founderlink.auth.exception.ResourceNotFoundException;
import com.founderlink.auth.repository.RoleRepository;
import com.founderlink.auth.repository.UserRepository;
import com.founderlink.auth.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {

        logger.info("Registering new user with email: {}", request.getEmail());

        // Validate email
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new InvalidInputException("Email cannot be empty!");
        }

        // Validate password
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new InvalidInputException("Password must be at least 6 characters!");
        }

        // Validate name
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new InvalidInputException("Name cannot be empty!");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Registration failed - email already exists: {}", request.getEmail());
            throw new DuplicateResourceException("Email already registered!");
        }

        // Find or create the role
        String roleName = request.getRole() != null
                ? request.getRole()
                : "ROLE_FOUNDER";

        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(roleName);
                    return roleRepository.save(newRole);
                });

        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        userRepository.save(user);

        logger.info("User registered successfully with email: {}", request.getEmail());

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), roleName);

        return new AuthResponse(token, user.getEmail(), roleName, user.getId());
    }

    public AuthResponse login(LoginRequest request) {

        logger.info("Login attempt for email: {}", request.getEmail());

        // Validate email
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new InvalidInputException("Email cannot be empty!");
        }

        // Validate password
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new InvalidInputException("Password cannot be empty!");
        }

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.error("Login failed - user not found: {}", request.getEmail());
                    return new ResourceNotFoundException("User not found!");
                });

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("Login failed - invalid password for email: {}", request.getEmail());
            throw new InvalidInputException("Invalid password!");
        }

        // Get role
        String roleName = user.getRoles()
                .stream()
                .findFirst()
                .map(Role::getName)
                .orElse("ROLE_FOUNDER");

        logger.info("Login successful for email: {}", request.getEmail());

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), roleName);

        return new AuthResponse(token, user.getEmail(), roleName, user.getId());
    }
}