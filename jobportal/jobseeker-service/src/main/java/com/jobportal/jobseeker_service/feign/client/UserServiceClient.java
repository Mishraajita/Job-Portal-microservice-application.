package com.jobportal.jobseeker_service.feign.client;

import com.jobportal.jobseeker_service.dto.UserDto;
import com.jobportal.jobseeker_service.feign.fallback.UserServiceClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE", fallback = UserServiceClientFallback.class)
public interface UserServiceClient {

    @GetMapping("/api/users/by-email/{email}")
    UserDto getUserByEmail(@PathVariable("email") String email);

    @GetMapping("/api/users/{id}")
    UserDto getUserById(@PathVariable("id") int id);
}
