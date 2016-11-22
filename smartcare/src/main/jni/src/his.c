#include "his.h"
#include "globals.h"
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>
#include <string.h>
#define RDF_TYPE "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
/*
 * Генерация добавки для URI
 */

 int kp_set_his_id(long nodeDescriptor, char* his_id, char* patient_uri, char** patient_his_uri){

    sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
         return -1;
    }

    sslog_individual_t *patient = sslog_node_get_individual_by_uri(node, patient_uri);

    char* patient_his = sslog_node_get_property(node, patient,PROPERTY_HISID);
    printf("\npatient_his %s\n", patient_his);

    if (patient_his == NULL) {
        printf("\nError patient: %s\n", sslog_error_get_last_text());
        return -1;
    }
    *patient_his_uri = patient_his;
    return 0;
 }

int kp_get_his(long nodeDescriptor, char** his_uri){
       sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
       if (node == NULL){
           return -1;
       }

    //list_t* hises;

    list_t* hises = sslog_node_get_individuals_by_class(node, CLASS_HOSPITALINFORMATIONSYSTEM);

    if (list_is_null_or_empty(hises)) {
        printf("There are no such individuals.\n");
            __android_log_print(ANDROID_LOG_INFO, "his", "There are no such individuals.\n");
        return -1;
    }

    sslog_individual_t *his = NULL;
    list_head_t *pos = NULL;

    list_for_each(pos, &hises->links)
    {
        list_t *node = list_entry(pos, list_t, links);
        his = (sslog_individual_t *) node->data;
        sslog_triple_t *his_uri_from_triple = sslog_individual_to_triple (his);
        *his_uri  = his_uri_from_triple->subject;
        return 0;
    } 
}

int kp_send_his_request(long nodeDescriptor, char* his_uri, char* patient_uri,  char* his_document_type,
            char* search_string, char* field_name, char* date_from, char* date_to, char** request_uri){

    sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
        return -1;
    }

    sslog_individual_t *his = sslog_node_get_individual_by_uri(node, his_uri);
    sslog_individual_t *patient = sslog_node_get_individual_by_uri(node, patient_uri);

    char * _his_request_uri = sslog_generate_uri(CLASS_HISREQUEST);
    char *his_request_uri = generate_uri(_his_request_uri);

    sslog_individual_t *his_request = sslog_new_individual(CLASS_HISREQUEST, his_request_uri);
    his_request_glob = his_request;
    if (his_request == NULL) {
        return -1;
    }

    if (search_string != NULL){
        sslog_insert_property(his_request, PROPERTY_SEARCHSTRING, search_string);
    }
    if ( field_name != NULL){
        sslog_insert_property(his_request, PROPERTY_FIELDNAME, field_name);
    }
    if (date_from != NULL){
        sslog_insert_property(his_request, PROPERTY_DATEFROM, date_from);
    }

    if ( date_to != NULL){
        sslog_insert_property(his_request, PROPERTY_DATETO, date_to);
    }

    *request_uri = his_request_uri;
    sslog_node_insert_individual(node, his_request);

    //object property of request - REQUESTSDOCUMENT
    char* class_uri;
        if (strcmp(his_document_type, "http://oss.fruct.org/smartcare#DemographicData") == 0) {
            class_uri =  sslog_entity_get_uri (CLASS_DEMOGRAPHICDATA);
        } 
        else if (strcmp(his_document_type, "http://oss.fruct.org/smartcare#BloodPressureMeasurement") == 0){
                class_uri =  sslog_entity_get_uri (CLASS_BLOODPRESSUREMEASUREMENT);
        }
        else if (strcmp(his_document_type, "http://oss.fruct.org/smartcare#LaboratoryAnalysis") == 0){
                class_uri =  sslog_entity_get_uri (CLASS_LABORATORYANALYSIS);
        }
        else if (strcmp(his_document_type, "http://oss.fruct.org/smartcare#DoctorExamination") == 0){
                class_uri =  sslog_entity_get_uri (CLASS_DOCTOREXAMINATION);
        }
        else if (strcmp(his_document_type, "http://oss.fruct.org/smartcare#ECGMeasurement") == 0){
                class_uri =  sslog_entity_get_uri (CLASS_ECGMEASUREMENT);
        }
    char* pred_uri =  sslog_entity_get_uri (PROPERTY_REQUESTSDOCUMENT);
    sslog_triple_t *class_triple = sslog_new_triple_detached(
            class_uri,
            RDF_TYPE,
            "http://www.w3.org/2000/01/rdf-schema#Class",
            SS_RDF_TYPE_URI, SS_RDF_TYPE_URI);
    sslog_node_insert_triple(node, class_triple);

    sslog_triple_t *type_triple = sslog_new_triple_detached(
            his_request_uri,
            pred_uri,
            class_uri,
            SS_RDF_TYPE_URI, SS_RDF_TYPE_URI);
    sslog_node_insert_triple(node, type_triple);

    //object property of request - RELATESTO
    sslog_node_insert_property(node, his_request, PROPERTY_RELATESTO, patient);
    //object property of request - HASREQUEST
    sslog_node_insert_property(node, his, PROPERTY_HASREQUEST, his_request);

    return 0;

}
int kp_remove_his_request(long nodeDescriptor, char* his_uri, char* his_request_uri){

    sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
        return -1;
    }
    sslog_individual_t *his = sslog_node_get_individual_by_uri(node, his_uri);
    sslog_individual_t *his_request = sslog_node_get_individual_by_uri(node, his_request_uri);

    sslog_node_remove_property(node, his, PROPERTY_HASREQUEST, his_request);

    /*char* uri = "http://oss.fruct.org/smartcare#hasRequest";
    sslog_triple_t *class_triple = sslog_new_triple_detached(
                his_uri,
                uri,
                his_request_uri,
                SS_RDF_TYPE_URI, SS_RDF_TYPE_URI);

    sslog_node_remove_triple(node, class_triple);*/

    __android_log_print(ANDROID_LOG_INFO, "his", "removeHisRequest done");
    return 0;
}

