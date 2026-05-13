package com.jobportal.recruiter_service.repository;

import com.jobportal.recruiter_service.entity.RecruiterProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruiterProfileRepository extends JpaRepository<RecruiterProfile, Integer> {
}
