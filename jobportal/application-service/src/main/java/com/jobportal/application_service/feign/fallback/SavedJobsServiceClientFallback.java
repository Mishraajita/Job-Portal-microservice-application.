package com.jobportal.application_service.feign.fallback;

import com.jobportal.application_service.feign.client.SavedJobsServiceClient;
import org.springframework.stereotype.Component;

@Component
public class SavedJobsServiceClientFallback implements SavedJobsServiceClient {

    @Override
    public boolean isAlreadySaved(int userId, int jobId) {
        return false;
    }
}
