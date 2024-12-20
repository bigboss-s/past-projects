#ifndef GRATUROWA_ENEMY_H
#define GRATUROWA_ENEMY_H

#include "string"
#include "vector"
#include "../Companions/Companion.h"
#include "../Companions/CompanionList.h"
#include "Contestant.h"

class Enemy : public Contestant{

public:
    Enemy(std::string playerName) : Contestant(playerName) {}

    Enemy(int id, const std::string &playerName, contestantStatus status) : Contestant(id, playerName, status) {}
};


#endif //GRATUROWA_ENEMY_H