int kp_get_his_response( long nodeDescriptor, char* his_request_uri, char** his_response_uri){

    sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
        return -1;
    }

    sslog_individual_t *his_request = sslog_node_get_individual_by_uri(node, his_request_uri);

    if(his_request == NULL) {
        printf(" no his_request\n");
        return -1;
    }

    sleep(4);

        sslog_individual_t *his_response = ( sslog_individual_t *) sslog_node_get_property(node,his_request,PROPERTY_HASRESPONSE);
        if(his_response == NULL) {
            printf(" no his_response\n");
            return -1;
        }
        char *uri;
        uri =  sslog_entity_get_uri (his_response);
        *his_response_uri = uri;
        __android_log_print(ANDROID_LOG_INFO, "his", "his_response_uri %s\n", uri);

        char *status;
        status = (char *) sslog_node_get_property(node, his_response, PROPERTY_STATUS);
        if (strcmp(status, "ERROR") == 0){
            printf("Error\n");
            return -1;
        }
        //__android_log_print(ANDROID_LOG_INFO, "his", "status %s\n", status);
}

int kp_get_his_document( long nodeDescriptor, char* his_response_uri, char** his_document_uri){
    sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
        return -1;
    }


   sslog_individual_t *his_response = sslog_node_get_individual_by_uri(node, his_response_uri);


    if(his_response == NULL) {
        printf(" no his_response\n");
        return -1;
    }
            sleep(1);


        sslog_individual_t * his_document = (sslog_individual_t *) sslog_node_get_property(node, his_response, PROPERTY_HASDOCUMENT);
        if (his_document != NULL){
            char* document_uri;
            document_uri  =  sslog_entity_get_uri (his_document);
            *his_document_uri  =  document_uri;

            __android_log_print(ANDROID_LOG_INFO, "his", "document_uri %s\n", document_uri);
        }


    return 0;
}


