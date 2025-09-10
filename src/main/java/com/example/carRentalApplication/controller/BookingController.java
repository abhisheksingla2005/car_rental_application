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
            booking.setStatus("CONFIRMED");
            booking.setNotes(notes);

            // Set additional fields for display purposes
            booking.setCustomerName(user.getUsername());
            booking.setCarBrand(car.getBrand());
            booking.setCarModel(car.getModel());
            booking.setUserName(user.getUsername());

            // Save booking
            Booking savedBooking = bookingRepository.save(booking);

            // Update car availability
            car.setAvailable(false);
            carRepository.save(car);

            response.put("success", true);
            response.put("message", "Booking created successfully!");
            response.put("booking", savedBooking);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
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
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            String newStatus = statusUpdate.get("status");

            // Update booking status
            booking.setStatus(newStatus);
            bookingRepository.save(booking);

            // If booking is cancelled, make car available again
            if ("CANCELLED".equals(newStatus)) {
                Optional<Car> carOpt = carRepository.findById(booking.getCarId());
                if (carOpt.isPresent()) {
                    Car car = carOpt.get();
                    car.setAvailable(true);
                    carRepository.save(car);
                }
            }

            response.put("success", true);
            response.put("message", "Booking status updated successfully!");
            response.put("booking", booking);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Booking not found!");
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBooking(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();

            // If booking was confirmed, make car available again
            if (!"CANCELLED".equals(booking.getStatus())) {
                Optional<Car> carOpt = carRepository.findById(booking.getCarId());
                if (carOpt.isPresent()) {
                    Car car = carOpt.get();
                    car.setAvailable(true);
                    carRepository.save(car);
                }
            }

            bookingRepository.deleteById(id);
            response.put("message", "Booking deleted successfully!");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Booking not found!");
            return ResponseEntity.notFound().build();
        }
    }
}
