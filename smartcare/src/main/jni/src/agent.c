//
// Created by Iuliia Zavialova on 09.03.16.
//

#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <unistd.h>

#include <android/log.h>

#include <jni.h>

#include "agent.h"
#include "handlers.h"
#include "globals.h"

#define TAG "SS"

#define RDF_TYPE "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
/*
 * Connecting to smartspace
 */
long kp_connect_smartspace(char *hostname_,char *ip_, int port){
    sslog_node_t *node;
    sslog_init();

    //__android_log_print(ANDROID_LOG_INFO, TAG, "sslog_init");

    node = sslog_new_node("KP_Volunteer", hostname_, ip_, port);
    /*
     * Delete this line when test
     */
    GlobalNode = node;

    register_ontology(node);

    //__android_log_print(ANDROID_LOG_INFO, TAG, "register_ontology");

    if (sslog_node_join(node) != SSLOG_ERROR_NO) {
        //__android_log_print(ANDROID_LOG_INFO, TAG, "Can't join to SS");
        return -1;
    }
    else{
        //__android_log_print(ANDROID_LOG_INFO, TAG, "KP joins to SS.");
        return (long) node;
    }
}

int kp_disconnect_smartspace(long nodeDescriptor){
    sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
        return -1;
    }

    //sslog_sbcr_unsubscribe_all(node, true);
    sslog_node_leave(node);
    sslog_shutdown();
    return 0;
}

static int uri_index = 0;
char* generate_uri(char *uri){

    uri_index++;
    int result;
    char *newstring;
    char *c;
    result = asprintf(&newstring, "%s%d", uri, uri_index);
    if (result == -1) newstring = NULL;
    return newstring;
}

/*******************************************/
int kp_remove_alarm(long nodeDescriptor, char *individual_uri){
    sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
        return -1;
    }

    sslog_triple_t *req_triple = sslog_new_triple_detached(
            SS_RDF_SIB_ANY,
            "http://oss.fruct.org/smartcare#sendAlarm",
            individual_uri,
            SS_RDF_TYPE_URI, SS_RDF_TYPE_URI);
    sslog_node_remove_triple(node, req_triple);

    sslog_triple_t *req_triple2 = sslog_new_triple_detached(
            SS_RDF_SIB_ANY,
            "http://oss.fruct.org/smartcare#responseToAlarm",
            individual_uri,
            SS_RDF_TYPE_URI, SS_RDF_TYPE_URI);
    sslog_node_remove_triple(node, req_triple2);

    sslog_individual_t *ind = sslog_node_get_individual_by_uri(node, individual_uri);

    if (ind == NULL){
        return -1;
    }
    sslog_node_remove_individual_with_local(node, ind);
    return 0;
}


int kp_remove_individual(long nodeDescriptor, char *individual_uri){
    sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
        return -1;
    }

    sslog_individual_t *ind = sslog_node_get_individual_by_uri(node, individual_uri);

    if (ind == NULL){
        return -1;
    }

    sslog_node_remove_individual_with_local(node, ind);
    return 0;
}

/*
* Patient
*/
int kp_init_patient(char **uri, long nodeDescriptor){
    sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
        //__android_log_print(ANDROID_LOG_INFO, TAG, "Node Error");
        return -1;
    }

    char * _patient_uri = sslog_generate_uri(CLASS_PATIENT);
    char *patient_uri = generate_uri(_patient_uri);

    sslog_individual_t *patient = sslog_new_individual(CLASS_PATIENT, patient_uri);

    //free(patient_uri);

    if (patient == NULL) {
        //__android_log_print(ANDROID_LOG_INFO, TAG, "Error patient: %s", sslog_error_get_last_text());
        return -1;
    }

    //insert individual to SmartSpace
    sslog_node_insert_individual(node, patient);

    *uri = patient_uri;

    return 0;
}

