package com.jobportal.jobseeker_service.controller;

import com.jobportal.jobseeker_service.dto.JobSeekerProfileDto;
import com.jobportal.jobseeker_service.entity.JobSeekerProfile;
import com.jobportal.jobseeker_service.services.JobSeekerProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/jobseekers")
public class JobSeekerRestController {

    private final JobSeekerProfileService jobSeekerProfileService;

    public JobSeekerRestController(JobSeekerProfileService jobSeekerProfileService) {
        this.jobSeekerProfileService = jobSeekerProfileService;
    }

    // Called by application-service and saved-jobs-service
    @GetMapping("/{userId}")
    public ResponseEntity<JobSeekerProfileDto> getJobSeekerProfile(@PathVariable int userId) {
        Optional<JobSeekerProfile> profile = jobSeekerProfileService.getOne(userId);
        return profile.map(p -> ResponseEntity.ok(new JobSeekerProfileDto(
                p.getUserAccountId(),
                p.getFirstName(),
                p.getLastName(),
                p.getCity(),
                p.getState(),
                p.getCountry(),
                p.getWorkAuthorization(),
                p.getEmploymentType(),
                p.getPhotosImagePath(),
                p.getResume()
        ))).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create/{userId}")
    public ResponseEntity<Void> createJobSeekerProfile(@PathVariable int userId) {
        jobSeekerProfileService.addNew(new JobSeekerProfile(userId));
        return ResponseEntity.ok().build();
    }
}
