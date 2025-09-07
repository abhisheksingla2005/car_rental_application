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
        System.out.println("Adding car: " + car.getName() + " " + car.getModel());
        Car savedCar = carRepository.save(car);
        System.out.println("Car saved with ID: " + savedCar.getId());
        return ResponseEntity.ok(savedCar);
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
}
