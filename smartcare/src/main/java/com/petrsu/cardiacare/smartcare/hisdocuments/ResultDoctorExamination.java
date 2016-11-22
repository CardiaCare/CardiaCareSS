package com.petrsu.cardiacare.smartcare.hisdocuments;

/**
 * Created by Iuliia Zavialova on 04.10.16.
 */

public class ResultDoctorExamination {

    String examinationReason;
    String visitOrder;
    String diagnoses;
    String medications;
    String smooking;
    String drinking;
    String height;
    String weight;
    String diseasePredisposition;

    public ResultDoctorExamination(String examinationReason, String visitOrder, String diagnoses, String medications, String smooking, String drinking, String height, String weight, String diseasePredisposition) {
        this.examinationReason = examinationReason;
        this.visitOrder = visitOrder;
        this.diagnoses = diagnoses;
        this.medications = medications;
        this.smooking = smooking;
        this.drinking = drinking;
        this.height = height;
        this.weight = weight;
        this.diseasePredisposition = diseasePredisposition;
    }

    public String getExaminationReason() {
        return examinationReason;
    }

    public void setExaminationReason(String examinationReason) {
        this.examinationReason = examinationReason;
    }

    public String getVisitOrder() {
        return visitOrder;
    }

    public void setVisitOrder(String visitOrder) {
        this.visitOrder = visitOrder;
    }

    public String getMedications() {
        return medications;
    }

    public void setMedications(String medications) {
        this.medications = medications;
    }

    public String getDiagnoses() {
        return diagnoses;
    }

    public void setDiagnoses(String diagnoses) {
        this.diagnoses = diagnoses;
    }

    public String getSmooking() {
        return smooking;
    }

    public void setSmooking(String smooking) {
        this.smooking = smooking;
    }

    public String getDrinking() {
        return drinking;
    }

    public void setDrinking(String drinking) {
        this.drinking = drinking;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDiseasePredisposition() {
        return diseasePredisposition;
    }

    public void setDiseasePredisposition(String diseasePredisposition) {
        this.diseasePredisposition = diseasePredisposition;
    }
}
