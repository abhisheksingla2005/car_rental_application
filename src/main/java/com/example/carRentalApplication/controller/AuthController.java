package com.example.carRentalApplication.controller;

import com.example.carRentalApplication.model.User;
import com.example.carRentalApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getName().equals("anonymousUser")) {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }

        Optional<User> userOpt = userRepository.findByEmail(authentication.getName());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("authenticated", true);
            response.put("id", user.getId()); // Add user ID
            response.put("email", user.getEmail());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("role", user.getRole().toString());
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok(Map.of("authenticated", false));
    }

    @PostMapping("/check-access")
    public ResponseEntity<Map<String, Object>> checkAccess(@RequestParam String requiredRole) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getName().equals("anonymousUser")) {
            response.put("hasAccess", false);
            response.put("message", "Not authenticated");
            return ResponseEntity.ok(response);
        }

        Optional<User> userOpt = userRepository.findByEmail(authentication.getName());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean hasAccess = user.getRole().toString().equals(requiredRole);
            response.put("hasAccess", hasAccess);
            response.put("role", user.getRole().toString());
            return ResponseEntity.ok(response);
        }

        response.put("hasAccess", false);
        response.put("message", "User not found");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
        return ResponseEntity.ok(Map.of("success", true, "message", "Logged out successfully"));
    }

    @GetMapping("/check-access")
    public ResponseEntity<Map<String, Object>> checkAccessGet(@RequestParam String requiredRole) {
        return checkAccess(requiredRole);
    }
}