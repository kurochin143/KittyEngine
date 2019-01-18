//
// Created by Israel on 1/17/2019.
//

#include <jni.h>

#include "Box2D/Box2D.h"

extern "C" JNIEXPORT jlong JNICALL
Java_KittyEngine_Engine_Physics_KBody_getNext(JNIEnv *env, jobject /* this */, jlong bodyPtr)
{
    b2Body* body = reinterpret_cast<b2Body*>(bodyPtr);

    return reinterpret_cast<jlong>(body->GetNext());
}

extern "C" JNIEXPORT jint JNICALL
Java_KittyEngine_Engine_Physics_KBody_getPhysicsObjectGameIndex(JNIEnv *env, jobject /* this */, jlong bodyPtr)
{
    b2Body* body = reinterpret_cast<b2Body*>(bodyPtr);

    return *static_cast<int*>(body->GetUserData());
}