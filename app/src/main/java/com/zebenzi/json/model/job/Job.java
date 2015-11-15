package com.zebenzi.json.model.job;

import com.zebenzi.json.model.user.User;

/**
 * Created by Vaugan.Nayagar on 2015/11/15.
 */
public class Job {
    int jobid;
    int currentStatusCode;
    String jobStatus;
    String jobStatusDate;
    String jobService;
    User user;
    User worker;
    String jobCreatedDate;
    String jobUpdatedDate;
    JobStatus[] statusHistory;
    int Rating;

    public int getJobid() {
        return jobid;
    }

    public void setJobid(int jobid) {
        this.jobid = jobid;
    }

    public int getCurrentStatusCode() {
        return currentStatusCode;
    }

    public void setCurrentStatusCode(int currentStatusCode) {
        this.currentStatusCode = currentStatusCode;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getJobStatusDate() {
        return jobStatusDate;
    }

    public void setJobStatusDate(String jobStatusDate) {
        this.jobStatusDate = jobStatusDate;
    }

    public String getJobService() {
        return jobService;
    }

    public void setJobService(String jobService) {
        this.jobService = jobService;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getWorker() {
        return worker;
    }

    public void setWorker(User worker) {
        this.worker = worker;
    }

    public String getJobCreatedDate() {
        return jobCreatedDate;
    }

    public void setJobCreatedDate(String jobCreatedDate) {
        this.jobCreatedDate = jobCreatedDate;
    }

    public String getJobUpdatedDate() {
        return jobUpdatedDate;
    }

    public void setJobUpdatedDate(String jobUpdatedDate) {
        this.jobUpdatedDate = jobUpdatedDate;
    }

    public JobStatus[] getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(JobStatus[] statusHistory) {
        this.statusHistory = statusHistory;
    }

    public int getRating() {
        return Rating;
    }

    public void setRating(int rating) {
        Rating = rating;
    }
}
