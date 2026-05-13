package com.jobportal.user_service.feign.fallback;

import com.jobportal.user_service.feign.client.RecruiterServiceClient;
import org.springframework.stereotype.Component;

@Component
public class RecruiterServiceClientFallback implements RecruiterServiceClient {

    @Override
    public void createRecruiterProfile(int userId) {
        // fallback: log or ignore
    }
}
