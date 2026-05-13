package com.jobportal.jobseeker_service.services;

import com.jobportal.jobseeker_service.dto.UserDto;
import com.jobportal.jobseeker_service.entity.JobSeekerProfile;
import com.jobportal.jobseeker_service.feign.client.UserServiceClient;
import com.jobportal.jobseeker_service.repository.JobSeekerProfileRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JobSeekerProfileService {

    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final UserServiceClient userServiceClient;

    public JobSeekerProfileService(JobSeekerProfileRepository jobSeekerProfileRepository,
                                   UserServiceClient userServiceClient) {
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.userServiceClient = userServiceClient;
    }

    public Optional<JobSeekerProfile> getOne(Integer id) {
        return jobSeekerProfileRepository.findById(id);
    }

    public JobSeekerProfile addNew(JobSeekerProfile jobSeekerProfile) {
        return jobSeekerProfileRepository.save(jobSeekerProfile);
    }

    public JobSeekerProfile getCurrentSeekerProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            UserDto userDto = userServiceClient.getUserByEmail(currentUsername);
            if (userDto == null) return null;
            return jobSeekerProfileRepository.findById(userDto.userId()).orElse(null);
        }
        return null;
    }
}
