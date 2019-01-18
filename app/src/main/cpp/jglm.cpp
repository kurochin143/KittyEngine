//
// Created by Israel on 1/9/2019.
//
#include <jni.h>
#include <cstring>
#include "glm/mat4x4.hpp"
#include "glm/gtc/matrix_transform.hpp"

extern "C" JNIEXPORT jfloatArray JNICALL
Java_glm_GLM_mat4MulVec4(
        JNIEnv *env,
        jobject /* this */,
        jfloatArray inMat4,
        jfloatArray inVec4) {

    jfloat* jMat4 = env->GetFloatArrayElements(inMat4, 0);
    glm::mat4 mat4;
    std::memcpy(&mat4[0][0], jMat4, sizeof(mat4));
    env->ReleaseFloatArrayElements(inMat4, jMat4, 0);

    jfloat* jVec4 = env->GetFloatArrayElements(inVec4, 0);
    glm::vec4 vec4;
    std::memcpy(&vec4[0], jVec4, sizeof(vec4));
    env->ReleaseFloatArrayElements(inVec4, jVec4, 0);

    glm::vec4 outVec4 = mat4 * vec4;

    jfloatArray outArray = env->NewFloatArray(4);
    env->SetFloatArrayRegion(outArray, 0, 4, &outVec4[0]);
    return outArray;
}

extern "C" JNIEXPORT jfloatArray JNICALL
Java_glm_GLM_mat4MulMat4(
        JNIEnv *env,
        jobject /* this */,
        jfloatArray inMat41,
        jfloatArray inMat42) {

    jfloat* jMat41 = env->GetFloatArrayElements(inMat41, 0);
    jfloat* jMat42 = env->GetFloatArrayElements(inMat42, 0);

    glm::mat4 mat41;
    std::memcpy(&mat41[0][0], jMat41, sizeof(mat41));

    glm::mat4 mat42;
    std::memcpy(&mat42[0][0], jMat42, sizeof(mat42));

    env->ReleaseFloatArrayElements(inMat41, jMat41, 0);
    env->ReleaseFloatArrayElements(inMat42, jMat42, 0);

    glm::mat4 outMat4 = mat41 * mat42;

    jfloatArray outArray = env->NewFloatArray(16);
    env->SetFloatArrayRegion(outArray, 0, 16, &outMat4[0][0]);
    return outArray;
}

extern "C" JNIEXPORT jfloatArray JNICALL
Java_glm_GLM_lookAtRH(
        JNIEnv *env,
        jobject /* this */,
        jfloatArray inEye,
        jfloatArray inCenter,
        jfloatArray inUp) {
    jfloat* jEye = env->GetFloatArrayElements(inEye, 0);
    jfloat* jCenter = env->GetFloatArrayElements(inCenter, 0);
    jfloat* jUp = env->GetFloatArrayElements(inUp, 0);

    glm::vec3 eye;
    std::memcpy(&eye[0], jEye, sizeof(glm::vec3));

    glm::vec3 center;
    std::memcpy(&center[0], jCenter, sizeof(glm::vec3));

    glm::vec3 up;
    std::memcpy(&up[0], jUp, sizeof(glm::vec3));

    env->ReleaseFloatArrayElements(inEye, jEye, 0);
    env->ReleaseFloatArrayElements(inCenter, jCenter, 0);
    env->ReleaseFloatArrayElements(inUp, jUp, 0);

    glm::mat4 outLookAtRH = glm::lookAtRH(eye, center, up);

    jfloatArray outArray = env->NewFloatArray(16);
    env->SetFloatArrayRegion(outArray, 0, 16, &outLookAtRH[0][0]);
    return outArray;
}

extern "C" JNIEXPORT jfloatArray JNICALL
Java_glm_GLM_rotateZ(
        JNIEnv *env,
        jobject /* this */,
        jfloatArray inMat4,
        jfloat inAngle) {
    jfloat* jMat4 = env->GetFloatArrayElements(inMat4, 0);
    glm::mat4 mat4;
    std::memcpy(&mat4[0][0], jMat4, sizeof(mat4));
    env->ReleaseFloatArrayElements(inMat4, jMat4, 0);

    glm::mat4 outMat4 = glm::rotate(mat4, inAngle, glm::vec3(0.f, 0.f, 1.f));

    jfloatArray outArray = env->NewFloatArray(16);
    env->SetFloatArrayRegion(outArray, 0, 16, &outMat4[0][0]);
    return outArray;
}

// TODO cache the ids to be more efficient
extern "C" JNIEXPORT jobject JNICALL
Java_glm_GLM_mat4MulVec4v(
        JNIEnv *env,
        jobject /* this */,
        jobject inMat4,
        jobject inVec4
        ) {
    jclass mat4cls = env->GetObjectClass(inMat4);
    jfieldID mMatID = env->GetFieldID(mat4cls, "mMat", "[B");
    jobject mMatInByteArray = env->GetObjectField(inMat4, mMatID);
    jdoubleArray* mMat = reinterpret_cast<jdoubleArray*>(&mMatInByteArray);

    glm::mat4 mat4;
    std::memcpy(&mat4[0][0], mMatInByteArray, sizeof(mat4));

    jclass vec4cls = env->GetObjectClass(inMat4);
    jfieldID vec4xID = env->GetFieldID(vec4cls, "x", "F");
    jfloat vec4x = env->GetFloatField(inVec4, vec4xID);
    jfieldID vec4yID = env->GetFieldID(vec4cls, "y", "F");
    jfloat vec4y = env->GetFloatField(inVec4, vec4yID);
    jfieldID vec4zID = env->GetFieldID(vec4cls, "z", "F");
    jfloat vec4z = env->GetFloatField(inVec4, vec4zID);
    jfieldID vec4wID = env->GetFieldID(vec4cls, "w", "F");
    jfloat vec4w = env->GetFloatField(inVec4, vec4wID);

    glm::vec4 outVec4 = mat4 * glm::vec4(vec4x, vec4y, vec4z, vec4w);

    jmethodID vec4constructorID = env->GetMethodID(vec4cls, "<init>", "()V");
    jobject outjVec4 = env->NewObject(vec4cls, vec4constructorID);
    env->SetFloatField(outjVec4, vec4xID, outVec4.x);
    env->SetFloatField(outjVec4, vec4yID, outVec4.y);
    env->SetFloatField(outjVec4, vec4zID, outVec4.z);
    env->SetFloatField(outjVec4, vec4wID, outVec4.w);

    return outjVec4;
}
