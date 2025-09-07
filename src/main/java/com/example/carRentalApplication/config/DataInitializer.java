package com.example.carRentalApplication.config;

import com.example.carRentalApplication.model.Car;
import com.example.carRentalApplication.model.User;
import com.example.carRentalApplication.repository.CarRepository;
import com.example.carRentalApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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

        // Add sample cars if none exist
        if (carRepository.count() == 0) {
            Car car1 = new Car("Toyota", "Camry", 2023, 50.0);
            Car car2 = new Car("Honda", "Civic", 2022, 45.0);
            Car car3 = new Car("BMW", "X5", 2023, 120.0);
            Car car4 = new Car("Mercedes", "C-Class", 2022, 100.0);
            Car car5 = new Car("Audi", "A4", 2023, 95.0);
            Car car6 = new Car("Ford", "Mustang", 2023, 85.0);
            Car car7 = new Car("Chevrolet", "Camaro", 2022, 80.0);
            Car car8 = new Car("Nissan", "Altima", 2023, 55.0);
            Car car9 = new Car("Hyundai", "Sonata", 2022, 50.0);
            Car car10 = new Car("Kia", "Optima", 2023, 60.0);

            carRepository.save(car1);
            carRepository.save(car2);
            carRepository.save(car3);
            carRepository.save(car4);
            carRepository.save(car5);
            carRepository.save(car6);
            carRepository.save(car7);
            carRepository.save(car8);
            carRepository.save(car9);
            carRepository.save(car10);

            System.out.println("Sample cars added to database");
        }

        // Add admin user if none exists
        if (userRepository.count() == 0) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@carrental.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setRole("ADMIN");

            User testUser = new User();
            testUser.setUsername("testuser");
            testUser.setEmail("test@example.com");
            testUser.setPassword(passwordEncoder.encode("password"));
            testUser.setRole("USER");

            userRepository.save(adminUser);
            userRepository.save(testUser);

            System.out.println("Sample users added to database");
            System.out.println("Admin user: admin/admin123");
            System.out.println("Test user: testuser/password");
        }

        System.out.println("Database initialization completed");
    }
}
