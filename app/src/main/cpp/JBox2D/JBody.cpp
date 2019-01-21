//
// Created by Israel on 1/17/2019.
//

#include <jni.h>

#include "Box2D/Box2D.h"

#include "JKittyCache.h"

extern "C" JNIEXPORT jint JNICALL
Java_KittyEngine_Engine_Physics_KPolygonShape_getMaxPolygonVertices(JNIEnv *env, jobject /* this */)
{
    return (jint)b2_maxPolygonVertices;
}

extern "C" JNIEXPORT jlong JNICALL
Java_KittyEngine_Engine_Physics_KBody_getNext(JNIEnv *env, jobject /* this */, jlong bodyPtr)
{
    b2Body* body = reinterpret_cast<b2Body*>(bodyPtr);

    return reinterpret_cast<jlong>(body->GetNext());
}

inline void rotateb2Vec2(b2Vec2& inOutVec2, float angle)
{
    float sn = sinf(angle);
    float cs = cosf(angle);
    inOutVec2 = b2Vec2(inOutVec2.x*cs - inOutVec2.y*sn, inOutVec2.x*sn + inOutVec2.y*cs);
};

extern "C" JNIEXPORT jlong JNICALL
Java_KittyEngine_Engine_Physics_KBody_createFixture(
        JNIEnv *env, jobject /* this */,
        jlong bodyPtr,
        jobject shape,
        jfloat relativePositionX,
        jfloat relativePositionY,
        jfloat relativeAngle,
        jint categoryBits,
        jint maskBits,
        jint groupIndex,
        jfloat density,
        jfloat friction,
        jfloat restitution,
        jboolean bSensor
        ) {
    enum ShapeType
    {
        CIRCLE = 0,
        POLYGON = 1
    };

    jobject shapeTypeObject = env->GetObjectField(shape, JKittyCache::s_KShape_m_type_FieldID);
    jint shapeTypeValue = env->GetIntField(shapeTypeObject, JKittyCache::s_KShape_Type_m_value_FieldID);

    b2FixtureDef fixtureDef;
    switch (shapeTypeValue)
    {
        case CIRCLE:
        {
            b2CircleShape* circle = new b2CircleShape();
            circle->m_p = b2Vec2(relativePositionX, relativePositionY);
            circle->m_radius = env->GetFloatField(shape, JKittyCache::s_KCircleShape_radius_FieldID);
            fixtureDef.shape = circle;
        } break;

        case POLYGON:
        {
            jobject verticesObj = env->GetObjectField(shape, JKittyCache::s_KPolygonShape_m_vertices_FieldID);
            jobjectArray * verticesOA = reinterpret_cast<jobjectArray*>(&verticesObj);
            // max vertices size of KPolygonShape is assumed to be b2_maxPolygonVertices
            jsize verticesSize = env->GetArrayLength(*verticesOA);

            b2Vec2 polygonVertices[b2_maxPolygonVertices];
            for (jsize i = 0; i < verticesSize; ++i) {
                jobject vertexObj = env->GetObjectArrayElement(*verticesOA, i);
                jfloat x = env->GetFloatField(vertexObj, JKittyCache::s_KVec2_x_FieldID);
                jfloat y = env->GetFloatField(vertexObj, JKittyCache::s_KVec2_y_FieldID);
                /* rotate and move vertex position because fixture vertices are relative to the b2Body
                * but polygon's vertices are relative to KPhysicsObject
                */
                b2Vec2 vertex = b2Vec2(x, y);
                rotateb2Vec2(vertex, relativeAngle); // rotate vertex
                polygonVertices[i] = vertex + b2Vec2(relativePositionX, relativePositionY);
            }

            b2PolygonShape* polygon = new b2PolygonShape();
            polygon->Set(polygonVertices, verticesSize);
            fixtureDef.shape = polygon;
        } break;

        default: break;
    }

    fixtureDef.filter.categoryBits = categoryBits;
    fixtureDef.filter.maskBits = maskBits;
    fixtureDef.filter.groupIndex = groupIndex;
    fixtureDef.density = density;
    fixtureDef.friction = friction;
    fixtureDef.restitution = restitution;
    fixtureDef.isSensor = bSensor;

    b2Body* body = reinterpret_cast<b2Body*>(bodyPtr);
    jlong fixturePtr = reinterpret_cast<jlong>(body->CreateFixture(&fixtureDef));
    delete fixtureDef.shape; // shape is cloned inside CreateFixture
    return fixturePtr;
}

extern "C" JNIEXPORT void JNICALL
Java_KittyEngine_Engine_Physics_KBody_destroyFixture(JNIEnv *env, jobject /* this */, jlong bodyPtr, jlong fixturePtr) {
    b2Body* body = reinterpret_cast<b2Body*>(bodyPtr);
    b2Fixture* fixture = reinterpret_cast<b2Fixture*>(fixturePtr);

    body->DestroyFixture(fixture);
}

