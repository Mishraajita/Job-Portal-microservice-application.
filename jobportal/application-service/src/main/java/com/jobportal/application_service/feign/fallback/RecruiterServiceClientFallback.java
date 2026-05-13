package com.jobportal.application_service.feign.fallback;

import com.jobportal.application_service.dto.RecruiterProfileDto;
import com.jobportal.application_service.feign.client.RecruiterServiceClient;
import org.springframework.stereotype.Component;

@Component
public class RecruiterServiceClientFallback implements RecruiterServiceClient {

    @Override
    public RecruiterProfileDto getRecruiterProfile(int userId) {
        return null;
    }
}
