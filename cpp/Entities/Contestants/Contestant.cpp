#include "Contestant.h"
int Contestant::IDcount=0;

contestantStatus Contestant::getStatus() {
    return status;
}

CompanionList Contestant::getCompanionList() const {
    return companionList;
}

/**
 * Default constructor
 * @param playerName
 */
Contestant::Contestant(std::string playerName) : playerName(playerName) {
    status=CREADY;
    ID=IDcount;
    IDcount++;
}
void Contestant::addCompanion(Companion &companion) {
        companionList.getReadyList()->push_back(&companion);
}

Contestant::~Contestant() = default;

int Contestant::getId() const {
    return ID;
}

void Contestant::setStatus(contestantStatus status)  {
    Contestant::status = status;
}

const std::string &Contestant::getPlayerName() const {
    return playerName;
}

/**
 * Used only by player
 */
Contestant::Contestant() {
    IDcount++;
    ID=IDcount;
}

void Contestant::setPlayerName(const std::string &playerName) {
    Contestant::playerName = playerName;
}

/**
 * Constructor used when loading the game
 */
Contestant::Contestant(int id, const std::string &playerName, contestantStatus status) : ID(id), playerName(playerName),
                                                                                         status(status) {}