extern "C" JNIEXPORT jlong JNICALL
Java_KittyEngine_Engine_Physics_KBody_transferFixture
(
        JNIEnv *env, jobject /* this */,
        jlong newBodyPtr,
        jlong oldFixturePtr,
        jobject shape,
        jfloat worldPositionX,
        jfloat worldPositionY,
        jfloat worldAngle) {
    b2Body* newBody = reinterpret_cast<b2Body*>(newBodyPtr);
    b2Fixture* oldFixture = reinterpret_cast<b2Fixture*>(oldFixturePtr);
    b2Body* oldBody = oldFixture->GetBody();

    enum ShapeType
    {
        CIRCLE = 0,
        POLYGON = 1
    };

    jobject shapeTypeObject = env->GetObjectField(shape, JKittyCache::s_KShape_m_type_FieldID);
    jint shapeTypeValue = env->GetIntField(shapeTypeObject, JKittyCache::s_KShape_Type_m_value_FieldID);

    b2FixtureDef fixtureDef;

    b2Vec2 relativePosition = b2Vec2(worldPositionX, worldPositionY) - newBody->GetPosition();
    switch (shapeTypeValue)
    {
        case CIRCLE:
        {
            b2CircleShape* circle = new b2CircleShape();

            circle->m_p = relativePosition;
            circle->m_radius = env->GetFloatField(shape, JKittyCache::s_KCircleShape_radius_FieldID);
            fixtureDef.shape = circle;
        } break;

        case POLYGON:
        {
            float relativeAngle = worldAngle - newBody->GetAngle();

            jobject verticesObj = env->GetObjectField(shape, JKittyCache::s_KPolygonShape_m_vertices_FieldID);
            jobjectArray * verticesOA = reinterpret_cast<jobjectArray*>(&verticesObj);
            // max vertices size of KPolygonShape is assumed to be b2_maxPolygonVertices
            jsize verticesSize = env->GetArrayLength(*verticesOA);

            b2Vec2 polygonVertices[b2_maxPolygonVertices];
            for (jsize i = 0; i < verticesSize; ++i) {
                jobject vertexObj = env->GetObjectArrayElement(*verticesOA, i);
                jfloat x = env->GetFloatField(vertexObj, JKittyCache::s_KVec2_x_FieldID);
                jfloat y = env->GetFloatField(vertexObj, JKittyCache::s_KVec2_y_FieldID);
                /* rotate and move vertex position because fixture vertices are relative to the b2Body
                * but polygon's vertices are relative to KPhysicsObject
                */
                b2Vec2 vertex = b2Vec2(x, y);
                rotateb2Vec2(vertex, relativeAngle); // rotate vertex
                polygonVertices[i] = vertex + relativePosition;
            }

            b2PolygonShape* polygon = new b2PolygonShape();
            polygon->Set(polygonVertices, verticesSize);
            fixtureDef.shape = polygon;
        } break;

        default: break;
    }

    jlong newFixturePtr = reinterpret_cast<jlong>(newBody->CreateFixture(&fixtureDef));
    delete fixtureDef.shape; // shape is cloned inside CreateFixture
    oldBody->DestroyFixture(oldFixture);

    return newFixturePtr;

}

extern "C" JNIEXPORT jlong JNICALL
Java_KittyEngine_Engine_Physics_KBody_getFixtureList(JNIEnv *env, jobject /* this */, jlong bodyPtr) {
    b2Body* body = reinterpret_cast<b2Body*>(bodyPtr);

    return reinterpret_cast<jlong>(body->GetFixtureList());
}

extern "C" JNIEXPORT jboolean JNICALL
Java_KittyEngine_Engine_Physics_KBody_isActive(JNIEnv *env, jobject /* this */, jlong bodyPtr) {
    b2Body* body = reinterpret_cast<b2Body*>(bodyPtr);
    return (jboolean)body->IsActive();
}

extern "C" JNIEXPORT jboolean JNICALL
Java_KittyEngine_Engine_Physics_KBody_isAwake(JNIEnv *env, jobject /* this */, jlong bodyPtr) {
    b2Body* body = reinterpret_cast<b2Body*>(bodyPtr);
    return (jboolean)body->IsAwake();
}

extern "C" JNIEXPORT jint JNICALL
Java_KittyEngine_Engine_Physics_KBody_getType(JNIEnv *env, jobject /* this */, jlong bodyPtr) {
    b2Body* body = reinterpret_cast<b2Body*>(bodyPtr);

    return (jint)body->GetType();
}

extern "C" JNIEXPORT void JNICALL
Java_KittyEngine_Engine_Physics_KBody_setTransform
(
        JNIEnv *env, jobject /* this */, jlong bodyPtr,
        jfloat positionX, jfloat positionY, jfloat angle)
        {
    b2Body* body = reinterpret_cast<b2Body*>(bodyPtr);
    body->SetTransform(b2Vec2(positionX, positionY), angle);
}

extern "C" JNIEXPORT jfloatArray JNICALL
Java_KittyEngine_Engine_Physics_KBody_getPosition(JNIEnv *env, jobject /* this */, jlong bodyPtr) {
    b2Body* body = reinterpret_cast<b2Body*>(bodyPtr);
    jfloatArray outPositionJFArr = env->NewFloatArray(2);
    env->SetFloatArrayRegion(outPositionJFArr, 0, 2, &body->GetPosition().x);
    return outPositionJFArr;
}

extern "C" JNIEXPORT jfloat JNICALL
Java_KittyEngine_Engine_Physics_KBody_getAngle(JNIEnv *env, jobject /* this */, jlong bodyPtr) {
    b2Body* body = reinterpret_cast<b2Body*>(bodyPtr);
    return (jfloat)body->GetAngle();
}

extern "C" JNIEXPORT void JNICALL
Java_KittyEngine_Engine_Physics_KBody_setLinearVelocity(JNIEnv *env, jobject /* this */, jlong bodyPtr, jfloat x, jfloat y) {
    b2Body* body = reinterpret_cast<b2Body*>(bodyPtr);
    body->SetLinearVelocity(b2Vec2(x, y));
}

extern "C" JNIEXPORT jobject JNICALL
Java_KittyEngine_Engine_Physics_KBody_getPhysicsObject(JNIEnv *env, jobject /* this */, jlong bodyPtr) {
    b2Body* body = reinterpret_cast<b2Body*>(bodyPtr);
    return (jobject)body->GetUserData();
}