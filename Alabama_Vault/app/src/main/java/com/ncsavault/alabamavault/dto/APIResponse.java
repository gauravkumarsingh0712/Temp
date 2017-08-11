package com.ncsavault.alabamavault.dto;

/**
 * Created by aqeeb.pathan on 15-04-2015.
 */
public class APIResponse {
    private String returnStatus;
    private long UserID;
    private String VerficationCode;
    private String emailID;
    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public long getUserID() {
        return UserID;
    }

    public void setUserID(long userID) {
        UserID = userID;
    }

    public String getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(String returnStatus) {
        this.returnStatus = returnStatus;
    }

    public String getVerficationCode() {
        return VerficationCode;
    }

    public void setVerficationCode(String verficationCode) {
        VerficationCode = verficationCode;
    }
}