int kp_init_patient_with_uri(long nodeDescriptor, char *patient_uri){
   sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
        //__android_log_print(ANDROID_LOG_INFO, TAG, "Node Error");
        return -1;
    }
    sslog_individual_t *patient = sslog_new_individual(CLASS_PATIENT, patient_uri);

    //free(patient_uri);

    if (patient == NULL) {
            //__android_log_print(ANDROID_LOG_INFO, TAG, "Error patient: %s", sslog_error_get_last_text());
        return -1;
    }

    //insert individual to SmartSpace
    sslog_node_insert_individual(node, patient);

    return 0;
}

int kp_init_auth_request(long nodeDescriptor, char *patient_uri, char** uri){

    return 0;
}

int kp_get_auth_responce(long nodeDescriptor, char *auth_uri){
   return 0;
}

int kp_init_location(long nodeDescriptor, char *individual_uri, char** uri ){
    //get node from SmartSpace
    sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
        //__android_log_print(ANDROID_LOG_INFO, TAG, "Node Error");
        return -1;
    }

    sslog_individual_t *ind = sslog_node_get_individual_by_uri(node, individual_uri);


    //Create Location individual with latitude and longitude properties
    char * _location_uri = sslog_generate_uri(CLASS_LOCATION);
    char *location_uri = generate_uri(_location_uri);

    //__android_log_print(ANDROID_LOG_INFO, TAG, "location_uri %s.", location_uri);

    sslog_individual_t *location = sslog_new_individual(CLASS_LOCATION, location_uri);

    if (location == NULL) {
        //__android_log_print(ANDROID_LOG_INFO, TAG, "Error location: %s", sslog_error_get_last_text());
        return -1;
    }

    // Location properties initialization
    sslog_insert_property(location, PROPERTY_LAT, "0.0");
    sslog_insert_property(location, PROPERTY_LONG, "0.0");

    // insert individual with properties to SmartSpace
    sslog_node_insert_individual(node, location);
    // insert location property to specific individual
    sslog_node_insert_property(node, ind, PROPERTY_HASPERSLOCATION, location);

    *uri = location_uri;
    return 0;
}

int kp_send_location(long nodeDescriptor, char *patient_uri, char *location_uri, char *latitude, char *longitude){
    //get node from SmartSpace
    sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
        //__android_log_print(ANDROID_LOG_INFO, TAG, "Node Error");
        return -1;
    }

    //get patient and location from SmartSpace
    sslog_individual_t *patient = sslog_node_get_individual_by_uri(node, patient_uri);
    sslog_individual_t *location = sslog_node_get_individual_by_uri(node, location_uri);

    //update in SmartSpace Location's property: latitude
    char *latitude_from_ss = sslog_node_get_property(node, location,  PROPERTY_LAT);
    //__android_log_print(ANDROID_LOG_INFO, TAG, "latitude_from_ss %s",latitude_from_ss);
    //__android_log_print(ANDROID_LOG_INFO, TAG, "latitude %s",latitude);
    sslog_node_update_property(node, location,  PROPERTY_LAT, latitude_from_ss,latitude);

    //update in SmartSpace Location's property: longitude
    char *longitude_from_ss = sslog_node_get_property(node, location,  PROPERTY_LONG);
    //__android_log_print(ANDROID_LOG_INFO, TAG, "longitudee_from_ss %s",longitude_from_ss);
    //__android_log_print(ANDROID_LOG_INFO, TAG, "longitude %s",longitude);
    sslog_node_update_property(node, location,  PROPERTY_LONG, longitude_from_ss,longitude);

    return 0;
}

int kp_send_alarm(long nodeDescriptor, char *patient_uri, char **alarm_uri_ind){

    //get node from SmartSpace
    sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
        //__android_log_print(ANDROID_LOG_INFO, TAG, "Node Error");
        return -1;
    }

    //get patient from SmartSpace

    sslog_individual_t *patient = sslog_node_get_individual_by_uri(node, patient_uri);

    //create alarm
    char * _alarm_uri = sslog_generate_uri(CLASS_ALARM);
    char *alarm_uri = generate_uri(_alarm_uri);

    sslog_individual_t *alarm = sslog_new_individual(CLASS_ALARM, alarm_uri);
    //__android_log_print(ANDROID_LOG_INFO, TAG, "alarm_uri %s.", alarm_uri);

    if (alarm == NULL) {
        //__android_log_print(ANDROID_LOG_INFO, TAG, "Error alarm: %s", sslog_error_get_last_text());
        return -1;
    }

    sslog_insert_property(alarm, PROPERTY_ALARMSTATUS, "Fibrillation");

    //insert individual with properties to SmartSpace
    sslog_node_insert_individual(node, alarm);

    //insert property, that connect alarm with patient
    sslog_node_insert_property(node, patient, PROPERTY_SENDALARM, alarm);
    *alarm_uri_ind = alarm_uri;
    return 0;
}

