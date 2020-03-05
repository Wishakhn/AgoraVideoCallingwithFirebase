package com.testapp.videocallingwithsnich;

public class CallModel {
    private String callerName;
    private String callerId;
    private String ReciverName;
    private String reciverId;
    private String callState;
    private String callDur;

    public CallModel() {
    }

    public CallModel(String callerName, String callerId, String reciverName, String reciverId, String callState, String callDur) {
        this.callerName = callerName;
        this.callerId = callerId;
        ReciverName = reciverName;
        this.reciverId = reciverId;
        this.callState = callState;
        this.callDur = callDur;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public String getReciverName() {
        return ReciverName;
    }

    public void setReciverName(String reciverName) {
        ReciverName = reciverName;
    }

    public String getReciverId() {
        return reciverId;
    }

    public void setReciverId(String reciverId) {
        this.reciverId = reciverId;
    }

    public String getCallState() {
        return callState;
    }

    public void setCallState(String callState) {
        this.callState = callState;
    }

    public String getCallDur() {
        return callDur;
    }

    public void setCallDur(String callDur) {
        this.callDur = callDur;
    }
}
