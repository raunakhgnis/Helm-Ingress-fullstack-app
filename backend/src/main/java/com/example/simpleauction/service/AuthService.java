package com.example.simpleauction.service;

import com.example.simpleauction.dto.AuthRequest;
import com.example.simpleauction.dto.AuthResponse;
import com.example.simpleauction.dto.MessageResponse;
import com.example.simpleauction.entity.User;
import com.example.simpleauction.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.simpleauction.security.JwtUtil;
// Note: No PasswordEncoder here for simplicity, add for real app
// import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    private final UserRepository userRepository;
    // VERY basic in-memory token store. NOT SECURE.
    // Should use JWTs and potentially a blacklist/refresh mechanism in production.
    // We keep a token blacklist for logout. In production, prefer short-lived JWTs and refresh tokens.
    final Map<String, Boolean> blacklistedTokens = new ConcurrentHashMap<>();

    // Inject PasswordEncoder if using hashing
    // private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(UserRepository userRepository, JwtUtil jwtUtil /*, PasswordEncoder passwordEncoder */) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        // this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<?> registerUser(AuthRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }
        // HASH THE PASSWORD in real app: passwordEncoder.encode(signUpRequest.getPassword())
        User user = new User(signUpRequest.getEmail(), signUpRequest.getPassword());
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    public ResponseEntity<?> loginUser(AuthRequest loginRequest) {
        Optional<User> userData = userRepository.findByEmail(loginRequest.getEmail());
        if (userData.isPresent()) {
            User user = userData.get();
            // COMPARE HASHED PASSWORDS in real app: passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())
            if (user.getPassword().equals(loginRequest.getPassword())) {
                // Generate JWT with subject set to user's email
                String token = jwtUtil.generateToken(user.getEmail());
                return ResponseEntity.ok(new AuthResponse("Login successful", token, user.getEmail()));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Error: Invalid credentials"));
    }

    // Helper to get email from the simple token map
    public String getUserEmailFromToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ") && bearerToken.length() > 7) {
            String token = bearerToken.substring(7);
            if (blacklistedTokens.getOrDefault(token, false)) {
                return null;
            }
            if (jwtUtil.validateToken(token)) {
                return jwtUtil.getSubjectFromToken(token);
            }
        }
        return null;
    }

     // Simple check for validity (token exists in map)
    public boolean isValidToken(String bearerToken) {
        return getUserEmailFromToken(bearerToken) != null;
    }

    public void logoutUser(String bearerToken) {
         if (bearerToken != null && bearerToken.startsWith("Bearer ") && bearerToken.length() > 7) {
            String token = bearerToken.substring(7);
            // Blacklist token so it cannot be used again until it expires
            blacklistedTokens.put(token, true);
        }
    }
}