int kp_init_sbcr_his_response(){

    printf("kp_init_sbcr_his_response\n");
    extern void kp_sbcr_his_request(sslog_subscription_t *);
    void (*pRequestHandler)(sslog_subscription_t *) = &kp_sbcr_his_request;

    sslog_subscription_t *sbcrRequest = NULL;
    sbcrRequest = sslog_new_subscription(GlobalNode, true);

    list_t* properties = list_new();
    list_add_data(properties, PROPERTY_HASRESPONSE);
    sslog_sbcr_add_individual(sbcrRequest, his_request_glob, properties);

    if(sbcrRequest == NULL) {
        return -1;
    }

    sslog_sbcr_set_changed_handler(sbcrRequest, pRequestHandler);

    if(sslog_sbcr_subscribe(sbcrRequest) != SSLOG_ERROR_NO) {
        return -1;
    }

    printf("kp_init_sbcr_his_response end \n");

}
void kp_sbcr_his_request(sslog_subscription_t *request_sbcr){
    sslog_sbcr_changes_t *changes = sslog_sbcr_get_changes_last(request_sbcr);

    const list_t *new_response =
            sslog_sbcr_ch_get_triples(changes, SSLOG_ACTION_INSERT);

    if( new_response != NULL ){
        sslog_individual_t *his_response = (sslog_individual_t *) sslog_node_get_property(GlobalNode, his_request_glob, PROPERTY_HASRESPONSE);
        his_response_glob = his_response;
    }

}

int get_his_subclasses( long nodeDescriptor, char *uri, char** subclass){

    sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
        return -1;
    }


    sslog_triple_t *req_triple = sslog_new_triple_detached(
            uri,
            RDF_TYPE,
            SS_RDF_SIB_ANY,
            SS_RDF_TYPE_URI, SS_RDF_TYPE_URI);

    list_t *uris = sslog_node_query_triple(node, req_triple);
    sslog_free_triple(req_triple);
  
    list_head_t *iterator = NULL;
    char *answer_class_uri;
    list_for_each(iterator, &uris->links){
        list_t *list_node = list_entry(iterator, list_t, links);
        char *_answer_class_uri = (char *) ((sslog_triple_t*) list_node->data)->object;
        if(_answer_class_uri != NULL){
            answer_class_uri = _answer_class_uri;
            //TODO break;
            *subclass = answer_class_uri;
        }   
    }
    *subclass = answer_class_uri;
    list_free_with_nodes(uris, NULL);
}


int kp_get_his_laboratory_analysis(long nodeDescriptor, char* his_document_uri,
        char** createdAt, char** author,
        char** organizationName, char** hemoglobin, char** erythrocyte, char** hematocrit){


    sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
        return -1;
    }

    sslog_individual_t *his_document = sslog_node_get_individual_by_uri(node, his_document_uri);
    sslog_node_populate(node, his_document);

    *createdAt = (char *) sslog_get_property(his_document, PROPERTY_CREATEDAT);
    *author = (char *) sslog_get_property(his_document, PROPERTY_AUTHOR);

    *organizationName = (char *) sslog_get_property(his_document, PROPERTY_ORGANIZATIONNAME);
    *hemoglobin = (char *) sslog_get_property(his_document, PROPERTY_HEMOGLOBIN);
    *erythrocyte = (char *) sslog_get_property(his_document, PROPERTY_ERYTHROCYTE);
    *hematocrit = (char *) sslog_get_property(his_document, PROPERTY_HEMATOCRIT);

}


int kp_get_his_blood_pressure_measurement(long nodeDescriptor, char* his_document_uri,
        char** createdAt, char** author,
        char** systolicPressure, char** diastolicPressure, char** pulse){

    sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
        return -1;
    }

    sslog_individual_t *his_document = sslog_node_get_individual_by_uri(node, his_document_uri);
    sslog_node_populate(node, his_document);

    sslog_node_populate(node, his_document);

    *createdAt = (char *) sslog_get_property(his_document, PROPERTY_CREATEDAT);
    *author = (char *) sslog_get_property(his_document, PROPERTY_AUTHOR);

    *systolicPressure = (char *) sslog_get_property(his_document, PROPERTY_SYSTOLICPRESSURE);
    *diastolicPressure = (char *) sslog_get_property(his_document, PROPERTY_DIASTOLICPRESSURE);
    *pulse = (char *) sslog_get_property(his_document, PROPERTY_PULSE);

}

