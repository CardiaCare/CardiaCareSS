//
// Created by nikolay on 13.01.16.
//
#ifdef	__cplusplus
extern "C" {
#endif

#ifndef SMARTCARE_QUESTIONNAIRE_AGENT_H
#define SMARTCARE_QUESTIONNAIRE_AGENT_H

#endif //SMARTCARE_VOLUNTEER_AGENT_H
#include "ontology/smartcare.h"
#include <jni.h>

char* generate_uri(char *);

/*******************************************/
int kp_remove_individual(long, char *);
int kp_remove_alarm(long nodeDescriptor, char *individual_uri);

int kp_init_patient(char **, long);

int kp_init_patient_with_uri(long, char *);

int kp_init_auth_request(long, char *, char**);

int kp_get_auth_responce(long, char *);

int kp_init_location(long, char *, char**);

int kp_send_location(long, char *, char *, char *, char *);

int kp_send_alarm(long, char *, char**);

int kp_send_feedback(long, char *, char *, char *);

int kp_insert_person_name(long, char *, char *);

int kp_update_person_name(long, char *, char *);

char* generate_uri(char *);
/*******************************************/

jobject kp_get_next_question(JNIEnv* , jobject *, sslog_node_t *, sslog_individual_t *, char* , char** );
jobject kp_get_first_question(JNIEnv* , jobject *, sslog_node_t *, sslog_individual_t *, char** );

sslog_individual_t * kp_get_questionnaire(sslog_node_t *node_ss, char**);

int kp_get_answer(JNIEnv* , jobject *, sslog_node_t *, sslog_individual_t*);
int kp_get_subanswer(JNIEnv* , jobject *, sslog_node_t *, sslog_individual_t* );
int get_subclasses(sslog_node_t *, char *, char** );
int check_answer_type(char *);
int kp_get_answer_items_by_type(JNIEnv* , jobject *, sslog_node_t *, sslog_individual_t *, char*  );
int kp_get_answer_items(JNIEnv* , jobject *, sslog_node_t *, sslog_individual_t *, char* );
int add_answer_item(JNIEnv* , jobject *, sslog_node_t *, sslog_individual_t *, char* );

int kp_init_feedback(char **uri);

long kp_connect_smartspace(char*, char*, int);
int kp_disconnect_smartpace(long);

#ifdef	__cplusplus
}
#endif