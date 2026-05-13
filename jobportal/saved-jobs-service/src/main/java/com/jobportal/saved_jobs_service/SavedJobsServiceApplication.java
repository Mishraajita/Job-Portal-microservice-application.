package com.jobportal.saved_jobs_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SavedJobsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SavedJobsServiceApplication.class, args);
	}

}
