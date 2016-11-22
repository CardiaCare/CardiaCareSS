
/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include "ontology/smartcare.h"
#include <android/log.h>
#include <errno.h>

#include "agent.h"
#include "his.h"
#include "handlers.h"
#include "jni_utils.h"
#include "globals.h"

#define TAG "SS"
#define KP_NS_URI "http://oss.fruct.org/smartcare#"
#define KP_PREDICATE KP_NS_URI"sendAlarm"

/*
 *   Подключаемся к Интелектуальному пространству
 *  SmartSpace node initialization by hostname_, ip_,port
 */
JNIEXPORT jlong JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_connectSmartSpace( JNIEnv* env,
                                                  jobject thiz , jstring hostname, jstring ip, jint port) {

    const char *hostname_ = (*env)->GetStringUTFChars(env, hostname, NULL);
    if( hostname_ == NULL) {
        return -1;
    }
    const char *ip_ = (*env)->GetStringUTFChars(env, ip, NULL);
    if( ip_ == NULL ){
        return -1;
    }
    long node = kp_connect_smartspace( hostname_, ip_, port);

    if (node == -1) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "Can't join to SS");
        return -1;
    } else {
        __android_log_print(ANDROID_LOG_INFO, TAG, "KP joins to SS.");
    }
    /*
     * Initialize global references to java classes
     * to use'em in handlers etc.
     */
    if( -1 == init_global_instances(env, thiz) ) {
        return -1;
    }
    /*
     * Get Java Virtual Machine Instance
     * to use it in callbacks generally
     */
    if( -1 == init_JVM_instance(env) ){
        return -1;
    }
    /*
     * Return node descriptor
     * to use it further in subscriptions etc.
     */
    return (jlong) node;

}
/*
 *  Disconnect from smartspace
 *
 */
JNIEXPORT void JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_disconnectSmartSpace( JNIEnv* env,
                                                  jobject thiz , jlong nodeDescriptor){
    int result = kp_disconnect_smartspace(nodeDescriptor);

    if (result == -1)
        __android_log_print(ANDROID_LOG_INFO, TAG, "Node Error");
    else
        __android_log_print(ANDROID_LOG_INFO, TAG, "KP leaves SS...");
}

/***********************************************************************************************************/

/*
 * Удаляем произвольного индивида по uri
 *
*/
JNIEXPORT void JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_removeIndividual
( JNIEnv* env, jobject thiz , jlong nodeDescriptor, jstring individualUri)
{

    if (individualUri == NULL)
        return;
    const char * individual_uri= (*env)->GetStringUTFChars(env, individualUri, 0);



    int result = kp_remove_individual(nodeDescriptor, individual_uri);

    if (result == -1){
        __android_log_print(ANDROID_LOG_INFO, TAG, "Individual already removed");
    }
    else{
        __android_log_print(ANDROID_LOG_INFO, TAG, "remove Individual");
    }
}

JNIEXPORT void JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_removeAlarm
( JNIEnv* env, jobject thiz , jlong nodeDescriptor, jstring individualUri)
{
    const char * individual_uri= (*env)->GetStringUTFChars(env, individualUri, 0);

    int result = kp_remove_alarm(nodeDescriptor, individual_uri);

    if (result == -1){
        __android_log_print(ANDROID_LOG_INFO, TAG, "Node Error");
    }
    else{
        __android_log_print(ANDROID_LOG_INFO, TAG, "remove Individual");
    }
}
/*
 * Инициализируем индивид пациента (возвращает сгенерированный ЮРИ пациента)
 *
 */

JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_initPatient
        (JNIEnv *env, jobject thiz, jlong nodeDescriptor)
{
    //TODO: корректно выделить память!!!
    char*  patient_uri  = (char *) malloc(30);

    kp_init_patient(&patient_uri, nodeDescriptor);

    __android_log_print(ANDROID_LOG_INFO, TAG, "patient_uri: %s\n", patient_uri);

    return (*env)->NewStringUTF(env, patient_uri);
    free(patient_uri);
}

