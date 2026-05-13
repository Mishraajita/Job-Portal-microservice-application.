package com.jobportal.user_service.services;

import com.jobportal.user_service.entity.Users;
import com.jobportal.user_service.feign.client.JobSeekerServiceClient;
import com.jobportal.user_service.feign.client.RecruiterServiceClient;
import com.jobportal.user_service.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final RecruiterServiceClient recruiterServiceClient;
    private final JobSeekerServiceClient jobSeekerServiceClient;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersService(UsersRepository usersRepository,
                        RecruiterServiceClient recruiterServiceClient,
                        JobSeekerServiceClient jobSeekerServiceClient,
                        PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.recruiterServiceClient = recruiterServiceClient;
        this.jobSeekerServiceClient = jobSeekerServiceClient;
        this.passwordEncoder = passwordEncoder;
    }

    public Users addNew(Users users) {
        users.setActive(true);
        users.setRegistrationDate(new Date(System.currentTimeMillis()));
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        Users savedUser = usersRepository.save(users);
        int userTypeId = users.getUserTypeId().getUserTypeId();
        if (userTypeId == 1) {
            recruiterServiceClient.createRecruiterProfile(savedUser.getUserId());
        } else {
            jobSeekerServiceClient.createJobSeekerProfile(savedUser.getUserId());
        }
        return savedUser;
    }

    public Optional<Users> getUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    public Users getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            return usersRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Could not find user"));
        }
        return null;
    }

    public Users findByEmail(String email) {
        return usersRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
