package com.jobportal.application_service.controller;

import com.jobportal.application_service.entity.JobSeekerApply;
import com.jobportal.application_service.services.JobSeekerApplyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
public class ApplicationRestController {

    private final JobSeekerApplyService jobSeekerApplyService;

    public ApplicationRestController(JobSeekerApplyService jobSeekerApplyService) {
        this.jobSeekerApplyService = jobSeekerApplyService;
    }

    @GetMapping("/by-jobseeker/{userId}")
    public List<Integer> getApplicationsByJobSeeker(@PathVariable("userId") int userId) {
        return jobSeekerApplyService.getCandidatesJobs(userId).stream()
                .map(JobSeekerApply::getJobId)
                .collect(Collectors.toList());
    }

    @GetMapping("/by-job/{jobId}")
    public List<Integer> getApplicationsByJob(@PathVariable("jobId") int jobId) {
        return jobSeekerApplyService.getJobCandidates(jobId).stream()
                .map(JobSeekerApply::getUserId)
                .collect(Collectors.toList());
    }
}
