//
// Created by Israel on 1/18/2019.
//

#include <jni.h>

#include "Box2D/Box2D.h"

extern "C" JNIEXPORT jlong JNICALL
Java_KittyEngine_Engine_Physics_KFixture_getNext(JNIEnv *env, jobject /* this */, jlong fixturePtr) {
    b2Fixture* fixture = reinterpret_cast<b2Fixture*>(fixturePtr);

    return reinterpret_cast<jlong>(fixture->GetNext());
}