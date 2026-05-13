package com.jobportal.recruiter_service.feign.fallback;

import com.jobportal.recruiter_service.dto.UserDto;
import com.jobportal.recruiter_service.feign.client.UserServiceClient;
import org.springframework.stereotype.Component;

@Component
public class UserServiceClientFallback implements UserServiceClient {

    @Override
    public UserDto getUserByEmail(String email) {
        return null;
    }

    @Override
    public UserDto getUserById(int id) {
        return null;
    }
}
