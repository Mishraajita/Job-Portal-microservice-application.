package com.jobportal.saved_jobs_service.services;

import com.jobportal.saved_jobs_service.entity.JobSeekerSave;
import com.jobportal.saved_jobs_service.repository.JobSeekerSaveRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobSeekerSaveService {

    private final JobSeekerSaveRepository jobSeekerSaveRepository;

    public JobSeekerSaveService(JobSeekerSaveRepository jobSeekerSaveRepository) {
        this.jobSeekerSaveRepository = jobSeekerSaveRepository;
    }

    public List<JobSeekerSave> getCandidatesJob(Integer userId) {
        return jobSeekerSaveRepository.findByUserId(userId);
    }

    public List<JobSeekerSave> getJobCandidates(Integer jobId) {
        return jobSeekerSaveRepository.findByJobId(jobId);
    }

    public void addNew(JobSeekerSave jobSeekerSave) {
        jobSeekerSaveRepository.save(jobSeekerSave);
    }
}
