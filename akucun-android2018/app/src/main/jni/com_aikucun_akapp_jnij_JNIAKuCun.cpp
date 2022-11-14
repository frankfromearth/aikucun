//
// Created by Micker on 2017/9/25.
//

#include<android/bitmap.h>
#include "com_aikucun_akapp_jnij_JNIAKuCun.h"
#include "common.h"
#include "encrypt.hpp"

JNIEXPORT jobject JNICALL Java_com_aikucun_akapp_jnij_JNIAKuCun_saveTextsToImage
        (JNIEnv *env, jclass classObject, jobject jobject1, jobjectArray jobjectArray1) {

    return saveTextsToImage(env,classObject,jobject1,jobjectArray1);
}

JNIEXPORT jobjectArray JNICALL Java_com_aikucun_akapp_jnij_JNIAKuCun_getDataFromImage
        (JNIEnv *env, jclass classObject, jobject jobject1) {

    return getSafeData(env,classObject,jobject1);
}

JNIEXPORT jstring JNICALL Java_com_aikucun_akapp_jnij_JNIAKuCun_getSafeImageName
        (JNIEnv *env, jclass classObject) {

    return env->NewStringUTF(SAFE_IMAGE_NAME);
}