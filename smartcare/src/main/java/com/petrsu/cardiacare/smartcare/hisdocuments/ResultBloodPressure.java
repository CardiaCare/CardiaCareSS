package com.petrsu.cardiacare.smartcare.hisdocuments;

/**
 * Created by Iuliia Zavialova on 04.10.16.
 */

public class ResultBloodPressure {

    String systolicPressure;
    String diastolicPressure;
    String pulse;

    public ResultBloodPressure(String systolicPressure, String diastolicPressure, String pulse) {
        this.systolicPressure = systolicPressure;
        this.diastolicPressure = diastolicPressure;
        this.pulse = pulse;
    }

    public String getSystolicPressure() {
        return systolicPressure;
    }

    public void setSystolicPressure(String systolicPressure) {
        this.systolicPressure = systolicPressure;
    }

    public String getDiastolicPressure() {
        return diastolicPressure;
    }

    public void setDiastolicPressure(String diastolicPressure) {
        this.diastolicPressure = diastolicPressure;
    }

    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }
}
