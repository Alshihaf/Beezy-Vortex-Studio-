
cmake_minimum_required(VERSION 3.22.1)

project("beezyvortexstudio"
    VERSION 0.1
    LANGUAGES CXX)

# Add the VulkanRenderer source file to the library
add_library(beezyvortex-native SHARED
    native-lib.cpp
    VulkanRenderer.cpp)

# Find and link the Android logging library
find_library(log-lib
    log)

# Find and link the Vulkan library included in the NDK
find_library(vulkan-lib
    vulkan)

# Link all libraries to our native library
target_link_libraries(beezyvortex-native
    ${log-lib}
    ${vulkan-lib}
    )
