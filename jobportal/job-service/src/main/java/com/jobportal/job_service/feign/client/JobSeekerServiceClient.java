package com.jobportal.job_service.feign.client;

import com.jobportal.job_service.dto.JobSeekerProfileDto;
import com.jobportal.job_service.feign.fallback.JobSeekerServiceClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "JOBSEEKER-SERVICE", fallback = JobSeekerServiceClientFallback.class)
public interface JobSeekerServiceClient {

    @GetMapping("/api/jobseekers/{userId}")
    JobSeekerProfileDto getJobSeekerProfile(@PathVariable("userId") int userId);
}
