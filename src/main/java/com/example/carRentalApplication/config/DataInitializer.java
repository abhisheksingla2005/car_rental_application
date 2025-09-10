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

        // Create admin user if not exists
        if (!userRepository.existsByEmail("admin@gmail.com")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@gmail.com");
            admin.setRole(User.Role.ADMIN);
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setPhoneNumber("+91-99999-00001");
            userRepository.save(admin);
            System.out.println("Admin user created: admin@gmail.com / admin123");
        }

        // Create demo regular user if not exists
        if (!userRepository.existsByEmail("user@example.com")) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user@example.com");
            user.setRole(User.Role.USER);
            user.setFirstName("John");
            user.setLastName("Doe");
            user.setPhoneNumber("+91-99999-00002");
            userRepository.save(user);
            System.out.println("Demo user created: user@example.com / user123");
        }

        System.out.println("Users initialization completed!");

        // Add sample cars if none exist
        if (carRepository.count() == 0) {
            System.out.println("Adding sample cars...");

            Car car1 = new Car();
            car1.setBrand("Toyota");
            car1.setModel("Camry");
            car1.setYear(2023);
            car1.setPricePerDay(3500.0); // ₹3,500 per day
            car1.setType("Sedan");
            car1.setSeats(5);
            car1.setFuelType("Petrol");
            car1.setDescription("Reliable and fuel-efficient sedan perfect for city driving");
            car1.setAvailable(true);
            carRepository.save(car1);

            Car car2 = new Car();
            car2.setBrand("Honda");
            car2.setModel("Civic");
            car2.setYear(2022);
            car2.setPricePerDay(3000.0); // ₹3,000 per day
            car2.setType("Sedan");
            car2.setSeats(5);
            car2.setFuelType("Petrol");
            car2.setDescription("Compact sedan with excellent fuel economy");
            car2.setAvailable(true);
            carRepository.save(car2);

            Car car3 = new Car();
            car3.setBrand("BMW");
            car3.setModel("X5");
            car3.setYear(2023);
            car3.setPricePerDay(8500.0); // ₹8,500 per day
            car3.setType("SUV");
            car3.setSeats(7);
            car3.setFuelType("Petrol");
            car3.setDescription("Luxury SUV with premium features and spacious interior");
            car3.setAvailable(true);
            carRepository.save(car3);

            Car car4 = new Car();
            car4.setBrand("Hyundai");
            car4.setModel("Creta");
            car4.setYear(2023);
            car4.setPricePerDay(4000.0); // ₹4,000 per day
            car4.setType("SUV");
            car4.setSeats(5);
            car4.setFuelType("Diesel");
            car4.setDescription("Popular compact SUV with modern features");
            car4.setAvailable(true);
            carRepository.save(car4);

            Car car5 = new Car();
            car5.setBrand("Maruti Suzuki");
            car5.setModel("Swift");
            car5.setYear(2022);
            car5.setPricePerDay(2500.0); // ₹2,500 per day
            car5.setType("Hatchback");
            car5.setSeats(5);
            car5.setFuelType("Petrol");
            car5.setDescription("Compact and economical car perfect for city commuting");
            car5.setAvailable(true);
            carRepository.save(car5);

            Car car6 = new Car();
            car6.setBrand("Tata");
            car6.setModel("Nexon");
            car6.setYear(2023);
            car6.setPricePerDay(3800.0); // ₹3,800 per day
            car6.setType("SUV");
            car6.setSeats(5);
            car6.setFuelType("Electric");
            car6.setDescription("Electric SUV with zero emissions and modern technology");
            car6.setAvailable(true);
            carRepository.save(car6);

            Car car7 = new Car();
            car7.setBrand("Mahindra");
            car7.setModel("Scorpio");
            car7.setYear(2022);
            car7.setPricePerDay(4500.0); // ₹4,500 per day
            car7.setType("SUV");
            car7.setSeats(8);
            car7.setFuelType("Diesel");
            car7.setDescription("Rugged SUV perfect for family trips and off-road adventures");
            car7.setAvailable(true);
            carRepository.save(car7);

            Car car8 = new Car();
            car8.setBrand("Ford");
            car8.setModel("EcoSport");
            car8.setYear(2021);
            car8.setPricePerDay(3200.0); // ₹3,200 per day
            car8.setType("SUV");
            car8.setSeats(5);
            car8.setFuelType("Petrol");
            car8.setDescription("Compact SUV with sporty design and good performance");
            car8.setAvailable(true);
            carRepository.save(car8);

            Car car9 = new Car();
            car9.setBrand("Volkswagen");
            car9.setModel("Polo");
            car9.setYear(2022);
            car9.setPricePerDay(2800.0); // ₹2,800 per day
            car9.setType("Hatchback");
            car9.setSeats(5);
            car9.setFuelType("Petrol");
            car9.setDescription("Premium hatchback with European build quality");
            car9.setAvailable(true);
            carRepository.save(car9);

            Car car10 = new Car();
            car10.setBrand("Kia");
            car10.setModel("Seltos");
            car10.setYear(2023);
            car10.setPricePerDay(4200.0); // ₹4,200 per day
            car10.setType("SUV");
            car10.setSeats(5);
            car10.setFuelType("Petrol");
            car10.setDescription("Feature-rich SUV with advanced safety and technology");
            car10.setAvailable(true);
            carRepository.save(car10);

            System.out.println("Sample cars added successfully!");
        }

        System.out.println("Database initialization completed!");
    }
}
