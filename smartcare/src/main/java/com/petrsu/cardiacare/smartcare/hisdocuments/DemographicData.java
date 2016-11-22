package com.petrsu.cardiacare.smartcare.hisdocuments;

/**
 * Created by Iuliia Zavialova on 04.10.16.
 */

public class DemographicData {

    String patientName;
    String surname;
    String patronymic;
    String birthDate;
    String sex;
    String residence;
    String contactInformation;

    public DemographicData(String patientName, String surname, String patronymic, String birthDate, String sex, String residence, String contactInformation) {
        this.patientName = patientName;
        this.surname = surname;
        this.patronymic = patronymic;
        this.birthDate = birthDate;
        this.sex = sex;
        this.residence = residence;
        this.contactInformation = contactInformation;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public String getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }
}
