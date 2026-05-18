package com.jobportal.application_service.feign.client;

import com.jobportal.application_service.feign.fallback.SavedJobsServiceClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SAVED-JOBS-SERVICE", fallback = SavedJobsServiceClientFallback.class)
public interface SavedJobsServiceClient {

    @GetMapping("/api/saved-jobs/check/{userId}/{jobId}")
    boolean isAlreadySaved(@PathVariable("userId") int userId, @PathVariable("jobId") int jobId);
}
