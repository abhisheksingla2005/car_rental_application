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
		int port = webServerAppCtxt.getWebServer().getPort();
		System.out.println("Car Rental Application has started successfully!");
		System.out.println("Application is running on: http://localhost:" + port);
		System.out.println("Open your browser and navigate to the URL above to access the car rental application!");
	}
}
