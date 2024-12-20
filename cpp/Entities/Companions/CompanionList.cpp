#include "CompanionList.h"
std::vector<Companion*> *CompanionList::getReadyList() {
    return &readyCompanions;
}

/**
 * Prints the ready companions list
 */
void CompanionList::showReady() {
    int i=0;
    for(auto iterator = readyCompanions.begin() ; iterator!=readyCompanions.end(); ++iterator, ++i){
        if(readyCompanions.at(i)->getStatus()!=FAINTED)
        { std::cout << **iterator << "\n"; }
    }
}


