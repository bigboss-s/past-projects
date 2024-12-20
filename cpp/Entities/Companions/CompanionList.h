#ifndef GRATUROWA_COMPANIONLIST_H
#define GRATUROWA_COMPANIONLIST_H

#include "vector"
#include "Companion.h"


class CompanionList {
protected:
    std::vector<Companion*> readyCompanions;
public:
    std::vector<Companion*> *getReadyList();
    void showReady();

};



#endif //GRATUROWA_COMPANIONLIST_H
