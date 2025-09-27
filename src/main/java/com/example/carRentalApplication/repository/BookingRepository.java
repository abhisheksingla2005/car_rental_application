package com.example.carRentalApplication.repository;

import com.example.carRentalApplication.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);

    // Find bookings by status (for admin approval workflow)
    List<Booking> findByStatus(String status);

    // Method to calculate total revenue from all bookings
    @Query("SELECT COALESCE(SUM(b.totalCost), 0) FROM Booking b WHERE b.status = 'APPROVED'")
    BigDecimal calculateTotalRevenue();
}
