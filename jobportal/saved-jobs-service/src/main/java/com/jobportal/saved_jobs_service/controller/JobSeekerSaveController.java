package com.jobportal.saved_jobs_service.controller;

import com.jobportal.saved_jobs_service.dto.JobDto;
import com.jobportal.saved_jobs_service.dto.JobSeekerProfileDto;
import com.jobportal.saved_jobs_service.dto.UserDto;
import com.jobportal.saved_jobs_service.entity.JobSeekerSave;
import com.jobportal.saved_jobs_service.feign.client.JobServiceClient;
import com.jobportal.saved_jobs_service.feign.client.JobSeekerServiceClient;
import com.jobportal.saved_jobs_service.feign.client.UserServiceClient;
import com.jobportal.saved_jobs_service.services.JobSeekerSaveService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class JobSeekerSaveController {

    private final JobSeekerSaveService jobSeekerSaveService;
    private final UserServiceClient userServiceClient;
    private final JobServiceClient jobServiceClient;
    private final JobSeekerServiceClient jobSeekerServiceClient;

    public JobSeekerSaveController(JobSeekerSaveService jobSeekerSaveService,
                                   UserServiceClient userServiceClient,
                                   JobServiceClient jobServiceClient,
                                   JobSeekerServiceClient jobSeekerServiceClient) {
        this.jobSeekerSaveService = jobSeekerSaveService;
        this.userServiceClient = userServiceClient;
        this.jobServiceClient = jobServiceClient;
        this.jobSeekerServiceClient = jobSeekerServiceClient;
    }

    @PostMapping("job-details/save/{id}")
    public String save(@PathVariable("id") int id, JobSeekerSave jobSeekerSave) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            UserDto userDto = userServiceClient.getUserByEmail(authentication.getName());
            if (userDto != null) {
                jobSeekerSave.setJobId(id);
                jobSeekerSave.setUserId(userDto.userId());
                jobSeekerSaveService.addNew(jobSeekerSave);
            }
        }
        return "redirect:/dashboard/";
    }

    @GetMapping("saved-jobs/")
    public String savedJobs(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<JobDto> jobPost = new ArrayList<>();
        UserDto userDto = null;
        JobSeekerProfileDto profile = null;

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            userDto = userServiceClient.getUserByEmail(authentication.getName());
            if (userDto != null) {
                profile = jobSeekerServiceClient.getJobSeekerProfile(userDto.userId());
                List<JobSeekerSave> savedList = jobSeekerSaveService.getCandidatesJob(userDto.userId());
                for (JobSeekerSave save : savedList) {
                    JobDto job = jobServiceClient.getJobById(save.getJobId());
                    if (job != null) jobPost.add(job);
                }
            }
        }

        model.addAttribute("jobPost", jobPost);
        model.addAttribute("user", profile);
        return "saved-jobs";
    }
}

