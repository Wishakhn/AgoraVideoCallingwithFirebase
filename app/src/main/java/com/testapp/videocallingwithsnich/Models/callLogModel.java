package com.testapp.videocallingwithsnich.Models;

public class callLogModel {
    String calldur;
    String callername;
    String callType;


    public callLogModel(String calldur, String callername) {
        this.calldur = calldur;
        this.callername = callername;
    }

    public callLogModel(String calldur, String callername, String callType) {
        this.calldur = calldur;
        this.callername = callername;
        this.callType = callType;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCalldur() {
        return calldur;
    }

    public void setCalldur(String calldur) {
        this.calldur = calldur;
    }

    public String getCallername() {
        return callername;
    }

    public void setCallername(String callername) {
        this.callername = callername;
    }
}
