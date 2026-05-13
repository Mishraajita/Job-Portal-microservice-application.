package com.jobportal.user_service.feign.fallback;

import com.jobportal.user_service.feign.client.JobSeekerServiceClient;
import org.springframework.stereotype.Component;

@Component
public class JobSeekerServiceClientFallback implements JobSeekerServiceClient {

    @Override
    public void createJobSeekerProfile(int userId) {
        // fallback: log or ignore
    }
}
