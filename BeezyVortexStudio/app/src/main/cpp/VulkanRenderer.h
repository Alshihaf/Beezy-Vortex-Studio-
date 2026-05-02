
#ifndef BEEZYVORTEXSTUDIO_VULKANRENDERER_H
#define BEEZYVORTEXSTUDIO_VULKANRENDERER_H

#include <android/native_window.h>
#include <vulkan/vulkan.h>
#include <vector>

class VulkanRenderer {
public:
    VulkanRenderer();
    ~VulkanRenderer();

    void init(ANativeWindow* window);
    void cleanup();

private:
    void createInstance();
    void createSurface(ANativeWindow* window);
    void pickPhysicalDevice();
    void createLogicalDevice();
    void createSwapchain();
    void createImageViews();

    VkInstance instance;
    VkSurfaceKHR surface;
    VkPhysicalDevice physicalDevice;
    VkDevice device;
    VkQueue graphicsQueue;
    VkSwapchainKHR swapchain;
    std::vector<VkImage> swapchainImages;
    std::vector<VkImageView> swapchainImageViews;
    VkFormat swapchainImageFormat;
    VkExtent2D swapchainExtent;
};

#endif //BEEZYVORTEXSTUDIO_VULKANRENDERER_H
