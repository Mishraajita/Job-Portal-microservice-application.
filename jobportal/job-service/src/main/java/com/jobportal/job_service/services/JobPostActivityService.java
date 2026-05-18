package com.jobportal.job_service.services;

import com.jobportal.job_service.entity.IRecruiterJobs;
import com.jobportal.job_service.entity.JobCompany;
import com.jobportal.job_service.entity.JobLocation;
import com.jobportal.job_service.entity.JobPostActivity;
import com.jobportal.job_service.entity.RecruiterJobsDto;
import com.jobportal.job_service.feign.client.ApplicationServiceClient;
import com.jobportal.job_service.repository.JobPostActivityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class JobPostActivityService {

    private final JobPostActivityRepository jobPostActivityRepository;
    private final ApplicationServiceClient applicationServiceClient;

    public JobPostActivityService(JobPostActivityRepository jobPostActivityRepository,
                                  ApplicationServiceClient applicationServiceClient) {
        this.jobPostActivityRepository = jobPostActivityRepository;
        this.applicationServiceClient = applicationServiceClient;
    }

    public JobPostActivity addNew(JobPostActivity jobPostActivity) {
        return jobPostActivityRepository.save(jobPostActivity);
    }

    public List<RecruiterJobsDto> getRecruiterJobs(int recruiter) {
        List<IRecruiterJobs> recruiterJobsDtos = jobPostActivityRepository.getRecruiterJobs(recruiter);
        List<RecruiterJobsDto> recruiterJobsDtoList = new ArrayList<>();
        for (IRecruiterJobs rec : recruiterJobsDtos) {
            JobLocation loc = new JobLocation(rec.getLocationId(), rec.getCity(), rec.getState(), rec.getCountry());
            JobCompany comp = new JobCompany(rec.getCompanyId(), rec.getName(), "");
            long count = applicationServiceClient.getApplicationsByJob(rec.getJob_post_id()).size();
            recruiterJobsDtoList.add(new RecruiterJobsDto(count, rec.getJob_post_id(),
                    rec.getJob_title(), loc, comp));
        }
        return recruiterJobsDtoList;
    }

    public JobPostActivity getOne(int id) {
        return jobPostActivityRepository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));
    }

    public void deleteById(int id) {
        jobPostActivityRepository.deleteById(id);
    }

    public List<JobPostActivity> getAll() {
        return jobPostActivityRepository.findAll();
    }

    public List<JobPostActivity> search(String job, String location, List<String> type, List<String> remote, LocalDate searchDate) {
        return Objects.isNull(searchDate) ? jobPostActivityRepository.searchWithoutDate(job, location, remote, type)
                : jobPostActivityRepository.search(job, location, remote, type, searchDate);
    }
}
