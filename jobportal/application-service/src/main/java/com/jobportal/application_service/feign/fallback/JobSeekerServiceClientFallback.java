package com.jobportal.application_service.feign.fallback;

import com.jobportal.application_service.dto.JobSeekerProfileDto;
import com.jobportal.application_service.feign.client.JobSeekerServiceClient;
import org.springframework.stereotype.Component;

@Component
public class JobSeekerServiceClientFallback implements JobSeekerServiceClient {

    @Override
    public JobSeekerProfileDto getJobSeekerProfile(int userId) {
        return null;
    }
}