/*
 * Инициализируем индивид пациента с предопределенным URI
 *
 */

JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_initPatientWithId
        (JNIEnv *env, jobject thiz, jlong nodeDescriptor, jstring patientUri)
{

   const char * patient_uri= (*env)->GetStringUTFChars(env, patientUri, 0);

   kp_init_patient_with_uri(nodeDescriptor, patient_uri);

    __android_log_print(ANDROID_LOG_INFO, TAG, "patient_uri: %s\n", patient_uri);

    return (*env)->NewStringUTF(env, patient_uri);
    free(patient_uri);
}


JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_initAuthRequest
        (JNIEnv *env, jobject thiz, jlong nodeDescriptor, jstring patientUri)
{

    const char * patient_uri= (*env)->GetStringUTFChars(env, patientUri, 0);
    //TODO: корректно выделить память!!!
    char*  auth_request_uri  = (char *) malloc(30);

    kp_init_auth_request(nodeDescriptor, patient_uri, &auth_request_uri);
    return (*env)->NewStringUTF(env, auth_request_uri);
}

JNIEXPORT jint JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getAuthResponce
        (JNIEnv *env, jobject thiz, jlong nodeDescriptor, jstring authDescriptor)
{
    //get node from SmartSpace
    sleep(5);

    const char * auth_uri= (*env)->GetStringUTFChars(env, authDescriptor, 0);

    int result = kp_get_auth_responce(nodeDescriptor, auth_uri);
    if (result == -1) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "NOT PASSED");
        return -1;
    }
    else {
        __android_log_print(ANDROID_LOG_INFO, TAG, "PASSED");
        return 0;
    }
}


/*
 * Инициализируем индивид локации (возвращает сгенерированный ЮРИ локации)
 *
 */
JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_initLocation
        (JNIEnv *env, jobject thiz, jlong nodeDescriptor, jstring individualUri)
{
    //get individual from SmartSpace
    const char * individual_uri= (*env)->GetStringUTFChars(env, individualUri, 0);

    //TODO: корректно выделить память!!!
    char*  location_uri  = (char *) malloc(30);

    kp_init_location (nodeDescriptor, individual_uri, &location_uri);

    return (*env)->NewStringUTF(env, location_uri);
}
/*
 *
 * Отпраляем аларм  ТУДУ
 */
JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_sendAlarm
        ( JNIEnv* env, jobject thiz, jlong nodeDescriptor,jstring patientUri )
{
    const char *patient_uri = (*env)->GetStringUTFChars(env, patientUri, 0);

    char * alarm_uri = (char *) malloc(30);
    kp_send_alarm(nodeDescriptor, patient_uri, &alarm_uri);

    __android_log_print(ANDROID_LOG_INFO, TAG, "alarm_uri: %s\n", alarm_uri);
    return (*env)->NewStringUTF(env, alarm_uri);

}
/*
 *
 * Отпралвяем локацию. В разработке
 */
JNIEXPORT jint JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_sendLocation
            (JNIEnv *env, jobject thiz, jlong nodeDescriptor, jstring patientUri, jstring locationUri, jstring latitudeJ, jstring longitudeJ)
{
    const char * patient_uri= (*env)->GetStringUTFChars(env, patientUri, 0);
    const char * location_uri= (*env)->GetStringUTFChars(env, locationUri, 0);
    const char *latitude = (*env)->GetStringUTFChars(env, latitudeJ, 0);
    const char *longitude = (*env)->GetStringUTFChars(env, longitudeJ, 0);

    int result = kp_send_location(nodeDescriptor, patient_uri, location_uri, latitude, longitude);
    if (result == -1) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "Node Error");
        return -1;
    }
    else {
        __android_log_print(ANDROID_LOG_INFO, TAG, "Send Location");
        return 0;
    }
}

