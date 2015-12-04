package com.zebenzi.json.model.job;

import com.zebenzi.json.model.quote.Quote;
import com.zebenzi.json.model.user.User;

/**
 * Created by Vaugan.Nayagar on 2015/11/15.
 */
public class Job {
    int jobId;
    JobStatus status;
    User user;
    User worker;
    Quote quote;
    boolean isDeleted;
    String createdDate;
    String updatedDate;

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
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

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public boolean isJobInProgress()
    {
        try {
            String jobStatus = status.getStatusReason();

            if ((jobStatus.equalsIgnoreCase("Accepted")) ||
                    (jobStatus.equalsIgnoreCase("Pending Acceptence")) ||
                    (jobStatus.equalsIgnoreCase("No reply from worker")) ||
                    (jobStatus.equalsIgnoreCase("Waiting acceptence from other workers"))) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
