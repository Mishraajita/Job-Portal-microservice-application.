package com.jobportal.application_service.services;

import com.jobportal.application_service.entity.JobSeekerApply;
import com.jobportal.application_service.repository.JobSeekerApplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobSeekerApplyService {

    private final JobSeekerApplyRepository jobSeekerApplyRepository;

    @Autowired
    public JobSeekerApplyService(JobSeekerApplyRepository jobSeekerApplyRepository) {
        this.jobSeekerApplyRepository = jobSeekerApplyRepository;
    }

    public List<JobSeekerApply> getCandidatesJobs(Integer userId) {
        return jobSeekerApplyRepository.findByUserId(userId);
    }

    public List<JobSeekerApply> getJobCandidates(Integer jobId) {
        return jobSeekerApplyRepository.findByJobId(jobId);
    }

    public void addNew(JobSeekerApply jobSeekerApply) {
        jobSeekerApplyRepository.save(jobSeekerApply);
    }
}
