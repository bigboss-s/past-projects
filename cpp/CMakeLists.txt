cmake_minimum_required(VERSION 3.21)
project(GraTurowa)

set(CMAKE_CXX_STANDARD 14)

add_executable(GraTurowa main.cpp Entities/Contestants/Player.cpp Entities/Contestants/Player.h Entities/Companions/Companion.cpp Entities/Companions/Companion.h Entities/Contestants/Contestant.cpp Entities/Contestants/Contestant.h Entities/Companions/CompanionList.cpp Entities/Companions/CompanionList.h Entities/Contestants/Enemy.cpp Entities/Contestants/Enemy.h)
