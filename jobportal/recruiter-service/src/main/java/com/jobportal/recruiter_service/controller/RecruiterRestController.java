package com.jobportal.recruiter_service.controller;

import com.jobportal.recruiter_service.dto.RecruiterProfileDto;
import com.jobportal.recruiter_service.entity.RecruiterProfile;
import com.jobportal.recruiter_service.services.RecruiterProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/recruiters")
public class RecruiterRestController {

    private final RecruiterProfileService recruiterProfileService;

    public RecruiterRestController(RecruiterProfileService recruiterProfileService) {
        this.recruiterProfileService = recruiterProfileService;
    }

    // Called by application-service
    @GetMapping("/{userId}")
    public ResponseEntity<RecruiterProfileDto> getRecruiterProfile(@PathVariable int userId) {
        Optional<RecruiterProfile> profile = recruiterProfileService.getOne(userId);
        return profile.map(p -> ResponseEntity.ok(new RecruiterProfileDto(
                p.getUserAccountId(),
                p.getFirstName(),
                p.getLastName(),
                p.getCompany(),
                p.getCity(),
                p.getState(),
                p.getCountry(),
                p.getPhotosImagePath()
        ))).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create/{userId}")
    public ResponseEntity<Void> createRecruiterProfile(@PathVariable int userId) {
        recruiterProfileService.addNew(new RecruiterProfile(userId));
        return ResponseEntity.ok().build();
    }
}
