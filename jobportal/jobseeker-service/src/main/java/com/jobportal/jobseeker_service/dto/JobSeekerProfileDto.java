package com.jobportal.jobseeker_service.dto;

// Lightweight DTO returned by jobseeker-service REST API for inter-service consumption
public record JobSeekerProfileDto(
        int userAccountId,
        String firstName,
        String lastName,
        String city,
        String state,
        String country,
        String workAuthorization,
        String employmentType,
        String photosImagePath,
        String resume
) {}
