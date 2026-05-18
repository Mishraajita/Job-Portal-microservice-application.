package com.jobportal.saved_jobs_service.controller;

import com.jobportal.saved_jobs_service.dto.JobDto;
import com.jobportal.saved_jobs_service.dto.JobSeekerProfileDto;
import com.jobportal.saved_jobs_service.dto.UserDto;
import com.jobportal.saved_jobs_service.entity.JobSeekerSave;
import com.jobportal.saved_jobs_service.feign.client.JobServiceClient;
import com.jobportal.saved_jobs_service.feign.client.JobSeekerServiceClient;
import com.jobportal.saved_jobs_service.feign.client.UserServiceClient;
import com.jobportal.saved_jobs_service.services.JobSeekerSaveService;
import jakarta.servlet.http.HttpServletRequest;
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
//    public String save(@PathVariable("id") int id, JobSeekerSave jobSeekerSave) { }
    public String save(@PathVariable("id") int id, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            UserDto userDto = userServiceClient.getUserByEmail(authentication.getName());
            if (userDto != null) {
                JobSeekerSave jobSeekerSave = new JobSeekerSave();    // for issue : Row was already updated or deleted by another transaction for entity [com.jobportal.saved_jobs_service.entity.JobSeekerSave with id '2']
                jobSeekerSave.setJobId(id);
                jobSeekerSave.setUserId(userDto.userId());
                if(!jobSeekerSaveService.isAlreadySaved(userDto.userId(), id)) {
                	jobSeekerSaveService.addNew(jobSeekerSave);
                }
            }
        }
        return buildGatewayRedirectUrl(request, "dashboard/");
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

     private String buildGatewayRedirectUrl(HttpServletRequest request, String path) {
        String forwardedHost = request.getHeader("X-Forwarded-Host");
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        String host;
        if (forwardedHost != null && !forwardedHost.isEmpty()) {
            host = forwardedHost.split(",")[0].trim();
            if (host.contains(":")) host = host.substring(0, host.lastIndexOf(":"));
        } else {
            host = "localhost";
        }
        String proto = (forwardedProto != null && !forwardedProto.isEmpty()) ? forwardedProto.split(",")[0].trim() : "http";
        return "redirect:" + proto + "://" + host + ":8080/" + path;
    }
}