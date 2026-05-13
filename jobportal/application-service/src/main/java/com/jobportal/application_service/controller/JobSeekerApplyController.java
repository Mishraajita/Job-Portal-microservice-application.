package com.jobportal.application_service.controller;

import com.jobportal.application_service.dto.JobDto;
import com.jobportal.application_service.dto.JobSeekerProfileDto;
import com.jobportal.application_service.dto.RecruiterProfileDto;
import com.jobportal.application_service.dto.UserDto;
import com.jobportal.application_service.entity.JobSeekerApply;
import com.jobportal.application_service.feign.client.JobServiceClient;
import com.jobportal.application_service.feign.client.JobSeekerServiceClient;
import com.jobportal.application_service.feign.client.RecruiterServiceClient;
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

import java.util.Date;
import java.util.List;

@Controller
public class JobSeekerApplyController {

    private final JobSeekerApplyService jobSeekerApplyService;
    private final UserServiceClient userServiceClient;
    private final JobServiceClient jobServiceClient;
    private final JobSeekerServiceClient jobSeekerServiceClient;
    private final RecruiterServiceClient recruiterServiceClient;

    @Autowired
    public JobSeekerApplyController(JobSeekerApplyService jobSeekerApplyService,
                                    UserServiceClient userServiceClient,
                                    JobServiceClient jobServiceClient,
                                    JobSeekerServiceClient jobSeekerServiceClient,
                                    RecruiterServiceClient recruiterServiceClient) {
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.userServiceClient = userServiceClient;
        this.jobServiceClient = jobServiceClient;
        this.jobSeekerServiceClient = jobSeekerServiceClient;
        this.recruiterServiceClient = recruiterServiceClient;
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
                    model.addAttribute("applyList", applyList);
                } else {
                    JobSeekerProfileDto profile = jobSeekerServiceClient.getJobSeekerProfile(userDto.userId());
                    model.addAttribute("user", profile);
                    boolean alreadyApplied = applyList.stream()
                            .anyMatch(a -> a.getUserId().equals(userDto.userId()));
                    // saved status via saved-jobs-service is checked on dashboard; here just apply check
                    model.addAttribute("alreadyApplied", alreadyApplied);
                    model.addAttribute("alreadySaved", false);
                }
            }
        }

        model.addAttribute("applyJob", new JobSeekerApply());
        model.addAttribute("jobDetails", jobDetails);
        return "job-details";
    }

    @PostMapping("job-details/apply/{id}")
    public String apply(@PathVariable("id") int id, JobSeekerApply jobSeekerApply) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            UserDto userDto = userServiceClient.getUserByEmail(authentication.getName());
            if (userDto != null) {
                jobSeekerApply = new JobSeekerApply();
                jobSeekerApply.setUserId(userDto.userId());
                jobSeekerApply.setJobId(id);
                jobSeekerApply.setApplyDate(new Date());
                jobSeekerApplyService.addNew(jobSeekerApply);
            }
        }
        return "redirect:/dashboard/";
    }
}

