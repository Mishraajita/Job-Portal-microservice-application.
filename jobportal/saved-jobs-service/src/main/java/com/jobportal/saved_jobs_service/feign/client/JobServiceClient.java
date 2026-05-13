package com.jobportal.saved_jobs_service.feign.client;

import com.jobportal.saved_jobs_service.dto.JobDto;
import com.jobportal.saved_jobs_service.feign.fallback.JobServiceClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "JOB-SERVICE", fallback = JobServiceClientFallback.class)
public interface JobServiceClient {

    @GetMapping("/api/jobs/{id}")
    JobDto getJobById(@PathVariable("id") int id);
}
