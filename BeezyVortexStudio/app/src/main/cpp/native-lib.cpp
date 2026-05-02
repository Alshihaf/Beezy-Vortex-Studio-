
#include <jni.h>
#include <string>
#include <android/native_window_jni.h>
#include <android/log.h>
#include "VulkanRenderer.h"

#define LOG_TAG "JNI_Bridge"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Global pointer to our renderer
static VulkanRenderer* renderer = nullptr;

extern "C" JNIEXPORT jstring JNICALL
Java_com_beezyvortex_studio_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    // Check if Vulkan has been initialized by checking the renderer pointer
    if (renderer != nullptr) {
        std::string hello = "Vulkan Core Initialized";
        return env->NewStringUTF(hello.c_str());
    } else {
        std::string hello = "Error: Vulkan Not Initialized";
        return env->NewStringUTF(hello.c_str());
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_beezyvortex_studio_MainActivity_initVulkan(JNIEnv* env, jobject /* this */, jobject surface) {
    if (surface == nullptr) {
        LOGE("Surface is null!");
        return;
    }
    ANativeWindow* nativeWindow = ANativeWindow_fromSurface(env, surface);
    if (nativeWindow == nullptr) {
        LOGE("Failed to get native window from surface");
        return;
    }

    if (renderer == nullptr) {
        renderer = new VulkanRenderer();
        try {
            renderer->init(nativeWindow);
        } catch (const std::exception& e) {
            LOGE("Renderer initialization failed: %s", e.what());
            // Clean up and nullify pointer if init fails
            delete renderer;
            renderer = nullptr;
        }
    } else {
        LOGI("Renderer already initialized.");
    }
}

// New function to be called from the render thread
extern "C" JNIEXPORT void JNICALL
Java_com_beezyvortex_studio_MainActivity_renderLoop(JNIEnv* env, jobject /* this */) {
    if (renderer != nullptr) {
        try {
            renderer->renderFrame();
        } catch (const std::exception& e) {
            LOGE("Exception in renderFrame: %s", e.what());
            // Decide on error handling. Maybe stop rendering?
        }
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_beezyvortex_studio_MainActivity_cleanupVulkan(JNIEnv* env, jobject /* this */) {
    if (renderer != nullptr) {
        LOGI("Cleaning up renderer from JNI...");
        renderer->cleanup();
        delete renderer;
        renderer = nullptr;
    } else {
        LOGI("Cleanup called but renderer was already null.");
    }
}
