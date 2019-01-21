#include <jni.h>

#include "JKittyCache.h"

jclass JKittyCache::s_KShape_Cls;
jfieldID JKittyCache::s_KShape_m_type_FieldID;
jclass JKittyCache::s_KShape_Type_Cls;
jfieldID JKittyCache::s_KShape_Type_m_value_FieldID;
jclass JKittyCache::s_KCircleShape_Cls;
jfieldID JKittyCache::s_KCircleShape_radius_FieldID;
jclass JKittyCache::s_KPolygonShape_Cls;
jfieldID JKittyCache::s_KPolygonShape_m_vertices_FieldID;
jclass JKittyCache::s_KVec2_Cls;
jfieldID JKittyCache::s_KVec2_x_FieldID;
jfieldID JKittyCache::s_KVec2_y_FieldID;

jint JNI_OnLoad(JavaVM* vm, void*) {

    JNIEnv* env;
    if (vm->GetEnv((void **)&env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    JKittyCache::cache(env);

    return JNI_VERSION_1_6;
}

void JNI_OnUnload(JavaVM *vm, void*) {
    JNIEnv* env;
    if (vm->GetEnv((void **)&env, JNI_VERSION_1_6) != JNI_OK) {
        return;
    }

    JKittyCache::deleteGlobalRefs(env);

}