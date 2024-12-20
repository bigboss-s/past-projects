#ifndef GRATUROWA_CONTESTANT_H
#define GRATUROWA_CONTESTANT_H

#include "string"
#include "vector"
#include "../Companions/CompanionList.h"

class Companion;
class CompanionList;

enum contestantStatus{CREADY, DEFEATED};
class Contestant {
protected:
    static int IDcount;
    int ID;
    CompanionList companionList;
    std::string playerName;
    contestantStatus status;
public:
    Contestant(std::string playerName);
    Contestant();

    void setPlayerName(const std::string &playerName);

    Contestant(int id, const std::string &playerName, contestantStatus status);

    virtual ~Contestant();

    friend std::ostream& operator<<(std::ostream& out, const Contestant& contestant);
    friend std::ostream& operator<<(std::ostream& out, const contestantStatus contestantStatus);

    void addCompanion(Companion &companion);
    int getId() const;

    contestantStatus getStatus();

    CompanionList getCompanionList() const;

    const std::string &getPlayerName() const;

    void setStatus(contestantStatus status) ;
};


#endif //GRATUROWA_CONTESTANT_H
