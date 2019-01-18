//
// Created by Israel on 1/17/2019.
//

#include <jni.h>

#include "Box2D/Box2D.h"

extern "C" JNIEXPORT jlong JNICALL
Java_KittyEngine_Engine_Physics_KWorld2D_createWorld2D(JNIEnv *env, jobject /* this */)
{
    b2World* newb2World = new b2World(b2Vec2(0.f, 0.f));

    return reinterpret_cast<jlong>(newb2World);
}

extern "C" JNIEXPORT void JNICALL
Java_KittyEngine_Engine_Physics_KWorld2D_step
(
        JNIEnv *env, jobject /* this */,
        jlong worldPtr,
        jfloat timeStep,
        jint velocityIterations,
        jint positionIterations)
{
    b2World* world = reinterpret_cast<b2World*>(worldPtr);

    world->Step(timeStep, velocityIterations, positionIterations);
}

extern "C" JNIEXPORT jlong JNICALL
Java_KittyEngine_Engine_Physics_KWorld2D_getBodyList(JNIEnv *env, jobject /* this */, jlong worldPtr)
{
    b2World* world = reinterpret_cast<b2World*>(worldPtr);

    return reinterpret_cast<jlong>(world->GetBodyList());
}

extern "C" JNIEXPORT jfloatArray JNICALL
Java_KittyEngine_Engine_Physics_KWorld2D_getGravity(JNIEnv *env, jobject /* this */, jlong worldPtr)
{
    b2World* world = reinterpret_cast<b2World*>(worldPtr);

    b2Vec2 grav = world->GetGravity();

    jfloatArray outArr = env->NewFloatArray(2);
    env->SetFloatArrayRegion(outArr, 0, 2, &grav.x);
    return outArr;
}

extern "C" JNIEXPORT jlong JNICALL
Java_KittyEngine_Engine_Physics_KWorld2D_createBody
(
        JNIEnv *env, jobject /* this */,
        jlong worldPtr, jint physicsObjectGameIndex,
        jfloat positionX,
        jfloat positionY,
        jfloat angle,
        jfloat linearVelocityX,
        jfloat linearVelocityY,
        jfloat angularVelocity,
        jfloat linearDamping,
        jfloat angularDamping,
        jboolean bAllowSleep,
        jboolean bFixedRotation,
        jboolean bBullet,
        jint bodyType,
        jfloat gravityScale)
{
    b2World* world = reinterpret_cast<b2World*>(worldPtr);

    b2BodyDef bodyDef;
    bodyDef.userData = new int(physicsObjectGameIndex);
    bodyDef.position.Set(positionX, positionY);
    bodyDef.angle = angle;
    bodyDef.linearVelocity.Set(linearVelocityX, linearVelocityY);
    bodyDef.angularVelocity = angularVelocity;
    bodyDef.linearDamping = linearDamping;
    bodyDef.angularDamping = angularDamping;
    bodyDef.allowSleep = bAllowSleep;
    bodyDef.awake = true;
    bodyDef.fixedRotation = bFixedRotation;
    bodyDef.bullet = bBullet;
    bodyDef.type = (b2BodyType)bodyType;
    bodyDef.active = true;
    bodyDef.gravityScale = gravityScale;

    return reinterpret_cast<jlong>(world->CreateBody(&bodyDef));
}

extern "C" JNIEXPORT void JNICALL
Java_KittyEngine_Engine_Physics_KWorld2D_destroyBody
(
        JNIEnv *env, jobject /* this */,
        jlong worldPtr, jlong bodyPtr)
{
    b2World* world = reinterpret_cast<b2World*>(worldPtr);
    b2Body* body = reinterpret_cast<b2Body*>(bodyPtr);

    delete static_cast<int*>(body->GetUserData());
    world->DestroyBody(body);
}