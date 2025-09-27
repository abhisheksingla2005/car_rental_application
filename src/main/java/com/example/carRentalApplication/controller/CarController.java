package com.example.carRentalApplication.controller;

import com.example.carRentalApplication.model.Car;
import com.example.carRentalApplication.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@CrossOrigin(origins = "*")
public class CarController {

    @Autowired
    private CarRepository carRepository;

    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        List<Car> cars = carRepository.findAll();
        System.out.println("Retrieved " + cars.size() + " cars from database");
        return ResponseEntity.ok(cars);
    }

    @PostMapping
    public ResponseEntity<Car> addCar(@RequestBody Car car) {
        System.out.println("Adding car: " + car.getBrand() + " " + car.getModel());
        Car savedCar = carRepository.save(car);
        System.out.println("Car saved with ID: " + savedCar.getId());
        return ResponseEntity.ok(savedCar);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestBody Car carDetails) {
        return carRepository.findById(id)
                .map(car -> {
                    car.setBrand(carDetails.getBrand());
                    car.setModel(carDetails.getModel());
                    car.setYear(carDetails.getYear());
                    car.setType(carDetails.getType());
                    car.setSeats(carDetails.getSeats());
                    car.setFuelType(carDetails.getFuelType());
                    car.setPricePerDay(carDetails.getPricePerDay());
                    car.setDescription(carDetails.getDescription());
                    car.setAvailable(carDetails.isAvailable());
                    if (carDetails.getPhotoUrl() != null) {
                        car.setPhotoUrl(carDetails.getPhotoUrl());
                    }
                    return ResponseEntity.ok(carRepository.save(car));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        return carRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Search cars by price range (budget slider)
    @GetMapping("/search/price")
    public ResponseEntity<List<Car>> searchCarsByPrice(@RequestParam double min, @RequestParam double max) {
        List<Car> cars = carRepository.findAll();
        cars.removeIf(car -> car.getPricePerDay() < min || car.getPricePerDay() > max);
        return ResponseEntity.ok(cars);
    }

    // Search cars by model or brand
    @GetMapping("/search")
    public ResponseEntity<List<Car>> searchCarsByModelOrBrand(@RequestParam String query) {
        String q = query.toLowerCase();
        List<Car> cars = carRepository.findAll();
        cars.removeIf(car -> !(car.getModel().toLowerCase().contains(q) || car.getBrand().toLowerCase().contains(q)));
        return ResponseEntity.ok(cars);
    }
}
