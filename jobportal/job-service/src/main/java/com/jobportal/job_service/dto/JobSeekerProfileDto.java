package com.jobportal.job_service.dto;

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
