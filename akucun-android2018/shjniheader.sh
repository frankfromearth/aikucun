#!bin/bash

WORK_PATH="$PWD"
echo $WORK_PATH

cd app/build/intermediates/classes/debug

javah -classpath ./:/Users/micker/Documents/\=\=tools/android-sdk-macosx/platforms/android-L/android.jar -jni com.aikucun.akapp.jnij.JNIAKuCun

cp com_aikucun_akapp_jnij_JNIAKuCun.h ../../../../src/main/jni/

echo "done"
