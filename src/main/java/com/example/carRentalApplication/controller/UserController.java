package com.example.carRentalApplication.controller;

import com.example.carRentalApplication.model.User;
import com.example.carRentalApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();

        try {
            // Basic validation
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                response.put("message", "Email is required!");
                return ResponseEntity.badRequest().body(response);
            }

            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                response.put("message", "Password is required!");
                return ResponseEntity.badRequest().body(response);
            }

            if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
                response.put("message", "First Name is required!");
                return ResponseEntity.badRequest().body(response);
            }

            if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
                response.put("message", "Last Name is required!");
                return ResponseEntity.badRequest().body(response);
            }

            // Set username as email if not provided
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                user.setUsername(user.getEmail());
            }

            // Check if email already exists
            if (userRepository.existsByEmail(user.getEmail())) {
                response.put("message", "Email already exists!");
                return ResponseEntity.badRequest().body(response);
            }

            // Check if username already exists
            if (userRepository.existsByUsername(user.getUsername())) {
                response.put("message", "Username already exists!");
                return ResponseEntity.badRequest().body(response);
            }

            // Set default role if not provided
            if (user.getRole() == null) {
                user.setRole(User.Role.USER);
            }

            // Encode password
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Save user to database
            User savedUser = userRepository.save(user);

            System.out.println("User saved to database: " + savedUser.getEmail());

            response.put("message", "User registered successfully!");
            response.put("userId", savedUser.getId().toString());
            response.put("redirect", "/login.html?registered=true");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error saving user: " + e.getMessage());
            e.printStackTrace();
            response.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> loginData) {
        Map<String, Object> response = new HashMap<>();

        String username = loginData.get("username");
        String password = loginData.get("password");

        if (username == null || username.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Username is required!");
            return ResponseEntity.badRequest().body(response);
        }

        if (password == null || password.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Password is required!");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            User user = userOpt.get();
            response.put("success", true);
            response.put("message", "Login successful!");
            response.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole().toString(),
                "firstName", user.getFirstName() != null ? user.getFirstName() : "",
                "lastName", user.getLastName() != null ? user.getLastName() : "",
                "phoneNumber", user.getPhoneNumber() != null ? user.getPhoneNumber() : ""
            ));
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Invalid username or password!");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        // Remove passwords from response for security
        users.forEach(user -> user.setPassword(""));
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(""); // Remove password from response
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        Map<String, String> response = new HashMap<>();

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Update only non-null fields
            if (updatedUser.getEmail() != null && !updatedUser.getEmail().trim().isEmpty()) {
                // Check if email is already taken by another user
                if (userRepository.existsByEmail(updatedUser.getEmail()) &&
                    !user.getEmail().equals(updatedUser.getEmail())) {
                    response.put("message", "Email already exists!");
                    return ResponseEntity.badRequest().body(response);
                }
                user.setEmail(updatedUser.getEmail());
            }

            if (updatedUser.getFirstName() != null) {
                user.setFirstName(updatedUser.getFirstName());
            }

            if (updatedUser.getLastName() != null) {
                user.setLastName(updatedUser.getLastName());
            }

            if (updatedUser.getPhoneNumber() != null) {
                user.setPhoneNumber(updatedUser.getPhoneNumber());
            }

            if (updatedUser.getRole() != null) {
                user.setRole(updatedUser.getRole());
            }

            // Update password if provided
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().trim().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            userRepository.save(user);
            response.put("message", "User updated successfully!");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "User not found!");
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<Map<String, String>> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> roleData) {
        Map<String, String> response = new HashMap<>();

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String newRole = roleData.get("role");

            if (newRole != null && (newRole.equals("USER") || newRole.equals("ADMIN"))) {
                user.setRole(newRole.equals("ADMIN") ? User.Role.ADMIN : User.Role.USER);
                userRepository.save(user);
                response.put("message", "User role updated successfully!");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Invalid role specified!");
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            response.put("message", "User not found!");
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();

        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            response.put("message", "User deleted successfully!");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "User not found!");
            return ResponseEntity.notFound().build();
        }
    }
}
