//
// Created by nikolay on 20.01.16.
//
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <jni.h>
#include "jni_utils.h"
#include "globals.h"

int init_global_instances(JNIEnv* env, jobject obj) {

    GlobalMainActivityClassObject = (jobject * )(*env)->NewGlobalRef(env, obj);
    if (NULL == GlobalMainActivityClassObject) { return -1; }

    jclass localMainActivityClass = (*env)->FindClass(env, "com/petrsu/cardiacare/smartcare/SmartCareLibrary");
    if (NULL == localMainActivityClass) { return -1; }
    GlobalMainActivityClass = (jclass * )(*env)->NewGlobalRef(env, localMainActivityClass);
    if (NULL == GlobalMainActivityClass) { return -1; }


    jclass _class_questionnaire = (*env)->FindClass(env, "com/petrsu/cardiacare/smartcare/servey/Questionnaire");
    if (_class_questionnaire == NULL) { return -1;  }
    class_questionnaire = (jclass * )(*env)->NewGlobalRef(env, _class_questionnaire);
    if (class_questionnaire == NULL) { return -1; }


    jclass _class_question = (*env)->FindClass(env, "com/petrsu/cardiacare/smartcare/servey/Question");
    if (_class_question == NULL) { return -1; }
    class_question = (jclass * )(*env)->NewGlobalRef(env, _class_question);
    if (NULL == class_question) { return -1; }

    jclass _class_answer = (*env)->FindClass(env, "com/petrsu/cardiacare/smartcare/servey/Answer");
    if (_class_answer == NULL) { return -1; }
    class_answer = (jclass * )(*env)->NewGlobalRef(env, _class_answer);
    if (NULL == class_answer) { return -1; }

    jclass _class_answer_item = (*env)->FindClass(env, "com/petrsu/cardiacare/smartcare/servey/AnswerItem");
    if (_class_answer_item == NULL) { return -1; }
    class_answer_item = (jclass * )(*env)->NewGlobalRef(env, _class_answer_item);
    if (NULL == class_answer_item) { return -1; }


        jclass _class_demographic = (*env)->FindClass(env, "com/petrsu/cardiacare/smartcare/hisdocuments/DemographicData");
        if (_class_demographic == NULL) { return -1; }
        class_demographic = (jclass * )(*env)->NewGlobalRef(env, _class_demographic);
        if (NULL == class_demographic) { return -1; }

            demographic_constructor = (*env)->GetMethodID(env, class_demographic, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
            if ( demographic_constructor == NULL) { return -1; }

        jclass _class_laboratory = (*env)->FindClass(env, "com/petrsu/cardiacare/smartcare/hisdocuments/LaboratoryStudy");
        if (_class_laboratory == NULL) { return -1; }
        class_laboratory = (jclass * )(*env)->NewGlobalRef(env, _class_laboratory);
        if (NULL == class_laboratory) { return -1; }

            laboratory_constructor = (*env)->GetMethodID(env, class_laboratory, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
            if ( laboratory_constructor == NULL) { return -1; }

        jclass _class_blood_pressure = (*env)->FindClass(env, "com/petrsu/cardiacare/smartcare/hisdocuments/ResultBloodPressure");
        if (_class_blood_pressure == NULL) { return -1; }
        class_blood_pressure = (jclass * )(*env)->NewGlobalRef(env, _class_blood_pressure);
        if (NULL == class_blood_pressure) { return -1; }

            blood_pressure_constructor = (*env)->GetMethodID(env, class_blood_pressure, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
            if ( blood_pressure_constructor == NULL) { return -1; }

        jclass _class_doctor_examination = (*env)->FindClass(env, "com/petrsu/cardiacare/smartcare/hisdocuments/ResultDoctorExamination");
        if (_class_doctor_examination == NULL) { return -1; }
        class_doctor_examination = (jclass * )(*env)->NewGlobalRef(env, _class_doctor_examination);
        if (NULL == class_doctor_examination) { return -1; }

            doctor_examination_constructor = (*env)->GetMethodID(env, class_doctor_examination, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
            if ( doctor_examination_constructor == NULL) { return -1; }

    questionnaire_constructor = (*env)->GetMethodID(env, class_questionnaire, "<init>", "(Ljava/lang/String;)V");
    if (questionnaire_constructor == NULL) { return -1; }

    add_question = (*env)->GetMethodID(env, class_questionnaire, "addQuestion", "(Lcom/petrsu/cardiacare/smartcare/servey/Question;)V");
    if (add_question == NULL) { return -1; }

    question_constructor = (*env)->GetMethodID(env, class_question, "<init>", "(Ljava/lang/String;Ljava/lang/String;)V");
    if (question_constructor == NULL) { return -1; }

    answer_constructor = (*env)->GetMethodID(env, class_answer, "<init>", "(Ljava/lang/String;Ljava/lang/String;)V");
    if (answer_constructor == NULL) { return -1; }

    add_answer = (*env)->GetMethodID(env, class_question, "setAnswer", "(Lcom/petrsu/cardiacare/smartcare/servey/Answer;)V");
    if (add_answer == NULL) { return -1; }

    add_subanswer = (*env)->GetMethodID(env, class_answer_item, "addSubAnswer", "(Lcom/petrsu/cardiacare/smartcare/servey/Answer;)V");
    if (add_subanswer == NULL) { return -1; }

    item_constructor = (*env)->GetMethodID(env, class_answer_item, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
    if ( item_constructor == NULL) { return -1; }

    add_answer_item_i = (*env)->GetMethodID(env,  class_answer, "addAnswerItem", "(Lcom/petrsu/cardiacare/smartcare/servey/AnswerItem;)V");
    if (add_answer_item_i == NULL) { return -1; }

}
int init_JVM_instance(JNIEnv* env){
    if((*env)->GetJavaVM(env, &JVM) != 0){
        return -1;
    }
    return 0;
}