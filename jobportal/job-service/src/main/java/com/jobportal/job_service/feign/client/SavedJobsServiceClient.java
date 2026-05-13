package com.jobportal.job_service.feign.client;

import com.jobportal.job_service.feign.fallback.SavedJobsServiceClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "SAVED-JOBS-SERVICE", fallback = SavedJobsServiceClientFallback.class)
public interface SavedJobsServiceClient {

    @GetMapping("/api/saved-jobs/by-jobseeker/{userId}")
    List<Integer> getSavedJobsByJobSeeker(@PathVariable("userId") int userId);
}
