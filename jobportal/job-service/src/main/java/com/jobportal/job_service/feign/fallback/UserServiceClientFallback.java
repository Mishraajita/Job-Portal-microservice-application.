package com.jobportal.job_service.feign.fallback;

import com.jobportal.job_service.dto.UserDto;
import com.jobportal.job_service.feign.client.UserServiceClient;
import org.springframework.stereotype.Component;

// Returned when USER-SERVICE is down or times out
@Component
public class UserServiceClientFallback implements UserServiceClient {

    @Override
    public UserDto getUserByEmail(String email) {
        return null; // caller must handle null → show "unavailable" in UI
    }

    @Override
    public UserDto getUserById(int id) {
        return null;
    }
}
