package com.jobportal.job_service.dto;

// DTO received from USER-SERVICE via Feign
public record UserDto(
        int userId,
        String email,
        int userTypeId,
        String userTypeName
) {}