/*
 * Инициализируем индивид локации (возвращает сгенерированный ЮРИ локации)
 *
 */
JNIEXPORT jint JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_insertPersonName
        (JNIEnv *env, jobject thiz, jlong nodeDescriptor, jstring patientUri, jstring name)
{
    const char * patient_uri= (*env)->GetStringUTFChars(env, patientUri, 0);
    const char *new_name = (*env)->GetStringUTFChars(env, name, 0);

    int result = kp_insert_person_name( nodeDescriptor, patient_uri, new_name);
    if (result == -1) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "Node Error");
        return -1;
    }
    else {
        __android_log_print(ANDROID_LOG_INFO, TAG, "Insert person name");
        return 0;
    }
}

JNIEXPORT jint JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_updatePersonName
        (JNIEnv *env, jobject thiz, jlong nodeDescriptor, jstring patientUri, jstring name)
{
    const char * patient_uri= (*env)->GetStringUTFChars(env, patientUri, 0);
    const char *new_name = (*env)->GetStringUTFChars(env, name, 0);

    int result = kp_update_person_name(nodeDescriptor, patient_uri, new_name);

    if (result == -1) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "Node Error");
        return -1;
    }
    else {
        __android_log_print(ANDROID_LOG_INFO, TAG, "Update person name");
        return 0;
    }
}

/***********************************************************************************************************/


JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getQuestionnaire
        (JNIEnv* env, jobject thiz , jlong nodeDescriptor){

    sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL ){
        __android_log_print(ANDROID_LOG_INFO, TAG, "Node Error");
        return NULL;
    }
    char* questionnaire_uri;
    sslog_individual_t *questionnaire_ss =  kp_get_questionnaire(node, &questionnaire_uri);

    /**************/
    //jobject *questionnaire;
    //questionnaire = (*env)->NewObject(env, class_questionnaire, questionnaire_constructor, (*env)->NewStringUTF(env, questionnaire_uri) );

    //char* question_uri;
    //char* next_question_uri;

    //jobject first_question;
    //first_question = kp_get_first_question(env, questionnaire, node, questionnaire_ss, &next_question_uri);
    //question_uri = next_question_uri;
    //(*env)->CallVoidMethod(env, questionnaire, add_question, first_question);

    //jobject next_question;
   // while (next_question_uri != NULL) {
      //  next_question = kp_get_next_question(env, questionnaire, node, questionnaire_ss, question_uri, &next_question_uri);
    //    (*env)->CallVoidMethod(env, questionnaire, add_question, next_question);
  //      question_uri = next_question_uri;
//    }
    /**************/
    return (*env)->NewStringUTF(env, questionnaire_uri);
}

JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getQuestionnaireVersion
(JNIEnv *env, jobject thiz, jlong nodeDescriptor, jstring questionnireUri){

 sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
        return -1;
    }
const char * questionnire_uri= (*env)->GetStringUTFChars(env, questionnireUri, 0);

sslog_individual_t *questionnaire = sslog_node_get_individual_by_uri(node, questionnire_uri);

    char *version;
    version = (char *) sslog_node_get_property(node, questionnaire, PROPERTY_VERSION);

    return (*env)->NewStringUTF(env, version);
}

JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getQuestionnaireSeverUri
(JNIEnv *env, jobject thiz, jlong nodeDescriptor, jstring questionnireUri){

 sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
        return -1;
    }
const char * questionnire_uri= (*env)->GetStringUTFChars(env, questionnireUri, 0);

sslog_individual_t *questionnaire = sslog_node_get_individual_by_uri(node, questionnire_uri);

    char *server_uri;
    server_uri = (char *) sslog_node_get_property(node, questionnaire, PROPERTY_QUESTIONNAIREURI);
    return (*env)->NewStringUTF(env, server_uri);
}

/*
 *
 * Отправляем Feedback
 */
