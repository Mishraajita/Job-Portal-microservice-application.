package com.jobportal.user_service.feign.client;

import com.jobportal.user_service.feign.fallback.JobSeekerServiceClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "JOBSEEKER-SERVICE", fallback = JobSeekerServiceClientFallback.class)
public interface JobSeekerServiceClient {

    @PostMapping("/api/jobseekers/create/{userId}")
    void createJobSeekerProfile(@PathVariable("userId") int userId);
}
