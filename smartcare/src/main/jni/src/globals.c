//
// Created by nikolay on 20.01.16.
//
#include <jni.h>
#include "ontology/smartcare.h"
#include "globals.h"

jobject *GlobalMainActivityClassObject;
jobject *GlobalPatientHelpActivityClassObject;
JavaVM* JVM;
JNIEnv *envGlob;
jclass *GlobalMainActivityClass;
jclass *GlobalPatientHelpActivityClass;
jmethodID GlobalGetHelpRequestNotification;
sslog_node_t *GlobalNode;

sslog_individual_t *his_request_glob;
sslog_individual_t *his_response_glob;

jclass *class_question;
jclass *class_questionnaire;
jclass *class_answer;
jclass *class_answer_item;
jclass *class_demographic;
jclass *class_laboratory;
jclass *class_blood_pressure;
jclass *class_doctor_examination;

jmethodID questionnaire_constructor;
jmethodID add_question;
jmethodID question_constructor;
jmethodID answer_constructor;
jmethodID add_answer;
jmethodID add_subanswer;
jmethodID item_constructor;
jmethodID add_answer_item_i;

jmethodID demographic_constructor;
jmethodID laboratory_constructor;
jmethodID blood_pressure_constructor;
jmethodID doctor_examination_constructor;










