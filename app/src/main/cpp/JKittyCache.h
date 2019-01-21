//
// Created by Israel on 1/19/2019.
//

#include <jni.h>

#ifndef KITTYENGINE_JFIELDCACHE_H
#define KITTYENGINE_JFIELDCACHE_H

// @TODO make a map of caches?
class JKittyCache
{
public:
    static jclass s_KShape_Cls;
    static jfieldID s_KShape_m_type_FieldID;
    static jclass s_KShape_Type_Cls;
    static jfieldID s_KShape_Type_m_value_FieldID;
    static jclass s_KCircleShape_Cls;
    static jfieldID s_KCircleShape_radius_FieldID;
    static jclass s_KPolygonShape_Cls;
    static jfieldID s_KPolygonShape_m_vertices_FieldID;
    static jclass s_KVec2_Cls;
    static jfieldID s_KVec2_x_FieldID;
    static jfieldID s_KVec2_y_FieldID;

public:
    static void cache(JNIEnv* env)
    {
        jclass tempCls;

        tempCls = env->FindClass("KittyEngine/Engine/Physics/KShape");
        s_KShape_Cls = (jclass)env->NewGlobalRef(tempCls);
        env->DeleteLocalRef(tempCls);
        s_KShape_m_type_FieldID = env->GetFieldID(s_KShape_Cls, "m_type", "LKittyEngine/Engine/Physics/KShape$Type;");

        tempCls = env->FindClass("KittyEngine/Engine/Physics/KShape$Type");
        s_KShape_Type_Cls = (jclass)env->NewGlobalRef(tempCls);
        env->DeleteLocalRef(tempCls);
        s_KShape_Type_m_value_FieldID = env->GetFieldID(s_KShape_Type_Cls, "m_value", "I");

        tempCls = env->FindClass("KittyEngine/Engine/Physics/KCircleShape");
        s_KCircleShape_Cls = (jclass)env->NewGlobalRef(tempCls);
        env->DeleteLocalRef(tempCls);
        s_KCircleShape_radius_FieldID = env->GetFieldID(s_KCircleShape_Cls, "radius", "F");

        tempCls = env->FindClass("KittyEngine/Engine/Physics/KPolygonShape");
        s_KPolygonShape_Cls = (jclass)env->NewGlobalRef(tempCls);
        env->DeleteLocalRef(tempCls);
        s_KPolygonShape_m_vertices_FieldID = env->GetFieldID(s_KPolygonShape_Cls, "m_vertices", "[LKittyEngine/Math/KVec2;"); // array of objects

        tempCls = env->FindClass("KittyEngine/Math/KVec2");
        s_KVec2_Cls = (jclass)env->NewGlobalRef(tempCls);
        env->DeleteLocalRef(tempCls);
        s_KVec2_x_FieldID = env->GetFieldID(s_KVec2_Cls, "x", "F");
        s_KVec2_y_FieldID = env->GetFieldID(s_KVec2_Cls, "y", "F");

    }

    static void deleteGlobalRefs(JNIEnv* env)
    {
        env->DeleteGlobalRef(s_KShape_Cls);
        env->DeleteGlobalRef(s_KShape_Type_Cls);
        env->DeleteGlobalRef(s_KCircleShape_Cls);
        env->DeleteGlobalRef(s_KPolygonShape_Cls);
        env->DeleteGlobalRef(s_KVec2_Cls);
    }

};

#endif //KITTYENGINE_JFIELDCACHE_H