JNIEXPORT jint JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_sendFeedback
        ( JNIEnv* env, jobject thiz, jlong nodeDescriptor,jstring patientUri, jstring feedbackUri,jstring feedbackDate )
{
    const char *patient_uri = (*env)->GetStringUTFChars(env, patientUri, 0);
    const char *feedback_uri = (*env)->GetStringUTFChars(env, feedbackUri, 0);
    const char *feedback_date = (*env)->GetStringUTFChars(env, feedbackDate, 0);

    //char * alarm_uri = (char *) malloc(30);
    int result = kp_send_feedback(nodeDescriptor, patient_uri, feedback_uri, feedback_date);
    if (result == -1) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "Node Error");
        return -1;
    }
    else {
        __android_log_print(ANDROID_LOG_INFO, TAG, "Send Feedback");
        return 0;
    }
}


JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_initFeedback
        ( JNIEnv* env, jobject thiz)
{
    char *uri;

    int error = kp_init_feedback(&uri);

    if (error == -1){
         return NULL;
    }

    return (*env)->NewStringUTF(env, uri);

}


/***********************************************************************************************************
    ********************** HIS ************************************
***********************************************************************************************************/


JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getHis
        ( JNIEnv* env, jobject thiz, jlong nodeDescriptor )
{
    char* his_uri;

    int error = kp_get_his(nodeDescriptor, &his_uri);

    if (error == -1){
        return NULL;
    }


    return (*env)->NewStringUTF(env, his_uri);
}

JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_setHisId(JNIEnv* env, jobject thiz, jlong nodeDescriptor, jstring hisId, jstring patientId){


    char* his_id = (*env)->GetStringUTFChars(env, hisId, 0);
    char* patient_uri= (*env)->GetStringUTFChars(env, patientId, 0);
    char* patient_id_uri;
    int error  =  kp_set_his_id(nodeDescriptor, his_id, patient_uri, &patient_id_uri);

    if (error == -1){
        return NULL;
    }

    //__android_log_print(ANDROID_LOG_INFO, TAG, "patient_id_uri %s", patient_id_uri);

    return (*env)->NewStringUTF(env, patient_id_uri);

}


JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_sendHisRequest
        ( JNIEnv* env, jobject thiz, jlong nodeDescriptor,  jstring hisUri, jstring patientUri,
            jstring hisDocumentType, jstring searchstring,
            jstring fieldName, jstring dateFrom,
            jstring dateTo)
{

     char* his_uri = (*env)->GetStringUTFChars(env, hisUri, 0);
     char* patient_uri = (*env)->GetStringUTFChars(env, patientUri, 0);
     char* his_document_type = (*env)->GetStringUTFChars(env, hisDocumentType, 0);

     char* search_string;
     if (searchstring != NULL){
        search_string = (*env)->GetStringUTFChars(env, searchstring, 0);

      }
      else searchstring = NULL;

     char* field_name;
     if (field_name != NULL){
        field_name = (*env)->GetStringUTFChars(env, fieldName, 0);
     } else field_name = NULL;

     char* date_from;
     if (date_from != NULL){
        date_from = (*env)->GetStringUTFChars(env, dateFrom, 0);
     } else date_from = NULL;

     char* date_to;
     if (date_from != NULL){
        date_from =(*env)->GetStringUTFChars(env, dateTo, 0);
     } else date_from = NULL;


     char* his_request_uri;

     int error = kp_send_his_request(nodeDescriptor, his_uri,
                            patient_uri,
                            his_document_type,
                            search_string,
                            field_name,
                            date_from,
                            date_to,
                            &his_request_uri);

     if (error == -1){
        return NULL;
     }


      return (*env)->NewStringUTF(env, his_request_uri);
}
JNIEXPORT jint JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_removeHisRequest
        ( JNIEnv* env, jobject thiz, jlong nodeDescriptor,  jstring hisUri, jstring requestUri)
{
    char* his_uri = (*env)->GetStringUTFChars(env, hisUri, 0);
    char* his_request_uri = (*env)->GetStringUTFChars(env, requestUri, 0);
     __android_log_print(ANDROID_LOG_INFO, TAG, "removeHisRequest");
    int error =  kp_remove_his_request(nodeDescriptor, his_uri, his_request_uri);

     if (error == -1){
        return -1;
     }

     return 0;
}

JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getHisResponce
        ( JNIEnv* env, jobject thiz, jlong nodeDescriptor,  jstring hisRequestUri)
{
    char* his_request_uri = (*env)->GetStringUTFChars(env, hisRequestUri, 0);
     __android_log_print(ANDROID_LOG_INFO, TAG, "his_request_uri %s", his_request_uri);

    char* his_response_uri;

    int error = kp_get_his_response(nodeDescriptor, his_request_uri, &his_response_uri);
    if (error == -1)
        return NULL;


    if (his_response_uri == NULL)
            return NULL;

    return (*env)->NewStringUTF(env, his_response_uri);
}

JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getHisDocument
        ( JNIEnv* env, jobject thiz, jlong nodeDescriptor,  jstring hisResponseUri)
{
    char* his_response_uri = (*env)->GetStringUTFChars(env, hisResponseUri, 0);

    char* his_document_uri;

    int error = kp_get_his_document(nodeDescriptor, his_response_uri, &his_document_uri);

     //__android_log_print(ANDROID_LOG_INFO, TAG, "his_response_uri %s", his_response_uri);


    if (error == -1)
        return NULL;


    if (his_document_uri == NULL)
            return NULL;

     __android_log_print(ANDROID_LOG_INFO, TAG, "his_document_uri %s", his_document_uri);

    return (*env)->NewStringUTF(env, his_document_uri);
}




JNIEXPORT jobject JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getHisBloodPressureResult
        ( JNIEnv* env, jobject thiz, jlong nodeDescriptor,  jstring hisDocumentUri){
    char* createdAt;
    char* author;
    char* systolicPressure;
    char* diastolicPressure;
    char* pulse;

    char* his_document_uri = (*env)->GetStringUTFChars(env, hisDocumentUri, 0);

    kp_get_his_blood_pressure_measurement(nodeDescriptor, his_document_uri,&createdAt, &author,
        &systolicPressure, &diastolicPressure, &pulse);
    //printf("createdAt %s\nauthor %s\nsystolicPressure %s\ndiastolicPressure %s\npulse %s\n",
    //            createdAt, author, systolicPressure, diastolicPressure, pulse);


    jobject *blood_pressure;
    blood_pressure = (*env)->NewObject(env, class_blood_pressure, blood_pressure_constructor,
                                        (*env)->NewStringUTF(env, systolicPressure),
                                        (*env)->NewStringUTF(env, diastolicPressure),
                                        (*env)->NewStringUTF(env, pulse));
    return blood_pressure;

}

JNIEXPORT jobject JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getHisDemographicData
        ( JNIEnv* env, jobject thiz, jlong nodeDescriptor,  jstring hisDocumentUri){
            char* createdAt;
            char* author;
            char* name;
            char* surname;
            char* patronymic;
            char* birthDate;
            char* sex;
            char* residence;
            char* contactInformaiton;
    char* his_document_uri = (*env)->GetStringUTFChars(env, hisDocumentUri, 0);

    kp_get_his_demographic_data(nodeDescriptor, his_document_uri, &createdAt, &author,
        &name, &surname, &patronymic, &birthDate, &sex, &residence, &contactInformaiton);

     //printf("createdAt %s\nauthor %s\nname %s\nsurname %s\npatronymic %s\nbirthDate %s\nsex %s\nresidence %s\ncontactInformaiton %s\n",
    //       createdAt, author,name, surname, patronymic, birthDate, sex, residence, contactInformaiton);

    jobject *demographic_data;

    demographic_data = (*env)->NewObject(env, class_demographic, demographic_constructor,
                                            (*env)->NewStringUTF(env, name),
                                            (*env)->NewStringUTF(env, surname),
                                            (*env)->NewStringUTF(env, patronymic),
                                            (*env)->NewStringUTF(env, birthDate),
                                            (*env)->NewStringUTF(env, sex),
                                            (*env)->NewStringUTF(env, residence),
                                            (*env)->NewStringUTF(env, contactInformaiton));

    return demographic_data;
}

