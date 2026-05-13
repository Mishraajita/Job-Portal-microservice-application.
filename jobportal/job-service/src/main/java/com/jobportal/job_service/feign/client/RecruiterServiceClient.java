package com.jobportal.job_service.feign.client;

import com.jobportal.job_service.dto.RecruiterProfileDto;
import com.jobportal.job_service.feign.fallback.RecruiterServiceClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "RECRUITER-SERVICE", fallback = RecruiterServiceClientFallback.class)
public interface RecruiterServiceClient {

    @GetMapping("/api/recruiters/{userId}")
    RecruiterProfileDto getRecruiterProfile(@PathVariable("userId") int userId);
}
