package com.petrsu.cardiacare.smartcare.hisdocuments;

/**
 * Created by Iuliia Zavialova on 04.10.16.
 */

public class LaboratoryStudy {

    String organizationName ;
    String hemoglobin;
    String erythrocyte;
    String hematocrit;

    public LaboratoryStudy(String organizationName, String hemoglobin, String erythrocyte, String hematocrit) {
        this.organizationName = organizationName;
        this.hemoglobin = hemoglobin;
        this.erythrocyte = erythrocyte;
        this.hematocrit = hematocrit;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getHemoglobin() {
        return hemoglobin;
    }

    public void setHemoglobin(String hemoglobin) {
        this.hemoglobin = hemoglobin;
    }

    public String getErythrocyte() {
        return erythrocyte;
    }

    public void setErythrocyte(String erythrocyte) {
        this.erythrocyte = erythrocyte;
    }

    public String getHematocrit() {
        return hematocrit;
    }

    public void setHematocrit(String hematocrit) {
        this.hematocrit = hematocrit;
    }
}
