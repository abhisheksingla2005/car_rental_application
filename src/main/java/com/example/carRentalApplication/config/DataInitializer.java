package com.example.carRentalApplication.config;

import com.example.carRentalApplication.model.Car;
import com.example.carRentalApplication.model.User;
import com.example.carRentalApplication.repository.CarRepository;
import com.example.carRentalApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Initializing database with sample data...");

        try {
            // Create admin user if not exists
            if (!userRepository.existsByEmail("admin@gmail.com")) {
                User admin = new User();
                admin.setUsername("admin@gmail.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@gmail.com");
                admin.setRole(User.Role.ADMIN);
                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setPhoneNumber("+91-99999-00001");
                userRepository.save(admin);
                System.out.println("✅ Admin user created: admin@gmail.com / admin123");
            }

            // Create demo regular user if not exists
            if (!userRepository.existsByEmail("user@example.com")) {
                User user = new User();
                user.setUsername("user@example.com");
                user.setPassword(passwordEncoder.encode("user123"));
                user.setEmail("user@example.com");
                user.setRole(User.Role.USER);
                user.setFirstName("John");
                user.setLastName("Doe");
                user.setPhoneNumber("+91-99999-00002");
                userRepository.save(user);
                System.out.println("✅ Demo user created: user@example.com / user123");
            }

            // Initialize cars if none exist
            if (carRepository.count() == 0) {
                System.out.println("Adding sample cars to database...");

                // Car 1 - Maruti Suzuki Swift
                Car car1 = new Car();
                car1.setModel("Swift VDi");
                car1.setBrand("Maruti Suzuki");
                car1.setYear(2023);
                car1.setPricePerDay(2500.0);
                car1.setType("Hatchback");
                car1.setSeats(5);
                car1.setFuelType("Diesel");
                car1.setDescription("Compact and fuel-efficient hatchback perfect for city driving");
                car1.setAvailable(true);
                carRepository.save(car1);

                // Car 2 - Honda City
                Car car2 = new Car();
                car2.setModel("City ZX CVT");
                car2.setBrand("Honda");
                car2.setYear(2023);
                car2.setPricePerDay(3500.0);
                car2.setType("Sedan");
                car2.setSeats(5);
                car2.setFuelType("Petrol");
                car2.setDescription("Comfortable sedan with premium features and smooth ride");
                car2.setAvailable(true);
                carRepository.save(car2);

                // Car 3 - Toyota Innova Crysta
                Car car3 = new Car();
                car3.setModel("Innova Crysta GX");
                car3.setBrand("Toyota");
                car3.setYear(2023);
                car3.setPricePerDay(4500.0);
                car3.setType("MPV");
                car3.setSeats(7);
                car3.setFuelType("Diesel");
                car3.setDescription("Spacious family vehicle ideal for long trips");
                car3.setAvailable(true);
                carRepository.save(car3);

                // Car 4 - Hyundai Creta
                Car car4 = new Car();
                car4.setModel("Creta SX");
                car4.setBrand("Hyundai");
                car4.setYear(2023);
                car4.setPricePerDay(4000.0);
                car4.setType("SUV");
                car4.setSeats(5);
                car4.setFuelType("Petrol");
                car4.setDescription("Stylish SUV with advanced safety features");
                car4.setAvailable(true);
                carRepository.save(car4);

                // Car 5 - Mahindra XUV700
                Car car5 = new Car();
                car5.setModel("XUV700 AX7");
                car5.setBrand("Mahindra");
                car5.setYear(2023);
                car5.setPricePerDay(5000.0);
                car5.setType("SUV");
                car5.setSeats(7);
                car5.setFuelType("Diesel");
                car5.setDescription("Premium SUV with cutting-edge technology");
                car5.setAvailable(true);
                carRepository.save(car5);

                System.out.println("✅ Sample cars added to database successfully!");
            }

            System.out.println("✅ Database initialization completed successfully!");
            System.out.println("📊 Users in database: " + userRepository.count());
            System.out.println("🚗 Cars in database: " + carRepository.count());

        } catch (Exception e) {
            System.err.println("❌ Error during database initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
