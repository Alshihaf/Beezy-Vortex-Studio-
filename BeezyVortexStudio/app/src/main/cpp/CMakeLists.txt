
cmake_minimum_required(VERSION 3.22.1)

project("beezyvortexstudio"
    VERSION 0.1
    LANGUAGES CXX)

add_library(beezyvortex-native SHARED
    native-lib.cpp
    VulkanRenderer.cpp) # Add VulkanRenderer.cpp to the build

find_library(log-lib
    log)

# Find and link Vulkan library
find_library(vulkan-lib
    vulkan)

target_link_libraries(beezyvortex-native
    ${log-lib}
    ${vulkan-lib}) # Link against Vulkan