int kp_get_his_ECG_measurement(long nodeDescriptor, char* his_document_uri,
        char** createdAt, char** author,
        char** dataLocation){
    sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
        return -1;
    }

    sslog_individual_t *his_document = sslog_node_get_individual_by_uri(node, his_document_uri);
    sslog_node_populate(node, his_document);


    *createdAt = (char *) sslog_get_property(his_document, PROPERTY_CREATEDAT);
    *author = (char *) sslog_get_property(his_document, PROPERTY_AUTHOR);

    *dataLocation = (char *) sslog_get_property(his_document, PROPERTY_DATALOCATION);

}

int kp_get_his_demographic_data(long nodeDescriptor, char* his_document_uri,
        char** createdAt, char** author,
        char** name, char** surname, char** patronymic, char** birthDate, char** sex, char** residence, char** contactInformaiton){

    sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
        return -1;
    }

    sslog_individual_t *his_document = sslog_node_get_individual_by_uri(node, his_document_uri);
    sslog_node_populate(node, his_document);

    *createdAt = (char *) sslog_get_property(his_document, PROPERTY_CREATEDAT);
    *author = (char *) sslog_get_property(his_document, PROPERTY_AUTHOR);

    *name = (char *) sslog_get_property(his_document, PROPERTY_NAME);
    *surname = (char *) sslog_get_property(his_document, PROPERTY_SURNAME);
    *patronymic = (char *) sslog_get_property(his_document, PROPERTY_PATRONYMIC);
    *birthDate = (char *) sslog_get_property(his_document, PROPERTY_BIRTHDATE);
    *sex = (char *) sslog_get_property(his_document, PROPERTY_SEX);
    *residence = (char *) sslog_get_property(his_document, PROPERTY_RESIDENCE);
    *contactInformaiton = (char *) sslog_get_property(his_document, PROPERTY_CONTACTINFORMATION);

}

int kp_get_his_doctor_examination(long nodeDescriptor, char* his_document_uri,
        char** createdAt, char** author,
        char** examinationReason, char** visitOrder, char** diagnoses, char** medications, char** smoking, char** drinking, char** height, char** weight,  char** diseasePredisposition){

    sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
        return -1;
    }

    sslog_individual_t *his_document = sslog_node_get_individual_by_uri(node, his_document_uri);
    sslog_node_populate(node, his_document);

    *createdAt = (char *) sslog_get_property(his_document, PROPERTY_CREATEDAT);
    *author = (char *) sslog_get_property(his_document, PROPERTY_AUTHOR);

    *examinationReason = (char *) sslog_get_property(his_document, PROPERTY_EXAMINATIONREASON);
    *visitOrder = (char *) sslog_get_property(his_document, PROPERTY_VISITORDER);
    *diagnoses = (char *) sslog_get_property(his_document, PROPERTY_DIAGNOSES);
    *medications = (char *) sslog_get_property(his_document, PROPERTY_MEDICATIONS);
    *smoking = (char *) sslog_get_property(his_document, PROPERTY_SMOKING);
    *drinking = (char *) sslog_get_property(his_document, PROPERTY_DRINKING);
    *height = (char *) sslog_get_property(his_document, PROPERTY_HEIGHT);
    *weight = (char *) sslog_get_property(his_document, PROPERTY_WEIGHT);
    *diseasePredisposition = (char *) sslog_get_property(his_document, PROPERTY_DISEASEPREDISPOSITION);

}



