package com.jobportal.saved_jobs_service.controller;

import com.jobportal.saved_jobs_service.entity.JobSeekerSave;
import com.jobportal.saved_jobs_service.services.JobSeekerSaveService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/saved-jobs")
public class SavedJobsRestController {

    private final JobSeekerSaveService jobSeekerSaveService;

    public SavedJobsRestController(JobSeekerSaveService jobSeekerSaveService) {
        this.jobSeekerSaveService = jobSeekerSaveService;
    }

    @GetMapping("/by-jobseeker/{userId}")
    public List<Integer> getSavedJobsByJobSeeker(@PathVariable("userId") int userId) {
        return jobSeekerSaveService.getCandidatesJob(userId).stream()
                .map(JobSeekerSave::getJobId)
                .collect(Collectors.toList());
    }
    
    @GetMapping("/check/{userId}/{jobId}")
    public boolean isAlreadySaved(@PathVariable("userId") int userId, @PathVariable("jobId") int jobId) {
        return jobSeekerSaveService.isAlreadySaved(userId, jobId);
    }
}
