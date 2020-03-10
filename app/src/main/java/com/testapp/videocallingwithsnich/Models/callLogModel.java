package com.testapp.videocallingwithsnich.Models;

public class callLogModel {
    String calldur;
    String callername;

    public callLogModel(String calldur, String callername) {
        this.calldur = calldur;
        this.callername = callername;
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
