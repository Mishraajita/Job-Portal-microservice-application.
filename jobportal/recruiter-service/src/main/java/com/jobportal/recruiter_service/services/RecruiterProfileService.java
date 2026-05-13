package com.jobportal.recruiter_service.services;

import com.jobportal.recruiter_service.dto.UserDto;
import com.jobportal.recruiter_service.entity.RecruiterProfile;
import com.jobportal.recruiter_service.feign.client.UserServiceClient;
import com.jobportal.recruiter_service.repository.RecruiterProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RecruiterProfileService {

    private final RecruiterProfileRepository recruiterProfileRepository;
    private final UserServiceClient userServiceClient;

    @Autowired
    public RecruiterProfileService(RecruiterProfileRepository recruiterProfileRepository,
                                   UserServiceClient userServiceClient) {
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.userServiceClient = userServiceClient;
    }

    public Optional<RecruiterProfile> getOne(Integer id) {
        return recruiterProfileRepository.findById(id);
    }

    public RecruiterProfile addNew(RecruiterProfile recruiterProfile) {
        return recruiterProfileRepository.save(recruiterProfile);
    }

    public RecruiterProfile getCurrentRecruiterProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            UserDto userDto = userServiceClient.getUserByEmail(currentUsername);
            if (userDto == null) return null;
            return recruiterProfileRepository.findById(userDto.userId()).orElse(null);
        }
        return null;
    }
}
