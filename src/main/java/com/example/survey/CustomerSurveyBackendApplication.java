package com.example.survey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CustomerSurveyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerSurveyBackendApplication.class, args);
	}

}
