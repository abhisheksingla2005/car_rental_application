package com.example.carRentalApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class CarRentalApplication {

	@Autowired
	private ServletWebServerApplicationContext webServerAppCtxt;

	public static void main(String[] args) {
		SpringApplication.run(CarRentalApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
		System.out.println("Car Rental Application has started successfully!");
		System.out.println("Application is running on: http://localhost:8081");
		System.out.println("=== LOGIN CREDENTIALS ===");
		System.out.println("Admin Dashboard: admin@gmail.com / admin123");
	}
}
