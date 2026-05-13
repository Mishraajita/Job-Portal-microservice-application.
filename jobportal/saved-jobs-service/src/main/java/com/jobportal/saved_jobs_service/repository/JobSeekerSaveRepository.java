package com.jobportal.saved_jobs_service.repository;

import com.jobportal.saved_jobs_service.entity.JobSeekerSave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerSaveRepository extends JpaRepository<JobSeekerSave, Integer> {

    List<JobSeekerSave> findByUserId(Integer userId);

    List<JobSeekerSave> findByJobId(Integer jobId);
}