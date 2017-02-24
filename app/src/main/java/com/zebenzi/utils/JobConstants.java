package com.zebenzi.utils;

/**
 * Created by soorajpottekat on 11/02/17.
 */

public enum JobConstants {
    NEW_JOB(1),
    AWAITING_EST_RES_CUST(2),
    EST_DECLINED_CUST(3),
    EST_ACCEPT_SUP(4),
    AWAITING_QUO_RES_CUST(5),
    QUO_ACCEPT_CUS(6),
    QUO_DECLINED_CUST(7),
    JOB_IN_PROGRESS(8),
    JOB_FIN_AWAITING_RAT(9),
    JOB_FIN(10),
    JOB_CAN_CUST_EST(11),
    JOB_CAN_CUST_QUO(12),
    JOB_CAN_CUST_JOB_IN_PROG(13),
    JOB_CAN_SUP_EST(14),
    JOB_CAN_SUP_QUO(15),
    JOB_CAN_SUP_JOB_IN_PROGRESS(16),
    INVALID(17);

    private int value;

    private JobConstants(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static JobConstants getEnum(int x) {
        switch(x) {
            case 1:
                return NEW_JOB;
            case 2:
                return AWAITING_EST_RES_CUST;
            case 3:
                return EST_DECLINED_CUST;
            case 4:
                return EST_ACCEPT_SUP;
            case 5:
                return AWAITING_QUO_RES_CUST;
            case 6:
                return QUO_ACCEPT_CUS;
            case 7:
                return QUO_DECLINED_CUST;
            case 8:
                return JOB_IN_PROGRESS;
            case 9:
                return JOB_FIN_AWAITING_RAT;
            case 10:
                return JOB_FIN;
            case 11:
                return JOB_CAN_CUST_EST;
            case 12:
                return JOB_CAN_CUST_QUO;
            case 13:
                return JOB_CAN_CUST_JOB_IN_PROG;
            case 14:
                return JOB_CAN_SUP_EST;
            case 15:
                return JOB_CAN_SUP_QUO;
            case 16:
                return JOB_CAN_SUP_JOB_IN_PROGRESS;
            default:
                return INVALID;
        }
    }
}
