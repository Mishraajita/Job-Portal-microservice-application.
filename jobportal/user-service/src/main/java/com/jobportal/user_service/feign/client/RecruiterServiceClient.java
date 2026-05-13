package com.jobportal.user_service.feign.client;

import com.jobportal.user_service.feign.fallback.RecruiterServiceClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "RECRUITER-SERVICE", fallback = RecruiterServiceClientFallback.class)
public interface RecruiterServiceClient {

    @PostMapping("/api/recruiters/create/{userId}")
    void createRecruiterProfile(@PathVariable("userId") int userId);
}
