package com.jobportal.recruiter_service.controller;

import com.jobportal.recruiter_service.dto.UserDto;
import com.jobportal.recruiter_service.entity.RecruiterProfile;
import com.jobportal.recruiter_service.feign.client.UserServiceClient;
import com.jobportal.recruiter_service.services.RecruiterProfileService;
import com.jobportal.recruiter_service.util.FileUploadUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/recruiter-profile")
public class RecruiterProfileController {

    private final UserServiceClient userServiceClient;
    private final RecruiterProfileService recruiterProfileService;

    public RecruiterProfileController(UserServiceClient userServiceClient,
                                      RecruiterProfileService recruiterProfileService) {
        this.userServiceClient = userServiceClient;
        this.recruiterProfileService = recruiterProfileService;
    }

    @GetMapping("/")
    public String recruiterProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            UserDto userDto = userServiceClient.getUserByEmail(authentication.getName());
            if (userDto != null) {
                Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.getOne(userDto.userId());
                recruiterProfile.ifPresent(p -> model.addAttribute("profile", p));
            }
        }
        return "recruiter_profile";
    }

    @PostMapping("/addNew")
    public String addNew(RecruiterProfile recruiterProfile,
                         @RequestParam("image") MultipartFile multipartFile,
                         Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            UserDto userDto = userServiceClient.getUserByEmail(authentication.getName());
            if (userDto != null) {
                recruiterProfile.setUserAccountId(userDto.userId());
            }
        }
        model.addAttribute("profile", recruiterProfile);
        String fileName = "";
        if (!Objects.requireNonNull(multipartFile.getOriginalFilename()).isEmpty()) {
            fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            recruiterProfile.setProfilePhoto(fileName);
        }
        RecruiterProfile savedUser = recruiterProfileService.addNew(recruiterProfile);
        String uploadDir = "photos/recruiter/" + savedUser.getUserAccountId();
        try {
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "redirect:/dashboard/";
    }
}

