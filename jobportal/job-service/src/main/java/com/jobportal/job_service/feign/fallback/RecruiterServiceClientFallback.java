package com.jobportal.job_service.feign.fallback;

import com.jobportal.job_service.dto.RecruiterProfileDto;
import com.jobportal.job_service.feign.client.RecruiterServiceClient;
import org.springframework.stereotype.Component;

@Component
public class RecruiterServiceClientFallback implements RecruiterServiceClient {

    @Override
    public RecruiterProfileDto getRecruiterProfile(int userId) {
        return null;
    }
}
