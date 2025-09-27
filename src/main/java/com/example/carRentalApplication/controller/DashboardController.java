package com.example.carRentalApplication.controller;

import com.example.carRentalApplication.repository.BookingRepository;
import com.example.carRentalApplication.repository.CarRepository;
import com.example.carRentalApplication.repository.UserRepository;
import com.example.carRentalApplication.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Get total counts
        long totalCars = carRepository.count();
        long totalBookings = bookingRepository.count();
        long totalUsers = userRepository.countByRole(User.Role.USER); // Only count regular users, not admin

        // Calculate total revenue (assuming confirmed bookings)
        BigDecimal totalRevenue = bookingRepository.calculateTotalRevenue();
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        stats.put("totalCars", totalCars);
        stats.put("totalBookings", totalBookings);
        stats.put("totalUsers", totalUsers);
        stats.put("totalRevenue", totalRevenue);

        System.out.println("Dashboard stats - Cars: " + totalCars + ", Bookings: " + totalBookings + ", Users: " + totalUsers + ", Revenue: " + totalRevenue);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findByRole(User.Role.USER);
        return ResponseEntity.ok(users);
    }
}
