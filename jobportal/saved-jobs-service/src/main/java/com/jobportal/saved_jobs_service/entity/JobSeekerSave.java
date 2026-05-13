package com.jobportal.saved_jobs_service.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "jobId"})
})
public class JobSeekerSave implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "userId")
    private Integer userId;    // FK → JOBSEEKER-SERVICE user account id

    @Column(name = "jobId")
    private Integer jobId;     // FK → JOB-SERVICE job post id

    public JobSeekerSave() {
    }

    public JobSeekerSave(Integer id, Integer userId, Integer jobId) {
        this.id = id;
        this.userId = userId;
        this.jobId = jobId;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getJobId() { return jobId; }
    public void setJobId(Integer jobId) { this.jobId = jobId; }

    @Override
    public String toString() {
        return "JobSeekerSave{id=" + id + ", userId=" + userId + ", jobId=" + jobId + '}';
    }
}
