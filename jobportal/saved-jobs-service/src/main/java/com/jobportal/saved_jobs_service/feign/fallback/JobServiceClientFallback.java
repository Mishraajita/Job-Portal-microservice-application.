package com.jobportal.saved_jobs_service.feign.fallback;

import com.jobportal.saved_jobs_service.dto.JobDto;
import com.jobportal.saved_jobs_service.feign.client.JobServiceClient;
import org.springframework.stereotype.Component;

@Component
public class JobServiceClientFallback implements JobServiceClient {

    @Override
    public JobDto getJobById(int id) {
        return null;
    }
}
