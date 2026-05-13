package com.jobportal.application_service.repository;

import com.jobportal.application_service.entity.JobSeekerApply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply, Integer> {

    List<JobSeekerApply> findByUserId(Integer userId);

    List<JobSeekerApply> findByJobId(Integer jobId);
}
