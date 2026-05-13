package com.jobportal.application_service.feign.fallback;

import com.jobportal.application_service.dto.JobDto;
import com.jobportal.application_service.feign.client.JobServiceClient;
import org.springframework.stereotype.Component;

@Component
public class JobServiceClientFallback implements JobServiceClient {

    @Override
    public JobDto getJobById(int id) {
        return null;
    }
}
