package com.tapeflicks.rentalstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RentalstoreApplication {

  public static void main(String[] args) {
    SpringApplication.run(RentalstoreApplication.class, args);
  }
}