JNIEXPORT jobject JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getHislaboratoryStudy
        ( JNIEnv* env, jobject thiz, jlong nodeDescriptor,  jstring hisDocumentUri){
    char* createdAt;
    char* author;
    char* organizationName;
    char* hemoglobin;
    char* erythrocyte;
    char* hematocrit;

    char* his_document_uri = (*env)->GetStringUTFChars(env, hisDocumentUri, 0);

    kp_get_his_laboratory_analysis(nodeDescriptor, his_document_uri,
                                    &createdAt, &author,
                                    &organizationName, &hemoglobin, &erythrocyte, &hematocrit);

    //printf("createdAt %s\nauthor %s\norganizationName %s\nhemoglobin %s\nerythrocyte %s\nhematocrit %s\n",
    //        createdAt, author, organizationName, hemoglobin, erythrocyte, hematocrit);

    jobject *laboratory_analysis;

    laboratory_analysis = (*env)->NewObject(env, class_laboratory, laboratory_constructor,
                                            (*env)->NewStringUTF(env, organizationName),
                                            (*env)->NewStringUTF(env, hemoglobin),
                                            (*env)->NewStringUTF(env, erythrocyte),
                                            (*env)->NewStringUTF(env, hematocrit));

    return laboratory_analysis;


}

JNIEXPORT jobject JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getHisDoctorExamination
        ( JNIEnv* env, jobject thiz, jlong nodeDescriptor,  jstring hisDocumentUri){
    char* createdAt;
    char* author;
    char* examinationReason;
    char* visitOrder;
    char* diagnoses;
    char* medications;
    char* smoking;
    char* drinking;
    char* height;
    char* weight;
    char* diseasePredisposition;

    char* his_document_uri = (*env)->GetStringUTFChars(env, hisDocumentUri, 0);

    kp_get_his_doctor_examination(nodeDescriptor, his_document_uri,
        &createdAt, &author,
        &examinationReason, &visitOrder, &diagnoses, &medications, &smoking, &drinking, &height, &weight,  &diseasePredisposition);

    //printf("createdAt %s\nauthor %s\nexaminationReason %s\nvisitOrder %s\ndiagnoses %s\nmedications %s\nsmoking %s\ndrinking %s\nheight %s\nweight %s\ndiseasePredisposition",createdAt, author,
    //    examinationReason, visitOrder, diagnoses, medications, smoking, drinking, height, weight,  diseasePredisposition);

    jobject *doctor_examination;

    doctor_examination = (*env)->NewObject(env, class_doctor_examination, doctor_examination_constructor,
                                            (*env)->NewStringUTF(env, examinationReason),
                                            (*env)->NewStringUTF(env, visitOrder),
                                            (*env)->NewStringUTF(env, diagnoses),
                                            (*env)->NewStringUTF(env, medications),
                                            (*env)->NewStringUTF(env, smoking),
                                            (*env)->NewStringUTF(env, drinking),
                                            (*env)->NewStringUTF(env, height),
                                            (*env)->NewStringUTF(env, weight),
                                            (*env)->NewStringUTF(env, diseasePredisposition));

    return doctor_examination;

}
/*
int print_ecg_measurment_data(long nodeDescriptor, char *his_document_uri){
    char* createdAt;
    char* author;
    char* dataLocation;
    kp_get_his_ECG_measurement(nodeDescriptor, his_document_uri,
        &createdAt, &author,&dataLocation);
    printf("createdAt %s\nauthor %s\ndataLocation %s\n", createdAt, author,dataLocation);

}*/


