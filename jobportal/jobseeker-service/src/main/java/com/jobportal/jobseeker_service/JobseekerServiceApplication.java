package com.jobportal.jobseeker_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class JobseekerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobseekerServiceApplication.class, args);
	}

}
