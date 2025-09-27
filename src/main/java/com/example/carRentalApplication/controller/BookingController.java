package com.example.carRentalApplication.controller;

import com.example.carRentalApplication.model.Booking;
import com.example.carRentalApplication.model.Car;
import com.example.carRentalApplication.model.User;
import com.example.carRentalApplication.repository.BookingRepository;
import com.example.carRentalApplication.repository.CarRepository;
import com.example.carRentalApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        System.out.println("Retrieved " + bookings.size() + " bookings from database");
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUser(@PathVariable Long userId) {
        List<Booking> bookings = bookingRepository.findByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createBooking(@RequestBody Map<String, Object> bookingData) {
        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("Creating booking with data: " + bookingData);

            // Extract booking data
            Long carId = Long.valueOf(bookingData.get("carId").toString());
            Long userId = Long.valueOf(bookingData.get("userId").toString());
            String startDateStr = bookingData.get("startDate").toString();
            String endDateStr = bookingData.get("endDate").toString();
            String notes = bookingData.get("notes") != null ? bookingData.get("notes").toString() : "";

            // Validate car exists and is available
            Optional<Car> carOpt = carRepository.findById(carId);
            if (!carOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Car not found!");
                return ResponseEntity.badRequest().body(response);
            }

            Car car = carOpt.get();
            if (!car.isAvailable()) {
                response.put("success", false);
                response.put("message", "Car is not available for booking!");
                return ResponseEntity.badRequest().body(response);
            }

            // Validate user exists
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "User not found!");
                return ResponseEntity.badRequest().body(response);
            }

            User user = userOpt.get();

            // Parse dates
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);

            // Validate dates
            if (startDate.isAfter(endDate)) {
                response.put("success", false);
                response.put("message", "Start date cannot be after end date!");
                return ResponseEntity.badRequest().body(response);
            }

            if (startDate.isBefore(LocalDate.now())) {
                response.put("success", false);
                response.put("message", "Start date cannot be in the past!");
                return ResponseEntity.badRequest().body(response);
            }

            // Calculate total cost
            long days = ChronoUnit.DAYS.between(startDate, endDate);
            if (days == 0) days = 1; // Minimum 1 day
            double totalCost = days * car.getPricePerDay();

            // Create booking
            Booking booking = new Booking();
            booking.setUserId(userId);
            booking.setCarId(carId);
            booking.setStartDate(startDate);
            booking.setEndDate(endDate);
            booking.setTotalCost(totalCost);
            booking.setStatus("PENDING"); // Keep as PENDING for admin approval
            booking.setNotes(notes);

            // Set additional fields for display purposes
            booking.setCustomerName(user.getFirstName() + " " + user.getLastName());
            booking.setCarBrand(car.getBrand());
            booking.setCarModel(car.getModel());
            booking.setUserName(user.getFirstName() + " " + user.getLastName());

            // Save booking to database
            System.out.println("Saving booking to database...");
            Booking savedBooking = bookingRepository.save(booking);
            System.out.println("Booking saved with ID: " + savedBooking.getId() + " with PENDING status");

            // Don't update car availability yet - wait for admin approval
            // car.setAvailable(false);
            // carRepository.save(car);

            response.put("success", true);
            response.put("message", "Booking request submitted successfully! Please wait for admin approval.");
            response.put("booking", savedBooking);
            response.put("bookingId", savedBooking.getId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error creating booking: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error creating booking: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return bookingRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateBookingStatus(@PathVariable Long id, @RequestBody Map<String, String> statusUpdate) {
        Map<String, Object> response = new HashMap<>();

        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Booking not found!");
            return ResponseEntity.badRequest().body(response);
        }

        Booking booking = bookingOpt.get();
        String newStatus = statusUpdate.get("status");

        // Update booking status
        booking.setStatus(newStatus);
        bookingRepository.save(booking);

        // Handle car availability based on status changes
        Optional<Car> carOpt = carRepository.findById(booking.getCarId());
        if (carOpt.isPresent()) {
            Car car = carOpt.get();

            // If booking is approved/confirmed, make car unavailable
            if ("APPROVED".equals(newStatus) || "CONFIRMED".equals(newStatus)) {
                car.setAvailable(false);
                carRepository.save(car);
                System.out.println("Car ID " + car.getId() + " set to unavailable due to booking approval");
            }
            // If booking is cancelled, rejected, or completed, make car available
            else if ("CANCELLED".equals(newStatus) || "REJECTED".equals(newStatus) || "COMPLETED".equals(newStatus)) {
                car.setAvailable(true);
                carRepository.save(car);
                System.out.println("Car ID " + car.getId() + " set to available due to booking " + newStatus.toLowerCase());
            }
        }

        response.put("success", true);
        response.put("message", "Booking status updated successfully!");
        response.put("booking", booking);
        return ResponseEntity.ok(response);
    }

    // User can update their booking (dates, notes, etc.)
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBooking(@PathVariable Long id, @RequestBody Map<String, Object> updateData) {
        Map<String, Object> response = new HashMap<>();
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            // Only allow update if booking is not cancelled or declined
            if ("CANCELLED".equals(booking.getStatus()) || "DECLINED".equals(booking.getStatus())) {
                response.put("success", false);
                response.put("message", "Cannot update a cancelled or declined booking.");
                return ResponseEntity.badRequest().body(response);
            }
            if (updateData.containsKey("startDate")) {
                booking.setStartDate(LocalDate.parse(updateData.get("startDate").toString()));
            }
            if (updateData.containsKey("endDate")) {
                booking.setEndDate(LocalDate.parse(updateData.get("endDate").toString()));
            }
            if (updateData.containsKey("notes")) {
                booking.setNotes(updateData.get("notes").toString());
            }
            bookingRepository.save(booking);
            response.put("success", true);
            response.put("message", "Booking updated successfully!");
            response.put("booking", booking);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Booking not found!");
            return ResponseEntity.notFound().build();
        }
    }

    // Admin: Approve booking
    @PutMapping("/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveBooking(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();

            // Check if booking is in pending status
            if (!"PENDING".equals(booking.getStatus())) {
                response.put("success", false);
                response.put("message", "Only pending bookings can be approved!");
                return ResponseEntity.badRequest().body(response);
            }

            // Update booking status to approved
            booking.setStatus("APPROVED");
            bookingRepository.save(booking);

            // Make car unavailable
            Optional<Car> carOpt = carRepository.findById(booking.getCarId());
            if (carOpt.isPresent()) {
                Car car = carOpt.get();
                car.setAvailable(false);
                carRepository.save(car);
                System.out.println("Car ID " + car.getId() + " set to unavailable due to booking approval");
            }

            response.put("success", true);
            response.put("message", "Booking approved successfully!");
            response.put("booking", booking);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Booking not found!");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Admin: Reject booking
    @PutMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectBooking(@PathVariable Long id, @RequestBody(required = false) Map<String, String> rejectionData) {
        Map<String, Object> response = new HashMap<>();

        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();

            // Check if booking is in pending status
            if (!"PENDING".equals(booking.getStatus())) {
                response.put("success", false);
                response.put("message", "Only pending bookings can be rejected!");
                return ResponseEntity.badRequest().body(response);
            }

            // Update booking status to rejected
            booking.setStatus("REJECTED");

            // Add rejection reason if provided
            if (rejectionData != null && rejectionData.containsKey("reason")) {
                String existingNotes = booking.getNotes() != null ? booking.getNotes() : "";
                booking.setNotes(existingNotes + "\nRejection Reason: " + rejectionData.get("reason"));
            }

            bookingRepository.save(booking);

            response.put("success", true);
            response.put("message", "Booking rejected successfully!");
            response.put("booking", booking);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Booking not found!");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Get pending bookings for admin review
    @GetMapping("/pending")
    public ResponseEntity<List<Booking>> getPendingBookings() {
        List<Booking> pendingBookings = bookingRepository.findByStatus("PENDING");
        System.out.println("Retrieved " + pendingBookings.size() + " pending bookings");
        return ResponseEntity.ok(pendingBookings);
    }
}