int kp_insert_person_name(long nodeDescriptor, char *patient_uri, char *new_name){
    //get node from SmartSpace
    sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
        //__android_log_print(ANDROID_LOG_INFO, TAG, "Node Error");
        return -1;
    }

    //get patient from SmartSpace
    sslog_individual_t *patient = sslog_node_get_individual_by_uri(node, patient_uri);

    // properties initialization
    const char *previous_name = (const char *) sslog_node_get_property(node, patient, PROPERTY_NAME);
    if (previous_name == NULL){
        sslog_node_insert_property(node, patient, PROPERTY_NAME, new_name);
    }
    else{
        sslog_node_update_property(node, patient, PROPERTY_NAME, previous_name, new_name);
    }
    return 0;

}

int kp_update_person_name(long nodeDescriptor, char *patient_uri, char *new_name){
    //get node from SmartSpace
    sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
        //__android_log_print(ANDROID_LOG_INFO, TAG, "Node Error");
        return -1;
    }

    //get patient from SmartSpace
    sslog_individual_t *patient = sslog_node_get_individual_by_uri(node, patient_uri);

    // properties initialization
    const char *previous_name = (const char *) sslog_node_get_property(node, patient, PROPERTY_NAME);

    if (previous_name  == NULL) {
        //__android_log_print(ANDROID_LOG_INFO, TAG,"No name");
    }
    sslog_node_update_property(node, patient, PROPERTY_NAME, previous_name, new_name);
    return 0;
}


/*******************************************/

sslog_individual_t* kp_get_questionnaire(sslog_node_t *node, char **questionnaire_uri){
    list_t* questionnaires;

    questionnaires = sslog_node_get_individuals_by_class(node, CLASS_QUESTIONNAIRE);

    if (list_is_null_or_empty(questionnaires) == true) {
        __android_log_print(ANDROID_LOG_INFO, TAG,"There are no such individuals.");
        return NULL;
    }
    sslog_individual_t *questionnaire_ss = NULL;
    list_head_t *pos = NULL;

    char* uri;
    list_for_each(pos, &questionnaires->links)
    {
        list_t *node = list_entry(pos, list_t, links);
        questionnaire_ss = (sslog_individual_t *) node->data;
        sslog_triple_t *questionnaire_uri_from_triple = sslog_individual_to_triple (questionnaire_ss);
        uri  = questionnaire_uri_from_triple->subject;
        *questionnaire_uri = uri;
        return questionnaire_ss;
    }

}

jobject kp_get_first_question(JNIEnv* env, jobject *questionnaire, sslog_node_t *node_ss, sslog_individual_t *questionnaire_ss, char** next_question_uri){

    sslog_individual_t *first_question = (sslog_individual_t *) sslog_node_get_property(node_ss, questionnaire_ss, PROPERTY_FIRSTQUESTION);
    if(first_question == NULL){
        return -1;
    }

    char *question_uri;
    sslog_triple_t *question_uri_from_triple = sslog_individual_to_triple (first_question);
    question_uri  =  question_uri_from_triple->subject;

    sslog_node_populate(node_ss, first_question);

    char *description_ss;
    description_ss = (char *) sslog_get_property(first_question, PROPERTY_DESCRIPTION);

    sslog_individual_t * next_question = (sslog_individual_t *) sslog_get_property(first_question, PROPERTY_NEXTQUESTION);
    if (next_question != NULL){
        sslog_triple_t * next_question_uri_from_triple = sslog_individual_to_triple (next_question);
        *next_question_uri  =  next_question_uri_from_triple->subject;
    }
    /**************/
    jobject *question;
    question = (*env)->NewObject(env, class_question, question_constructor, (*env)->NewStringUTF(env, question_uri), (*env)->NewStringUTF(env, description_ss));
    kp_get_answer(env, question, node_ss, first_question);
    /**************/

    return question;
}

