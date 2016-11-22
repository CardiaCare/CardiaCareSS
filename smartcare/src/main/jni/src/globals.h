//
// Created by nikolay on 20.01.16.
//

#ifndef SMARTCARE_QUESTIONNAIRE_GLOBALS_H
#define SMARTCARE_QUESTIONNAIRE_GLOBALS_H

#endif //SMARTCARE_QUESTIONNAIRE_GLOBALS_H
#include "ontology/smartcare.h"
#include <jni.h>

extern jobject *GlobalMainActivityClassObject;
extern JavaVM* JVM;
extern jclass *GlobalMainActivityClass;
extern sslog_node_t *GlobalNode;


extern sslog_individual_t *his_request_glob;
extern sslog_individual_t *his_response_glob;

extern jclass *class_question;
extern jclass *class_questionnaire;
extern jclass *class_answer;
extern jclass *class_answer_item;
extern jclass *class_demographic;
extern jclass *class_laboratory;
extern jclass *class_blood_pressure;
extern jclass *class_doctor_examination;

extern jmethodID questionnaire_constructor;
extern jmethodID add_question;
extern jmethodID question_constructor;
extern jmethodID answer_constructor;
extern jmethodID add_answer;
extern jmethodID add_subanswer;
extern jmethodID item_constructor;
extern jmethodID add_answer_item_i;

extern jmethodID demographic_constructor;
extern jmethodID laboratory_constructor;
extern jmethodID blood_pressure_constructor;
extern jmethodID doctor_examination_constructor;

