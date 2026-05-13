package com.jobportal.job_service.feign.fallback;

import com.jobportal.job_service.feign.client.SavedJobsServiceClient;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class SavedJobsServiceClientFallback implements SavedJobsServiceClient {

    @Override
    public List<Integer> getSavedJobsByJobSeeker(int userId) {
        return Collections.emptyList();
    }
}
