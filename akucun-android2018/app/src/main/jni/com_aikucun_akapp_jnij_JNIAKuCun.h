/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_aikucun_akapp_jnij_JNIAKuCun */

#ifndef _Included_com_aikucun_akapp_jnij_JNIAKuCun
#define _Included_com_aikucun_akapp_jnij_JNIAKuCun
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_aikucun_akapp_jnij_JNIAKuCun
 * Method:    saveTextsToImage
 * Signature: (Landroid/content/Context;[Ljava/lang/String;)Landroid/graphics/Bitmap;
 */
JNIEXPORT jobject JNICALL Java_com_aikucun_akapp_jnij_JNIAKuCun_saveTextsToImage
  (JNIEnv *, jclass, jobject, jobjectArray);

/*
 * Class:     com_aikucun_akapp_jnij_JNIAKuCun
 * Method:    getDataFromImage
 * Signature: (Landroid/content/Context;)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_com_aikucun_akapp_jnij_JNIAKuCun_getDataFromImage
  (JNIEnv *, jclass, jobject);

/*
 * Class:     com_aikucun_akapp_jnij_JNIAKuCun
 * Method:    getSafeImageName
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_aikucun_akapp_jnij_JNIAKuCun_getSafeImageName
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
