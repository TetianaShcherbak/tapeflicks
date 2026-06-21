package com.tapeflicks.rentalstore;

import org.springframework.boot.SpringApplication;

public class TestRentalstoreApplication {

	public static void main(String[] args) {
		SpringApplication.from(RentalstoreApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
