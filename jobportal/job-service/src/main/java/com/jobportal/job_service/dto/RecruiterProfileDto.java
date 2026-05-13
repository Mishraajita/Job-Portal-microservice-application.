package com.jobportal.job_service.dto;

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
