package com.jobportal.application_service.controller;

import com.jobportal.application_service.dto.ApplyListItemDto;
import com.jobportal.application_service.dto.JobDto;
import com.jobportal.application_service.dto.JobSeekerProfileDto;
import com.jobportal.application_service.dto.RecruiterProfileDto;
import com.jobportal.application_service.dto.UserDto;
import com.jobportal.application_service.entity.JobSeekerApply;
import com.jobportal.application_service.feign.client.JobServiceClient;
import com.jobportal.application_service.feign.client.JobSeekerServiceClient;
import com.jobportal.application_service.feign.client.RecruiterServiceClient;
import com.jobportal.application_service.feign.client.SavedJobsServiceClient;
import com.jobportal.application_service.feign.client.UserServiceClient;
import com.jobportal.application_service.services.JobSeekerApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.List;

@Controller
public class JobSeekerApplyController {

    private final JobSeekerApplyService jobSeekerApplyService;
    private final UserServiceClient userServiceClient;
    private final JobServiceClient jobServiceClient;
    private final JobSeekerServiceClient jobSeekerServiceClient;
    private final RecruiterServiceClient recruiterServiceClient;
    private final SavedJobsServiceClient savedJobsServiceClient;

    @Autowired
    public JobSeekerApplyController(JobSeekerApplyService jobSeekerApplyService,
                                    UserServiceClient userServiceClient,
                                    JobServiceClient jobServiceClient,
                                    JobSeekerServiceClient jobSeekerServiceClient,
                                    RecruiterServiceClient recruiterServiceClient,
                                    SavedJobsServiceClient savedJobsServiceClient) {
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.userServiceClient = userServiceClient;
        this.jobServiceClient = jobServiceClient;
        this.jobSeekerServiceClient = jobSeekerServiceClient;
        this.recruiterServiceClient = recruiterServiceClient;
        this.savedJobsServiceClient = savedJobsServiceClient;
    }

    @GetMapping("job-details-apply/{id}")
    public String display(@PathVariable("id") int id, Model model) {
        JobDto jobDetails = jobServiceClient.getJobById(id);
        List<JobSeekerApply> applyList = jobSeekerApplyService.getJobCandidates(id);

        // Defaults — ensures buttons render even if auth/feign call fails
        model.addAttribute("alreadyApplied", false);
        model.addAttribute("alreadySaved", false);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            UserDto userDto = userServiceClient.getUserByEmail(authentication.getName());
            if (userDto != null) {
                if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
                    RecruiterProfileDto profile = recruiterServiceClient.getRecruiterProfile(userDto.userId());
                    model.addAttribute("user", profile);
               //   model.addAttribute("applyList", applyList); ----> for monolithic
// for microservice ---> userId is a number, has No .userAccountId property i.e why we use ApplyListItemsDto

                    List<ApplyListItemDto> applyListItems = applyList.stream()
                            .map(a -> new ApplyListItemDto(jobSeekerServiceClient.getJobSeekerProfile(a.getUserId())))
                            .filter(a -> a.userId() != null)
                            .collect(java.util.stream.Collectors.toList());
                    model.addAttribute("applyList", applyListItems);
                } else {
                    JobSeekerProfileDto profile = jobSeekerServiceClient.getJobSeekerProfile(userDto.userId());
                    model.addAttribute("user", profile);
                    boolean alreadyApplied = applyList.stream()
                            .anyMatch(a -> a.getUserId().equals(userDto.userId()));
                    boolean alreadySaved = savedJobsServiceClient.isAlreadySaved(userDto.userId(), id);
                    model.addAttribute("alreadyApplied", alreadyApplied);
                    model.addAttribute("alreadySaved", alreadySaved);
                }
            }
        }

        model.addAttribute("applyJob", new JobSeekerApply());
        model.addAttribute("jobDetails", jobDetails);
        return "job-details";
    }

    @PostMapping("job-details/apply/{id}")
    public String apply(@PathVariable("id") int id, JobSeekerApply jobSeekerApply, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            UserDto userDto = userServiceClient.getUserByEmail(authentication.getName());
            if (userDto != null && !jobSeekerApplyService.isAlreadyApplied(userDto.userId(), id)) {
                jobSeekerApply = new JobSeekerApply();    //if multiple candidates apply for the same job, the new candidates info is correctly added to DB
                jobSeekerApply.setUserId(userDto.userId());
                jobSeekerApply.setJobId(id);
                jobSeekerApply.setApplyDate(new Date());
                jobSeekerApplyService.addNew(jobSeekerApply);
            }
        }
        return buildGatewayRedirectUrl(request, "dashboard/");
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

