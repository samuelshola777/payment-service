package com.paymentService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for the Payment Service.
 * This class serves as the entry point for the Spring Boot application and enables auto-configuration.
 */
@SpringBootApplication
public class PaymentServiceApplication {

	/**
	 * The main method that bootstraps and launches the Spring Boot application.
	 * 
	 * @param args Command line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
	}

}
