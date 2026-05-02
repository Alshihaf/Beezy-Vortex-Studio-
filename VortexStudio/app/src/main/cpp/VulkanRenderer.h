
#pragma once

#include <android/native_window.h>
#include <vulkan/vulkan.h>

class VulkanRenderer {
public:
    VulkanRenderer(ANativeWindow* window);
    ~VulkanRenderer();

    bool init();
    void render();
    void cleanup();

private:
    bool createInstance();
    bool pickPhysicalDevice();
    bool createLogicalDevice();
    bool createSwapchain();
    void cleanupSwapchain();

    ANativeWindow* nativeWindow_ = nullptr;
    VkInstance instance_ = VK_NULL_HANDLE;
    VkPhysicalDevice physicalDevice_ = VK_NULL_HANDLE;
    VkDevice device_ = VK_NULL_HANDLE;
    VkQueue graphicsQueue_ = VK_NULL_HANDLE;
    VkSurfaceKHR surface_ = VK_NULL_HANDLE;
    VkSwapchainKHR swapchain_ = VK_NULL_HANDLE;
    
    // Add more Vulkan handles here as we build the engine
};
