package com.jobportal.job_service.feign.fallback;

import com.jobportal.job_service.dto.JobSeekerProfileDto;
import com.jobportal.job_service.feign.client.JobSeekerServiceClient;
import org.springframework.stereotype.Component;

@Component
public class JobSeekerServiceClientFallback implements JobSeekerServiceClient {

    @Override
    public JobSeekerProfileDto getJobSeekerProfile(int userId) {
        return null;
    }
}
