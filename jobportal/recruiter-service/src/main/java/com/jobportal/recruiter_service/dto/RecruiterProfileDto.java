package com.jobportal.recruiter_service.dto;

// Lightweight DTO returned by recruiter-service REST API for inter-service consumption
public record RecruiterProfileDto(
        int userAccountId,
        String firstName,
        String lastName,
        String company,
        String city,
        String state,
        String country,
        String photosImagePath
) {}
