cmake_minimum_required (VERSION 2.8)
project(iiwa_com)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -pthread")


find_package(Boost REQUIRED  system thread timer chrono)


add_executable(${PROJECT_NAME} src/iiwa_client.cpp)
target_link_libraries(${PROJECT_NAME} ${Boost_LIBRARIES})

