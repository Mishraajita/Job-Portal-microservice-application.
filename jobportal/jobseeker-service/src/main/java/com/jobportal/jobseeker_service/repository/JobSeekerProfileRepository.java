package com.jobportal.jobseeker_service.repository;

import com.jobportal.jobseeker_service.entity.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobSeekerProfileRepository extends JpaRepository<JobSeekerProfile, Integer> {
}
