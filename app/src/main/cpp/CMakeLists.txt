cmake_minimum_required(VERSION 3.10)

project("macronutrient")

add_library(macronutrient-lib SHARED native.cpp)

find_library(log-lib log)

target_link_libraries(macronutrient-lib ${log-lib})
