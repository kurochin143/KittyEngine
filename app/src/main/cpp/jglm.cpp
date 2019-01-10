//
// Created by Israel on 1/9/2019.
//
#include <jni.h>
#include <cstring>
#include "glm/mat4x4.hpp"

extern "C" JNIEXPORT jfloatArray JNICALL
Java_glm_GLM_mat4MulVec4(
        JNIEnv *env,
        jobject /* this */,
        jfloatArray inMat4,
        jfloatArray inVec4) {

    jfloat* jMat4 = env->GetFloatArrayElements(inMat4, 0);
    jfloat* jVec4 = env->GetFloatArrayElements(inVec4, 0);

    glm::mat4 mat4;
    std::memcpy(&mat4[0][0], jMat4, sizeof(mat4));

    glm::vec4 vec4;
    std::memcpy(&vec4[0], jVec4, sizeof(vec4));

    env->ReleaseFloatArrayElements(inMat4, jMat4, 0);
    env->ReleaseFloatArrayElements(inVec4, jVec4, 0);

    glm::vec4 outVec4 = mat4 * vec4;

    jfloatArray outArray = env->NewFloatArray(4);
    env->SetFloatArrayRegion(outArray, 0, 4, &outVec4[0]);
    return outArray;
}