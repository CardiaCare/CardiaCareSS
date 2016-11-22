#ifdef	__cplusplus
extern "C" {
#endif

#include "ontology/smartcare.h"
#include <jni.h>

#include <android/log.h>


 int kp_set_his_id(long nodeDescriptor, char* his_id, char* patient_uri, char** patient_his_uri);

int get_his_subclasses(long nodeDescriptor, char *uri, char** subclass);

int kp_get_his(long nodeDescriptor, char** his_uri);

int kp_send_his_request( long nodeDescriptor, char* his_uri, char* patient_uri,char* his_document_type, char* searchstring, char* fieldname, char* datefrom, char* dateto, char**);
int kp_remove_his_request(long nodeDescriptor, char* his_uri, char* his_request_uri);

int kp_get_his_response(long nodeDescriptor, char* his_request_uri, char** his_response_uri);
int kp_get_his_document( long nodeDescriptor, char* his_response_uri, char** his_document_uri);

int kp_get_his_laboratory_analysis(long nodeDescriptor,  char* his_document_uri,
        char** createdAt, char** author,
        char** organizationName, char** hemoglobin, char** erythrocyte, char** hematocrit);

int kp_get_his_blood_pressure_measurement(long nodeDescriptor, char* his_document_uri,
        char** createdAt, char** author,
        char** systolicPressure, char** diastolicPressure, char** pulse);

int kp_get_his_ECG_measurement(long nodeDescriptor, char* his_document_uri,
        char** createdAt, char** author,
        char** dataLocation);

int kp_get_his_demographic_data(long nodeDescriptor, char* his_document_uri,
        char** createdAt, char** author,
        char** name, char** surname, char** patronymic, char** birthDate, char** sex, char** residence, char** contactInformaiton);

int kp_get_his_doctor_examination(long nodeDescriptor, char* his_document_uri,
        char** createdAt, char** author,
        char** examinationReason, char** visitOrder, char** diagnoses, char** medications, char** smoking, char** drinking, char** height, char** weight,  char** diseasePredisposition);

void kp_sbcr_his_request(sslog_subscription_t *);
int kp_init_sbcr_his_response();


#ifdef	__cplusplus
}
#endif