jobject kp_get_next_question(JNIEnv* env, jobject *questionnaire, sslog_node_t *node_ss, sslog_individual_t *questionnaire_ss, char* question_uri, char** next_question_uri){

    sslog_individual_t *question_ss = sslog_node_get_individual_by_uri(node_ss,  question_uri);

    sslog_node_populate(node_ss, question_ss);

    char *description_ss;
    description_ss = (char *) sslog_get_property(question_ss, PROPERTY_DESCRIPTION);

    sslog_individual_t * next_question = (sslog_individual_t *) sslog_get_property(question_ss, PROPERTY_NEXTQUESTION);
    if (next_question != NULL){
        sslog_triple_t * next_question_uri_from_triple = sslog_individual_to_triple (next_question);
        *next_question_uri  =  next_question_uri_from_triple->subject;
    }
    else *next_question_uri  = NULL;

    /**************/
    jobject *question;
    question = (*env)->NewObject(env, class_question, question_constructor, (*env)->NewStringUTF(env, question_uri), (*env)->NewStringUTF(env, description_ss));
    kp_get_answer(env, question, node_ss, question_ss);
    /**************/

    return question;
}

int kp_get_answer(JNIEnv* env, jobject *question, sslog_node_t *node_ss, sslog_individual_t* question_ss){

    sslog_individual_t *answer_ss;
    answer_ss = (sslog_individual_t *)  sslog_node_get_property (node_ss, question_ss, PROPERTY_HASANSWER);

    sslog_node_populate(node_ss, answer_ss);

        char *subclass_name_uri = NULL;
        char *subclass_name = NULL;
    char* answer_uri = sslog_entity_get_uri(answer_ss);
        get_subclasses(node_ss, answer_uri, &subclass_name_uri);

        __android_log_print(ANDROID_LOG_INFO, TAG, "kp_get_answer");
        /**************/
        jobject *answer;
        answer = (*env)->NewObject(env, class_answer, answer_constructor, (*env)->NewStringUTF(env, answer_uri), (*env)->NewStringUTF(env, subclass_name_uri));
        kp_get_answer_items_by_type(env, answer, node_ss, answer_ss, subclass_name_uri);
        (*env)->CallVoidMethod(env, question, add_answer, answer);
        /**************/

        subclass_name_uri = NULL;
        answer = NULL;
        sslog_remove_individual(answer_ss);
    return 0;
}

int kp_get_subanswer(JNIEnv* env, jobject *answer_item, sslog_node_t *node_ss, sslog_individual_t* item){
    char* uri = sslog_entity_get_uri(item);
    sslog_triple_t *req_triple = sslog_new_triple_detached(
            uri,
            "http://oss.fruct.org/smartcare#subAnswer",
            SS_RDF_SIB_ANY,
            SS_RDF_TYPE_URI, SS_RDF_TYPE_URI);

    list_t *uris = sslog_node_query_triple(node_ss, req_triple);
    sslog_free_triple(req_triple);

    list_head_t *iterator = NULL;
    char *answer_class_uri;
    list_for_each(iterator, &uris->links)
    {
        list_t *list_node = list_entry(iterator, list_t, links);
        char *_answer_class_uri = (char *) ((sslog_triple_t *) list_node->data)->object;
        if (_answer_class_uri != NULL) {
            answer_class_uri = _answer_class_uri;
            sslog_individual_t *answer_ss = sslog_node_get_individual_by_uri(node_ss,
                                                                             answer_class_uri);
            sslog_node_populate(node_ss, answer_ss);

            char *subclass_name_uri = NULL;
            char *answer_uri = sslog_entity_get_uri(answer_ss);
            get_subclasses(node_ss, answer_uri, &subclass_name_uri);

            __android_log_print(ANDROID_LOG_INFO, TAG, "kp_get_subanswer");
            /**************/
            jobject *answer;
            answer = (*env)->NewObject(env, class_answer, answer_constructor,
                                       (*env)->NewStringUTF(env, answer_uri),
                                       (*env)->NewStringUTF(env, subclass_name_uri));
            kp_get_answer_items_by_type(env, answer, node_ss, answer_ss, subclass_name_uri);
            (*env)->CallVoidMethod(env, answer_item, add_subanswer, answer);
            /**************/

            sslog_remove_individual(answer_ss);
            subclass_name_uri = NULL;
        }
    }
    list_free_with_nodes(uris, NULL);
    return 0;
}

