package com.jobportal.jobseeker_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve job seeker profile photos and resumes uploaded to photos/candidate/{userId}/
        Path photosPath = Paths.get("photos/candidate");
        registry.addResourceHandler("/photos/candidate/**")
                .addResourceLocations("file:" + photosPath.toAbsolutePath() + "/");
    }
}
