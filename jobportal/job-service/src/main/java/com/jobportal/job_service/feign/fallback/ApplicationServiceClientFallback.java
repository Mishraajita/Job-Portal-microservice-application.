package com.jobportal.job_service.feign.fallback;

import com.jobportal.job_service.feign.client.ApplicationServiceClient;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ApplicationServiceClientFallback implements ApplicationServiceClient {

    @Override
    public List<Integer> getApplicationsByJobSeeker(int userId) {
        return Collections.emptyList();
    }

    @Override
    public List<Integer> getApplicationsByJob(int jobId) {
        return Collections.emptyList();
    }
}
