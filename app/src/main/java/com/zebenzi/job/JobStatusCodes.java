package com.zebenzi.job;

/**
 * Created by Vaugan.Nayagar on 2015/09/29.
 */
public enum JobStatusCodes {

    PENDING_ACCEPTANCE("Pending Acceptence", 1),
    WORKER_ACCEPTED("Accepted", 2),
    WORKER_COMPLETED("Completed", 3),
    WORKER_DECLINED("Declined", 4),
    CUSTOMER_CANCELLED("Cancelled By User", 5),
    WORKER_NO_REPLY("No reply from worker", 6),
    OTHER_WORKERS_WAITING_ACCEPTANCE("Waiting acceptence from other workers", 7),
    DELETED("Deleted", 8),
    WORKERS_NOT_AVAILABLE("No Workers available", 9),
    ACCEPTED_SMS_SEND("Accepted sms Send", 10),
    WORKER_COMPLETED_UNRATED("Completed_Unrated", 11);

    private final String description;
    private final int code;

        private JobStatusCodes(String name, int id) {
            this.description = name;
           this.code = id;
        }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }

    public static JobStatusCodes findByName(String statusName){
        for(JobStatusCodes f : values()){
            if( f.getDescription().equalsIgnoreCase(statusName)){
                return f;
            }
        }
        return null;
    }

    public static JobStatusCodes findByCode(int code){
        for(JobStatusCodes f : values()){
            if( f.getCode() == code) {
                return f;
            }
        }
        return null;
    }
}

