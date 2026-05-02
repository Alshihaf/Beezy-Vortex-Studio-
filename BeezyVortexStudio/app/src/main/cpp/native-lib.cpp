
#include <jni.h>
#include <string>
#include <android/native_window_jni.h>
#include "VulkanRenderer.h"

static VulkanRenderer* renderer = nullptr;

extern "C" JNIEXPORT jstring JNICALL
Java_com_beezyvortex_studio_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Vulkan Core Initialized";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT void JNICALL
Java_com_beezyvortex_studio_MainActivity_initVulkan(JNIEnv* env, jobject /* this */, jobject surface) {
    ANativeWindow* window = ANativeWindow_fromSurface(env, surface);
    if (window == nullptr) {
        return;
    }
    
    renderer = new VulkanRenderer(window);
    if (!renderer->init()) {
        delete renderer;
        renderer = nullptr;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_beezyvortex_studio_MainActivity_cleanupVulkan(JNIEnv* env, jobject /* this */) {
    if (renderer != nullptr) {
        delete renderer;
        renderer = nullptr;
    }
}