/*


__android_log_print(ANDROID_LOG_INFO, "his", "2");
    sslog_subscription_t *response_subscription = sslog_new_subscription(node, false);
    list_t* properties = list_new();
    list_add_data(properties, PROPERTY_HASRESPONSE);

    __android_log_print(ANDROID_LOG_INFO, "his", "3");
    sslog_sbcr_add_individual(response_subscription, his_request, properties);

    if (sslog_sbcr_subscribe(response_subscription) != SSLOG_ERROR_NO) {
        printf("\nCan't subscribe.");
        return -1;
    }

    sslog_individual_t *his_response;
__android_log_print(ANDROID_LOG_INFO, "his", "4");
    while (
            sslog_sbcr_is_active(response_subscription) == true &&
                sslog_sbcr_wait(response_subscription) != SSLOG_ERROR_NO
          ){continue;}

    sslog_sbcr_changes_t *changes =
            sslog_sbcr_get_changes_last(response_subscription);


    const list_t *inserted_ind =
            sslog_sbcr_ch_get_individual_by_action(changes, SSLOG_ACTION_INSERT);
__android_log_print(ANDROID_LOG_INFO, "his", "5");


    list_head_t *list_walker = NULL;

    list_for_each(list_walker, &inserted_ind->links) {
        list_t *list_node = list_entry(list_walker, list_t, links);
        char *uri = (char *) list_node->data;
        sslog_individual_t *his_response = sslog_node_get_individual_by_uri(node, uri);

        __android_log_print(ANDROID_LOG_INFO, "his", "%s", uri);
        __android_log_print(ANDROID_LOG_INFO, "his", "%s", his_response);


        *his_response_uri = his_response;
        break;
    }
    sslog_sbcr_unsubscribe(response_subscription);
    list_free_with_nodes(inserted_ind, NULL);

__android_log_print(ANDROID_LOG_INFO, "his", "6");

    char *status;
    status = (char *) sslog_node_get_property(node, his_response, PROPERTY_STATUS);
    if (strcmp(status, "ERROR") == 0){
        printf("Error\n");
        return -1;
    }



        /*sslog_subscription_t *document_subscription = sslog_new_subscription(node, false);

    list_t* doc_properties = list_new();
    list_add_data(doc_properties, PROPERTY_HASDOCUMENT);

    __android_log_print(ANDROID_LOG_INFO, "his", "7");
    sslog_sbcr_add_individual(document_subscription, his_response, doc_properties);

    if (sslog_sbcr_subscribe(document_subscription) != SSLOG_ERROR_NO) {
        printf("\nCan't subscribe.");
        return -1;
    }

    sslog_individual_t *his_document;

    while (
            sslog_sbcr_is_active(document_subscription) == true &&
                sslog_sbcr_wait(document_subscription) != SSLOG_ERROR_NO
          ){continue;}

    sslog_sbcr_changes_t *doc_changes =
                sslog_sbcr_get_changes_last(document_subscription);

__android_log_print(ANDROID_LOG_INFO, "his", "8");
    const list_t *doc_inserted_ind =
            sslog_sbcr_ch_get_individual_by_action(doc_changes, SSLOG_ACTION_INSERT);

    list_head_t *doc_list_walker = NULL;
    list_for_each(doc_list_walker, &doc_inserted_ind->links) {
        list_t *doc_list_node = list_entry(doc_list_walker, list_t, links);
        char *doc_uri = (char *) doc_list_node->data;
        sslog_individual_t *his_document = sslog_node_get_individual_by_uri(node, doc_uri);
__android_log_print(ANDROID_LOG_INFO, "his", "9");
        *his_document_uri = his_document;
        break;
    }
    list_free_with_nodes(doc_inserted_ind, NULL);
    sslog_sbcr_unsubscribe(document_subscription);

    __android_log_print(ANDROID_LOG_INFO, "his", "10");
    //TODO несколько документов
    if (his_document != NULL){
        char* subclass;
        get_his_subclasses(node, his_document_uri , &subclass);
        *his_document_type = subclass;
        __android_log_print(ANDROID_LOG_INFO, "his", "11");
    }
*/
