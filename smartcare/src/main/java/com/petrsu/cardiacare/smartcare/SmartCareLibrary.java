package com.petrsu.cardiacare.smartcare;

import com.petrsu.cardiacare.smartcare.hisdocuments.DemographicData;
import com.petrsu.cardiacare.smartcare.hisdocuments.LaboratoryStudy;
import com.petrsu.cardiacare.smartcare.hisdocuments.ResultBloodPressure;
import com.petrsu.cardiacare.smartcare.hisdocuments.ResultDoctorExamination;

/**
 * Created by Iuliia Zavialova on 28.06.16.
 */
public class SmartCareLibrary {
    // Native code part begin
    static {
        System.loadLibrary("smartcare_native");
    }

    public native long connectSmartSpace(String name, String ip, int port);

    public native void disconnectSmartSpace(long nodeDescriptor);

    public native String getQuestionnaire(long nodeDescriptor);
    public native String getQuestionnaireVersion(long nodeDescriptor,String questionnaireUri);
    public native String getQuestionnaireSeverUri(long nodeDescriptor,String questionnaireUri);

    public native String initPatient (long nodeDescriptor);
    public native String initPatientWithId (long nodeDescriptor,String patientUri);
    public native String initAuthRequest (long nodeDescriptor, String patientUri);
    public native String initLocation (long nodeDescriptor, String patientUri);
    public native void removeIndividual (long nodeDescriptor, String individualUri);
    public native void removeAlarm (long nodeDescriptor, String individualUri);
    public native String  sendAlarm(long nodeDescriptor, String patientUri);
    static public native int sendLocation(long nodeDescriptor, String patientUri, String locationUri ,String latitude, String longitude);
    static public native int sendFeedback(long nodeDescriptor, String patientUri, String feedbackUri, String feedbackDate);
    static public native String initFeedback();

    static public native int insertPersonName(long nodeDescriptor, String patientUri, String name);
    static public native int updatePersonName(long nodeDescriptor, String patientUri, String name);

    public native int getAuthResponce(long nodeDescriptor, String authUri);


    public native String getHis(long nodeDescriptor);

    public native String sendHisRequest(long nodeDescriptor, String hisUri, String patientUri,
                                      String hisDocumentType, String searchstring,
                                      String fieldName, String dateFrom,
                                      String dateTo);
    public native int removeHisRequest(long nodeDescriptor, String hisUri, String requrstUri);
    public native String getHisResponce(long nodeDescriptor,String hisRequestUri);
    public native String getHisDocument(long nodeDescriptor,String hisResponseUri);
    public native ResultBloodPressure getHisBloodPressureResult(long nodeDescriptor, String hisDocumentUri);
    public native DemographicData getHisDemographicData (long nodeDescriptor, String hisDocumentUri);
    public native LaboratoryStudy getHislaboratoryStudy(long nodeDescriptor, String hisDocumentUri);
    public native ResultDoctorExamination getHisDoctorExamination(long nodeDescriptor, String hisDocumentUri);
    public native String setHisId(long nodeDescriptor, String hisId, String patientId);
}
