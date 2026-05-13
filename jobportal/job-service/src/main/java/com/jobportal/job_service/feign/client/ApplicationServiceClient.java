package com.jobportal.job_service.feign.client;

import com.jobportal.job_service.feign.fallback.ApplicationServiceClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "APPLICATION-SERVICE", fallback = ApplicationServiceClientFallback.class)
public interface ApplicationServiceClient {

    @GetMapping("/api/applications/by-jobseeker/{userId}")
    List<Integer> getApplicationsByJobSeeker(@PathVariable("userId") int userId);

    @GetMapping("/api/applications/by-job/{jobId}")
    List<Integer> getApplicationsByJob(@PathVariable("jobId") int jobId);
}