int get_subclasses(sslog_node_t *node, char *uri, char** subclass){
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
        }
        if (check_answer_type(answer_class_uri) == 0){
            break;
        }
    }
    *subclass = answer_class_uri;
    list_free_with_nodes(uris, NULL);
}

int check_answer_type(char *answer_class){
    if (strcmp(answer_class,(char *) sslog_class_to_triple (CLASS_TEXT)->subject) == 0
        || strcmp(answer_class,(char *) sslog_class_to_triple (CLASS_SINGLECHOISE)->subject) == 0
        || strcmp(answer_class,(char *) sslog_class_to_triple (CLASS_MULTIPLECHOISE)->subject) == 0
        || strcmp(answer_class,(char *) sslog_class_to_triple (CLASS_DICHOTOMOUS)->subject) == 0
        || strcmp(answer_class,(char *) sslog_class_to_triple (CLASS_BIPOLARQUESTION)->subject) == 0
        || strcmp(answer_class,(char *) sslog_class_to_triple (CLASS_GUTTMANSCALE)->subject) == 0
        || strcmp(answer_class,(char *) sslog_class_to_triple (CLASS_LIKERTSCALE)->subject) == 0
            ){
        return 0;
    }else
        return -1;
}

int kp_get_answer_items_by_type(JNIEnv* env, jobject *answer, sslog_node_t *node_ss, sslog_individual_t *answer_ss, char*  subclass_name){
    if (strcmp(subclass_name,(char *) sslog_class_to_triple (CLASS_SINGLECHOISE)->subject) == 0){
          kp_get_answer_items(env, answer ,node_ss, answer_ss, "singleItem");
        return 0;
    }

    if (strcmp(subclass_name,(char *) sslog_class_to_triple (CLASS_MULTIPLECHOISE)->subject) == 0){
         kp_get_answer_items(env, answer,node_ss, answer_ss, "multipleItem");
        return 0;
    }
    if (strcmp(subclass_name,(char *) sslog_class_to_triple (CLASS_DICHOTOMOUS)->subject) == 0){
        kp_get_answer_items(env, answer,node_ss, answer_ss, "dichotomousItem");
        return 0;
    }

    if (strcmp(subclass_name,(char *) sslog_class_to_triple (CLASS_GUTTMANSCALE)->subject) == 0){
        kp_get_answer_items(env, answer,node_ss, answer_ss, "rangingItem");
        return 0;
    }

    if (strcmp(subclass_name,(char *) sslog_class_to_triple (CLASS_LIKERTSCALE)->subject) == 0){
        kp_get_answer_items(env, answer,node_ss, answer_ss, "rangingItem");
        return 0;
    }

    if (strcmp(subclass_name,(char *) sslog_class_to_triple (CLASS_BIPOLARQUESTION)->subject) == 0){
        kp_get_answer_items(env, answer,node_ss, answer_ss, "leftBipolar");
        kp_get_answer_items(env, answer,node_ss, answer_ss, "rightBipolar");
        return 0;
    }

    return 0;
}


