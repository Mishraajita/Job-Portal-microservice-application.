package com.jobportal.user_service.dto;

public record UserDto(
        int userId,
        String email,
        int userTypeId,
        String userTypeName
) {}