int kp_get_answer_items(JNIEnv* env, jobject *answer, sslog_node_t *node_ss, sslog_individual_t *answer_ss, char* answer_type){
    //__android_log_print(ANDROID_LOG_INFO, TAG, "kp_get_answer_items");
    sslog_triple_t *answer_uri_from_triple = sslog_individual_to_triple (answer_ss);
    char* answer_uri = answer_uri_from_triple->subject;

    char* rdf_property = NULL;
    char* rdf_ontology = "http://oss.fruct.org/smartcare#";
    int result = asprintf(&rdf_property, "%s%s", rdf_ontology, answer_type);
    if (result == -1) rdf_property = NULL;

    sslog_triple_t *req_triple = sslog_new_triple_detached(
            answer_uri,
            rdf_property,
            SS_RDF_SIB_ANY,
            SS_RDF_TYPE_URI, SS_RDF_TYPE_URI);

    list_t *uris = sslog_node_query_triple(node_ss, req_triple);
    sslog_free_triple(req_triple);

    list_head_t *iterator = NULL;
    char *item_uri;
    list_for_each(iterator, &uris->links){
        list_t *list_node = list_entry(iterator, list_t, links);
        char *_item_uri = (char *) ((sslog_triple_t*) list_node->data)->object;
        if(_item_uri != NULL){
            item_uri = _item_uri;
        }
        sslog_individual_t *item = sslog_node_get_individual_by_uri(node_ss,  item_uri);
        sslog_node_populate(node_ss, item);
        add_answer_item(env, answer, node_ss, item,  answer_type);
    }
    list_free_with_nodes(uris, NULL);
}

int add_answer_item(JNIEnv* env, jobject *answer, sslog_node_t *node_ss, sslog_individual_t *item, char* type){

    //__android_log_print(ANDROID_LOG_INFO, TAG, "add_answer_item");
    sslog_triple_t *answer_uri_from_triple = sslog_individual_to_triple (item);
    char* answer_item_uri = answer_uri_from_triple->subject;
    char* item_score = (char *) sslog_get_property(item, PROPERTY_ITEMSCORE);
    char* item_text = (char *) sslog_get_property(item, PROPERTY_ITEMTEXT);
    sslog_individual_t *subitem = ( sslog_individual_t *) sslog_get_property(item, PROPERTY_SUBANSWER);

    /**************/
    jobject *answer_item;
    answer_item = (*env)->NewObject(env, class_answer_item,  item_constructor,
        (*env)->NewStringUTF(env, answer_item_uri),
        (*env)->NewStringUTF(env, item_score),
        (*env)->NewStringUTF(env, item_text));

    (*env)->CallVoidMethod(env, answer, add_answer_item_i, answer_item);
    /**************/
     //subanswer
    if (subitem != NULL)
        kp_get_subanswer(env, answer_item, node_ss, item);
    return 0;
}

int kp_send_feedback(long nodeDescriptor, char *patient_uri, char *feedback_uri, char *feedback_date){
    //get node from SmartSpace
    sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
        //__android_log_print(ANDROID_LOG_INFO, TAG, "Node Error");
        return -1;
    }
    //get patient and feeddback from SmartSpace
    sslog_individual_t *patient = sslog_node_get_individual_by_uri(node, patient_uri);

    sslog_individual_t *feedback = sslog_new_individual(CLASS_FEEDBACK, feedback_uri);

    if (feedback == NULL) {
        return -1;
    }


   sslog_insert_property(feedback, PROPERTY_FEEDBACKDATE, feedback_date);
   sslog_node_insert_individual(node, feedback);
   sslog_node_insert_property(node, patient, PROPERTY_HASFEEDBACK, feedback);

    //update in SmartSpace Patient's property: feedback_date
    //char *feedback_from_ss = sslog_node_get_property(node, patient, PROPERTY_FEEDBACKDATE);
    //sslog_node_update_property(node, patient,  PROPERTY_FEEDBACKDATE, feedback_from_ss,feedback_date);
    return 0;
}

/*
* Patient
*/
int kp_init_feedback(char **uri){

    char * _feedback_uri = sslog_generate_uri(CLASS_FEEDBACK);
    char *feedback_uri = generate_uri(_feedback_uri);

    *uri = feedback_uri;

    return 0;
}




