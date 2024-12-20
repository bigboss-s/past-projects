#include <iostream>
#include <cctype>
#include <algorithm>
#include <fstream>
#include "Entities/Companions/Companion.h"
#include "Entities/Contestants/Player.h"
#include "Entities/Contestants/Contestant.h"

enum diffLevel {
    NORMAL, HARD
};

diffLevel pdiffLevel;
std::vector<Enemy *> undefeatedEnemies;
std::vector<Companion *> gameCompanions;
Player *player;
bool canSave;

/**
 * Enum to string maps and functions
 */
static std::map<TypeSpecial, std::string> toStringSptype;
static std::map<Relation, std::string> toStringRelation;
static std::map<Type, std::string> toStringType;
static std::map<CompanionStatus, std::string> toStringStatus;
static std::map<diffLevel, std::string> toStringLevel;
static std::map<contestantStatus, std::string> toStringContStatus;

std::ostream &operator<<(std::ostream &out, const diffLevel level) {

    return out << toStringLevel[level];
}

std::ostream &operator<<(std::ostream &out, const CompanionStatus status) {

    return out << toStringStatus[status];
}

std::ostream &operator<<(std::ostream &out, const Type type) {

    return out << toStringType[type];
}

std::ostream &operator<<(std::ostream &out, const Relation relation) {

    return out << toStringRelation[relation];
}

std::ostream &operator<<(std::ostream &out, const Companion &companion) {
    if (companion.relation == ALLY) {
        return out << companion.ID << ". " << companion.compName << ", current HP "
                   << companion.currHP << " / " << companion.HP <<
                   ", strength " << companion.currStr << ", dexterity " << companion.dex << ", type " << companion.type
                   <<
                   (companion.spCurrCooldown == 0 ? ", special attack READY " : ", special attack on cooldown " +
                                                                                std::to_string(
                                                                                        companion.spCurrCooldown))
                   << "(" << companion.getSpName() << "), current EXP " << companion.currentEvo << "/"
                   << companion.evoTresh;
    } else {
        return out << companion.ID << ". " << companion.compName << ", current HP " << companion.currHP << " / "
                   << companion.HP <<
                   ", strength " << companion.currStr << ",  dexterity " << companion.dex << ", type " << companion.type
                   <<
                   (companion.spCurrCooldown == 0 ? ", special attack READY " : ", special attack on cooldown " +
                                                                                std::to_string(
                                                                                        companion.spCurrCooldown))
                   << "(" << companion.getSpName() << ")";
    }
}

std::ostream &operator<<(std::ostream &out, const Contestant &contestant) {
    return out << contestant.ID << ". " << contestant.playerName;
}

std::ostream &operator<<(std::ostream &out, const contestantStatus contestantStatus) {
    return out << toStringContStatus[contestantStatus];
}

std::ostream &operator<<(std::ostream &out, const TypeSpecial spType) {


    return out << toStringSptype[spType];
}

/**
 * Checks if a text file exists
 * @param fileName name of file without .txt
 * @return true - exists, false - doesn't
 */
bool fileExists(const std::string &fileName) {
    if (FILE *file = fopen(fileName.c_str(), "r")) {
        fclose(file);
        return true;
    } else {
        return false;
    }
}

/**
 * Returns reference to enemy from a vector of enemies
 * @param ID ID of enemy
 * @param enemyVec vector where enemy should be added
 * @return return reference, or throws invalid_argument if enemy doesn't belong to the vector
 */
Enemy &getEnemyByID(int ID, std::vector<Enemy *> const &enemyVec) {
    int i = 0;
    for (auto iterator = enemyVec.begin(); iterator != enemyVec.end(); ++iterator, ++i) {
        if (enemyVec.at(i)->getId() == ID) {
            return **iterator;
        }
    }
    throw std::invalid_argument("Invalid ID\n");
}

/**
 * Returns reference to companion from a vector of companions
 * @param ID ID of companion
 * @param compVec vector where the companion should be added
 * @return return reference, or throws invalid_argument if companion doesn't belong to the vector
 */
Companion &getCompByID(int ID, std::vector<Companion *> const &compVec) {
    int i = 0;
    for (auto iterator = compVec.begin(); iterator != compVec.end(); ++iterator, ++i) {
        if (compVec.at(i)->getId() == ID) {
            return **iterator;
        }
    }
    throw std::invalid_argument("Companion unavailable!\n");
}

/**
 * Saves a companion from contestant's ready list at i to the savefile
 * @param contestant Contestant whose companion we're saving
 * @param outfile File stream to the save file
 * @param i Index of companion in in the contestants list
 */
void saveContestant(Contestant const &contestant, std::ofstream &outfile, int i) {
    outfile << contestant.getCompanionList().getReadyList()->at(i)->getCompName() << " \n "
            << contestant.getCompanionList().getReadyList()->at(i)->getId()
            << "\n" << contestant.getCompanionList().getReadyList()->at(i)->getStr() << "\n"
            << contestant.getCompanionList().getReadyList()->at(i)->getDex()
            << "\n" << contestant.getCompanionList().getReadyList()->at(i)->getHp() << "\n"
            << contestant.getCompanionList().getReadyList()->at(i)->getCurrHp()
            << "\n" << contestant.getCompanionList().getReadyList()->at(i)->getExp()
            << "\n" << contestant.getCompanionList().getReadyList()->at(i)->getEvoTresh() << "\n"
            << contestant.getCompanionList().getReadyList()->at(i)->getCurrentEvo()
            << "\n" << contestant.getCompanionList().getReadyList()->at(i)->getSpCooldown()
            << "\n" << contestant.getCompanionList().getReadyList()->at(i)->getSpCurrCooldown() << "\n"
            << contestant.getCompanionList().getReadyList()->at(i)->getCurrStr()
            << "\n" << contestant.getCompanionList().getReadyList()->at(i)->getOwnerId() << "\n"
            << contestant.getCompanionList().getReadyList()->at(i)->getDebuffCount()
            << "\n" << contestant.getCompanionList().getReadyList()->at(i)->getBuffCount() << "\n";

    outfile << "" << contestant.getCompanionList().getReadyList()->at(i)->getSpName()
            << "\n" << contestant.getCompanionList().getReadyList()->at(i)->getType() << "\n"
            << contestant.getCompanionList().getReadyList()->at(i)->getStatus()
            << "\n" << contestant.getCompanionList().getReadyList()->at(i)->getRelation() << "\n"
            << contestant.getCompanionList().getReadyList()->at(i)->getSpType()
            << "\n" << contestant.getCompanionList().getReadyList()->at(i)->isCanEvo() << "\n"
            << contestant.getCompanionList().getReadyList()->at(i)->getIsEvolved() << "\n";
}

/**
 * Saves game to a text file
 * Checks whether a file already exists and asks if we want to overwrite
 */
void saveGame() {
    std::string fileName;
    while (true) {
        std::cout << "Enter savefile name:\n";
        std::cin >> fileName;
        fileName += ".txt";
        if (fileExists(fileName)) {
            std::cout << "File already exists, overwrite? Yes to confirm\n";
            std::string choice;
            std::cin >> choice;
            std::transform(choice.begin(), choice.end(), choice.begin(), tolower);
            if (choice == "yes" || choice == "y")
                break;
        } else
            break;
    }
    std::ofstream saveList;
    saveList.open("GameSaves.txt", std::ios::app);
    saveList<<fileName<<" - "<<player->getPlayerName()<<"\n";
    saveList.close();
    std::ofstream outfile;
    outfile.open(fileName, std::ios::out);
    outfile << "awesomegamesavefile\n\nPLAYER NAME\n";
    outfile << player->getPlayerName() << "\n";
    for (int i = 0; i < player->getCompanionList().getReadyList()->size(); ++i) {
        outfile << "\n" << "PLAYER COMPANIONS" << "\n";
        saveContestant(*player, outfile, i);
    }
    for (auto &undefeatedEnemie: undefeatedEnemies) {
        outfile << "\n" << "ENEMIES" << "\n";
        outfile << undefeatedEnemie->getId() << "\n" << undefeatedEnemie->getPlayerName() << "\n"
                << undefeatedEnemie->getStatus() << "\n";
    }
    for (auto &undefeatedEnemie: undefeatedEnemies) {
        for (int j = 0; j < undefeatedEnemie->getCompanionList().getReadyList()->size(); ++j) {
            outfile << "\n" << "ENEMY COMPANIONS" << "\n";
            saveContestant(*undefeatedEnemie, outfile, j);
        }
    }
    outfile.close();
    std::cout << "Save completed, saved to " << fileName << "\n\n";
}

/**
 * Checks if file is empty
 * @param file file to check
 * @return True if empty
 */
bool fileEmpty(std::fstream& file){
    return file.peek() == std::fstream::traits_type::eof();
}

/**
 * Loads game from a text file
 * Checks if text file is a save file
 */
void loadGame() {
    std::string fileName;
    std::fstream saveList;
    saveList.open("GameSaves.txt", std::ios::in);
    if(!fileEmpty(saveList)){
        std::cout << "Available save files:\n";
        std::string line;
        while (!saveList.eof()) {
            std::getline(saveList, line);
            std::cout << line;
        }
    } else{
        std::cout<<"No available save files";
    }
    std::cout<<"\n";
    saveList.close();
    while (true) {
        while (true) {
            std::cout << "Enter save file name\n";
            std::cin >> fileName;
            fileName += ".txt";
            if (!fileExists(fileName))
                std::cout << "File doesn't exist\n";
            else
                break;
        }
        try {
            std::fstream infile;
            infile.open(fileName, std::ios::in);
            std::string curLine;
            std::getline(infile, curLine);
            if (curLine != "awesomegamesavefile") {
                throw std::invalid_argument("Not a save file\n");
            }
            while (!infile.eof()) {
                std::getline(infile, curLine);
                if (curLine == "PLAYER NAME") {
                    std::getline(infile, curLine);
                    player->setPlayerName(curLine);
                }
                if (curLine == "PLAYER COMPANIONS") {
                    std::string COMPNAME;
                    int ID, STR, DEX, HP, CURRHP, EXP, EVOTRESH, CURRENTEVO, SPCOOLDOWN, SPCURRCOOLDOWN,
                            CURRSTR, OWNERID, DEBUFFCOUNT, BUFFCOUNT;
                    std::string SPNAME, TYPE, STATUS, RELATION, SPTYPE, CANEVO, ISEVOLVED;

                    std::getline(infile, curLine);
                    COMPNAME = curLine;

                    std::getline(infile, curLine);
                    ID = std::stoi(curLine);

                    std::getline(infile, curLine);
                    STR = std::stoi(curLine);

                    std::getline(infile, curLine);
                    DEX = std::stoi(curLine);

                    std::getline(infile, curLine);
                    HP = std::stoi(curLine);

                    std::getline(infile, curLine);
                    CURRHP = std::stoi(curLine);

                    std::getline(infile, curLine);
                    EXP = std::stoi(curLine);

                    std::getline(infile, curLine);
                    EVOTRESH = std::stoi(curLine);

                    std::getline(infile, curLine);
                    CURRENTEVO = std::stoi(curLine);

                    std::getline(infile, curLine);
                    SPCOOLDOWN = std::stoi(curLine);

                    std::getline(infile, curLine);
                    SPCURRCOOLDOWN = std::stoi(curLine);

                    std::getline(infile, curLine);
                    CURRSTR = std::stoi(curLine);

                    std::getline(infile, curLine);
                    OWNERID = std::stoi(curLine);

                    std::getline(infile, curLine);
                    DEBUFFCOUNT = std::stoi(curLine);

                    std::getline(infile, curLine);
                    BUFFCOUNT = std::stoi(curLine);

                    std::getline(infile, curLine);
                    SPNAME = curLine;

                    std::getline(infile, curLine);
                    TYPE = curLine;

                    std::getline(infile, curLine);
                    STATUS = curLine;

                    std::getline(infile, curLine);
                    RELATION = curLine;

                    std::getline(infile, curLine);
                    SPTYPE = curLine;

                    std::getline(infile, curLine);
                    CANEVO = curLine;

                    std::getline(infile, curLine);
                    ISEVOLVED = curLine;

                    Type type;
                    for (auto &i: toStringType) {
                        if (i.second == TYPE)
                            type = i.first;
                    }

                    CompanionStatus status;
                    for (auto &i: toStringStatus) {
                        if (i.second == STATUS)
                            status = i.first;
                    }

                    Relation relation;
                    for (auto &i: toStringRelation) {
                        if (i.second == RELATION)
                            relation = i.first;
                    }

                    TypeSpecial sptype;
                    for (auto &i: toStringSptype) {
                        if (i.second == SPTYPE)
                            sptype = i.first;
                    }

                    bool canevo = !(CANEVO == "0");

                    bool isevolved = !(ISEVOLVED == "0");

                    player->addCompanion(*new Companion(ID, STR, DEX, HP, CURRHP, EXP, EVOTRESH, CURRENTEVO,
                                                        SPCOOLDOWN, SPCURRCOOLDOWN, CURRSTR, OWNERID, DEBUFFCOUNT,
                                                        BUFFCOUNT, SPNAME, COMPNAME, type, status, relation,
                                                        sptype, canevo, isevolved));
                }
                if (curLine == "ENEMIES") {
                    int ID;
                    std::string PLAYERNAME, CONTSTATUS;

                    std::getline(infile, curLine);
                    ID = std::stoi(curLine);

                    std::getline(infile, curLine);
                    PLAYERNAME = curLine;

                    std::getline(infile, curLine);
                    CONTSTATUS = curLine;

                    contestantStatus contestantStatus;
                    for (auto &i: toStringContStatus) {
                        if (i.second == CONTSTATUS)
                            contestantStatus = i.first;
                    }

                    undefeatedEnemies.push_back(new Enemy(ID, PLAYERNAME, contestantStatus));
                }
                if (curLine == "ENEMY COMPANIONS") {
                    std::string COMPNAME;
                    int ID, STR, DEX, HP, CURRHP, EXP, EVOTRESH, CURRENTEVO, SPCOOLDOWN, SPCURRCOOLDOWN,
                            CURRSTR, OWNERID, DEBUFFCOUNT, BUFFCOUNT;
                    std::string SPNAME, TYPE, STATUS, RELATION, SPTYPE, CANEVO, ISEVOLVED;

                    std::getline(infile, curLine);
                    COMPNAME = curLine;

                    std::getline(infile, curLine);
                    ID = std::stoi(curLine);

                    std::getline(infile, curLine);
                    STR = std::stoi(curLine);

                    std::getline(infile, curLine);
                    DEX = std::stoi(curLine);

                    std::getline(infile, curLine);
                    HP = std::stoi(curLine);

                    std::getline(infile, curLine);
                    CURRHP = std::stoi(curLine);

                    std::getline(infile, curLine);
                    EXP = std::stoi(curLine);

                    std::getline(infile, curLine);
                    EVOTRESH = std::stoi(curLine);

                    std::getline(infile, curLine);
                    CURRENTEVO = std::stoi(curLine);

                    std::getline(infile, curLine);
                    SPCOOLDOWN = std::stoi(curLine);

                    std::getline(infile, curLine);
                    SPCURRCOOLDOWN = std::stoi(curLine);

                    std::getline(infile, curLine);
                    CURRSTR = std::stoi(curLine);

                    std::getline(infile, curLine);
                    OWNERID = std::stoi(curLine);

                    std::getline(infile, curLine);
                    DEBUFFCOUNT = std::stoi(curLine);

                    std::getline(infile, curLine);
                    BUFFCOUNT = std::stoi(curLine);

                    std::getline(infile, curLine);
                    SPNAME = curLine;

                    std::getline(infile, curLine);
                    TYPE = curLine;

                    std::getline(infile, curLine);
                    STATUS = curLine;

                    std::getline(infile, curLine);
                    RELATION = curLine;

                    std::getline(infile, curLine);
                    SPTYPE = curLine;

                    std::getline(infile, curLine);
                    CANEVO = curLine;

                    std::getline(infile, curLine);
                    ISEVOLVED = curLine;

                    Type type;
                    for (auto &i: toStringType) {
                        if (i.second == TYPE)
                            type = i.first;
                    }

                    CompanionStatus status;
                    for (auto &i: toStringStatus) {
                        if (i.second == STATUS)
                            status = i.first;
                    }

                    Relation relation;
                    for (auto &i: toStringRelation) {
                        if (i.second == RELATION)
                            relation = i.first;
                    }

                    TypeSpecial sptype;
                    for (auto &i: toStringSptype) {
                        if (i.second == SPTYPE)
                            sptype = i.first;
                    }

                    bool canevo = !(CANEVO == "0");

                    bool isevolved = !(ISEVOLVED == "0");

                    getEnemyByID(OWNERID, undefeatedEnemies).addCompanion(
                            *new Companion(ID, STR, DEX, HP, CURRHP, EXP, EVOTRESH, CURRENTEVO,
                                           SPCOOLDOWN, SPCURRCOOLDOWN, CURRSTR, OWNERID, DEBUFFCOUNT,
                                           BUFFCOUNT, SPNAME, COMPNAME, type, status, relation,
                                           sptype, canevo, isevolved));
                }
            }
            infile.close();
            break;
        } catch (std::invalid_argument &ex) {
            std::cout << ex.what();
        }
        break;
    }
    std::cout << "Loading complete!\n";
}

/**
 * Erases companions in the gamecompanions vector which also belong to the given vector
 * @param vec vector to compare with gamecompanions
 */
void eraseCompFromVec(std::vector<Companion *> const &vec) {
    for (auto it: vec) {
        auto it2 = gameCompanions.begin();
        for (int j = 0; j < gameCompanions.size(); ++j, ++it2) {
            if (it == gameCompanions.at(j))
                gameCompanions.erase(it2);
        }
    }
}

/**
 * More friendly version of companions tostring
 * @param comp companion to print
 */
void printComp(Companion const &comp) {
    std::string toLowerSp = comp.getSpName();
    std::transform(toLowerSp.begin(), toLowerSp.end(), toLowerSp.begin(), ::tolower);
    std::cout << comp.getCompName() << ", type " << comp.getType() << ", can do " <<
              toLowerSp << ", with a cooldown of " << comp.getSpCooldown() << " turns";
}

/**
 * Prints all undefeated enemies
 */
void printUndefeated() {
    int i = 0;
    for (auto it = undefeatedEnemies.begin(); it != undefeatedEnemies.end(); ++it, ++i) {
        std::cout << **it << " fighting with:\n";
        undefeatedEnemies.at(i)->getCompanionList().showReady();
        std::cout << "\n";
    }
}

/**
 * Checks if a string is a command
 * @param cmdCheck String to check for commands
 * @return true if string was a command, false if wasn't
 */
bool commandHandle(std::string const &cmdCheck) {
    if (cmdCheck.at(0) == '-') {
        if (cmdCheck == "--help" || cmdCheck == "-h")
            std::cout << "great help for a great game\n"
                         "--list or -ls for own companions list\n"
                         "--enemylist or -els for enemy companion list\n"
                         "--save or -s for saving game (only between each fight)\n";
        else if ((cmdCheck == "--list" || cmdCheck == "-ls") && !player->getCompanionList().getReadyList()->empty())
            player->getCompanionList().showReady();
        else if ((cmdCheck == "--save" || cmdCheck == "-s") && canSave) {
            saveGame();
        } else if ((cmdCheck == "--enemylist" || cmdCheck == "-els") && !undefeatedEnemies.empty()) {
            for (auto enemy: undefeatedEnemies) {
                std::cout << enemy->getId() << ". " << enemy->getPlayerName() << "'s current companions:\n";
                enemy->getCompanionList().showReady();
                std::cout << "\n";
            }
        } else if ((cmdCheck == "--quit" || cmdCheck == "-q") && canSave) {
            exit(0);
        } else
            std::cout << "cmd not found\n";
        return true;
    } else
        return false;
}

/**
 * Adds a selected companion to players companion list
 * @param counter for nicer messages
 */
void selectCompanion(int counter) {
    std::string countString[] = {"first", "second", "third", "fourth", "fifth", "last"};
    while (true) {
        std::cout << "Select your " << countString[counter] << " companion by ID\n";
        try {
            std::string companionChoice;
            std::cin >> companionChoice;
            while (commandHandle(companionChoice))
                std::cin >> companionChoice;
            std::stoi(companionChoice);
            getCompByID(std::stoi(companionChoice), gameCompanions);
            std::cout << "For your " << countString[counter] << " companion you've chosen " <<
                      getCompByID(std::stoi(companionChoice), gameCompanions).getCompName() << "!\n";
            player->addCompanion(getCompByID(std::stoi(companionChoice), gameCompanions));
            for (auto it = gameCompanions.begin(); it != gameCompanions.end(); it++) {
                if (*it == &getCompByID(std::stoi(companionChoice), gameCompanions)) {
                    gameCompanions.erase(it);
                    break;
                }
            }
            getCompByID(std::stoi(companionChoice), *player->getCompanionList().getReadyList()).setOwnerId(
                    player->getId());
            break;
        } catch (std::out_of_range &ex) {
            std::cout << "No companion with this ID\n";
        } catch (std::invalid_argument &ex) {
            std::cout << "Invalid ID\n";
        }
    }
}

/**
 * Sets a global difficulty
 */
void pickDiff() {
    std::string choice;
    while (true) {
        std::cin >> choice;
        while (commandHandle(choice)) {
            std::cin >> choice;
        }
        if (choice == "1") {
            pdiffLevel = NORMAL;
            break;
        }
        if (choice == "2") {
            pdiffLevel = HARD;
            break;
        }
        std::cout << "Bad selection, try again\n";
    }
}

/**
 * Main menu of the game
 * @return Ints corresponding to different choices
 */
int mainMenu() {
    std::cout << "1. New game\n2. Load game\n3. Exit\n";
    std::string choice;
    while (true) {
        std::cin >> choice;
        while (commandHandle(choice))
            std::cin >> choice;
        if (choice == "1") {
            return 1;
        }
        if (choice == "2") {
            return 2;
        }
        if (choice == "3") {
            return 3;
        }
        std::cout << "Bad selection, try again\n";
    }
}

/**
 * Describes pickDiff() function
 */
void newGame() {
    std::cout << "New game!\nFirst, pick your contestant name:\n";
    std::string playerName;
    std::cin >> playerName;
    while (commandHandle(playerName))
        std::cin >> playerName;
    player->setPlayerName(playerName);
    std::cout << playerName << " it is!\nNow, let's select the difficulty level:\n"
                               "1. Easy - 4 enemies, each with 4 companions\n"
                               "2. Hard - 8 enemies, each with 4 companions\n";
    pickDiff();
    std::cout << "You've chosen " << pdiffLevel << "!\n";
}

/**
 * Generates enemies with a set name pool
 */
void generateEnemies() {
    std::cout << "Let's generate some enemies...\n";
    std::vector<std::string> enemyNames = {"James", "Josh", "Jackson", "Jake", "Jeremy",
                                           "Jill", "Jean", "Harry", "Jude", "Jaden", "Jeff",
                                           "Jay", "John", "Jermaine"};
    int loopCount;
    switch (pdiffLevel) {
        case NORMAL:
            loopCount = 4;
            break;
        case HARD:
            loopCount = 8;
            break;
    }
    for (int i = 0; i < loopCount; i++) {
        auto iterator = enemyNames.begin() + Companion::genRandom(0, enemyNames.size() - 1);
        std::string name = *iterator;
        undefeatedEnemies.push_back(new Enemy(name));
        enemyNames.erase(iterator);
    }
    std::cout << "You'll be fighting against:\n";
    for (auto iterator = undefeatedEnemies.begin(); iterator != undefeatedEnemies.end(); iterator++) {
        std::cout << **iterator;
        if (iterator != undefeatedEnemies.end() - 2 && iterator != undefeatedEnemies.end() - 1) {
            std::cout << ", ";
        } else if (iterator == undefeatedEnemies.end() - 2)
            std::cout << " and ";
        else
            std::cout << "!\n";
    }
}

/**
 * Generates companions for enemies and the player,
 * with names from a set name pool
 */
void generateCompanions() {
    std::cout << "Now, let's generate some companions...\n";

    std::vector<std::string> companionNames = {"Gazepet", "Bicross", "Oysteat", "Tigig",
                                               "Horrorilla", "Ramwing", "Odduin", "Moltida",
                                               "Tuturkey", "Hamstun", "Shanite", "Salamadily",
                                               "Vulteaf", "Dracevoir", "Chimequito", "Grotile",
                                               "Venomida", "Glacopus", "Boaris", "Pigloo",
                                               "Wolvelite", "Bacoon", "Ostrairy", "Dolphairy",
                                               "Magimingo", "Magmawhale", "Ironutor", "Silverowary",
                                               "Magpine", "Aquail", "Pigeomite", "Chimpaphy",
                                               "Coyomite", "Goriros", "Leopygon", "Rhinaza",
                                               "Stundine", "Glacihawk", "Quilite", "Waros",
                                               "Tortoros", "Numbat", "Dragoros", "Termou",
                                               "Dolphoal", "Steelbug", "Electriphant", "Waranzee",
                                               "Oddephant", "Camosquito", "Bamboa", "Manatuff",
                                               "Lemair", "Caterpius", "Specibat", "Steelphin",
                                               "Terryte", "Demoyote", "Kingray", "Oysterminate"};

    for (int i = 0; i < 25; i++) {
        auto iterator = companionNames.begin() + Companion::genRandom(0, companionNames.size() - 1);
        std::string name = *iterator;
        Type compType;
        TypeSpecial comSpecialType;
        int spCooldown;
        std::string spName;
        switch (Companion::genRandom(1, 6)) {
            case 1:
                compType = WATER;
                break;
            case 2:
                compType = EARTH;
                break;
            case 3:
                compType = AIR;
                break;
            case 4:
                compType = FIRE;
                break;
            case 5:
                compType = ICE;
                break;
            case 6:
                compType = IRON;
                break;
        }
        switch (Companion::genRandom(1, 4)) {
            case 1:
                comSpecialType = HEALTEAM;
                spName = "Team heal";
                spCooldown = 3;
                break;
            case 2:
                comSpecialType = DEBUFFENEMY;
                spName = "Enemy debuff";
                spCooldown = 4;
                break;
            case 3:
                comSpecialType = BUFFSELF;
                spName = "Buff self";
                spCooldown = 3;
                break;
            case 4:
                comSpecialType = POWERATTACK;
                spName = "Supercharged attack";
                spCooldown = 4;
                break;
        }
        gameCompanions.push_back(new Companion(name, Companion::genRandom(15, 30), Companion::genRandom(15, 30),
                                               Companion::genRandom(60, 80), Companion::genRandom(40, 60),
                                               spName, compType, ALLY, comSpecialType, spCooldown
        ));
        companionNames.erase(iterator);
    }
    std::cout << "You and your enemies will be fighting with:\n";
    for (auto comp: gameCompanions) {
        std::cout << *comp << "\n";
    }
}

/**
 * Picking 6 companions and printing them after election
 */
void chooseCompanion() {
    std::cout
            << "Now, let's select your companions!\nYou can only select six, so pick wisely!\nYou can also pick a single companion only once!\n";
    for (int i = 0; i < 6; ++i) {
        selectCompanion(i);
    }
    std::cout << "These are your companions!\n";
    player->getCompanionList().showReady();
    eraseCompFromVec(*player->getCompanionList().getReadyList());
    for (auto comp: gameCompanions) {
        comp->setRelation(ENEMY);
    }
}

/**
 * Randomly assigning copies of the
 * remaining companions to the enemies
 */
void enemyCompanions() {
    std::cout << "Now, let's distribute companions to the enemies.\n";
    int i = 0;
    for (auto it = undefeatedEnemies.begin(); it != undefeatedEnemies.end(); ++it, ++i) {
        for (int j = 0; j < 4; ++j) {
            auto *comp = new Companion(*gameCompanions.at(Companion::genRandom(0, gameCompanions.size()-1)));
            undefeatedEnemies.at(i)->addCompanion(*comp);
            undefeatedEnemies.at(i)->getCompanionList().getReadyList()->at(j)->setOwnerId(
                    undefeatedEnemies.at(i)->getId());
        }
        std::cout << **it << " will be fighting with:\n";
        for (int j = 0; j < 4; ++j) {
            std::cout << undefeatedEnemies.at(i)->getCompanionList().getReadyList()->at(j)->getCompName();
            if (j != 2 && j != 3)
                std::cout << ",\n";
            else if (j == 2)
                std::cout << "\nand ";
        }
        std::cout << "!\n";

    }
}

/**
 * Swapping a companion
 * Checks whether the selected companion is ready to fight
 * @return reference to selected companion
 */
Companion &swampComp() {
    while (true) {
        std::cout << "Select your new companion by ID\n";
        try {
            std::string companionChoice;
            std::cin >> companionChoice;
            while (commandHandle(companionChoice))
                std::cin >> companionChoice;
            std::stoi(companionChoice);
            if (getCompByID(std::stoi(companionChoice), *player->getCompanionList().getReadyList()).getStatus() ==
                FAINTED) {
                throw std::invalid_argument("This companion is currently fainted!\n"
                                            "Select a different companion\n");
            }
            std::cout << "You've chosen " <<
                      getCompByID(std::stoi(companionChoice), *player->getCompanionList().getReadyList()).getCompName()
                      << "!\n";
            return getCompByID(std::stoi(companionChoice), *player->getCompanionList().getReadyList());
        } catch (std::out_of_range &ex) {
            std::cout << "No companion with this ID\n";
        } catch (std::invalid_argument &ex) {
            std::cout << ex.what();
        }
    }
}

/**
 * Checks and decreases special ability cooldowns
 * for companions in the vector
 * @param contTeam Vector in which we want to check for cooldowns
 */
void checkCooldown(std::vector<Companion *> const &contTeam) {
    for (auto comp: contTeam) {
        if (comp->getSpCurrCooldown() > 0) {
            comp->setSpCurrCooldown(comp->getSpCurrCooldown() - 1);
        }
        if (comp->getDebuffCount() > 0) {
            comp->setDebuffCount(comp->getDebuffCount() - 1);
            if (comp->getDebuffCount() == 0) {
                comp->setCurrStr(comp->getStr());
                std::cout << comp->getCompName() << " debuff ended!\n";
            }
        }
        if (comp->getBuffCount() > 0) {
            comp->setBuffCount(comp->getBuffCount() - 1);
            if (comp->getBuffCount() == 0) {
                comp->setCurrStr(comp->getStr());
                std::cout << comp->getCompName() << " buff ended!\n";
            }
        }
    }
}

/**
 * Selecting an action and generating the enemy's action
 * @param playerComp Player's current companion
 * @param enemyComp Enemy's current companion
 * @param enemy Enemy for later passing
 */
void battleFight(Companion &playerComp, Companion &enemyComp, Enemy &enemy) {
    Companion *playerCompPtr = &playerComp;
    Companion *enemyCompPtr = &enemyComp;
    std::string choice;
    while (true) {
        try {
            checkCooldown(*player->getCompanionList().getReadyList());
            bool ifCont = true;
            while (ifCont) {
                std::cout << "What do you want to do?\n"
                             "1. Attack\n"
                             "2. Special ability\n"
                             "3. Swap your companion\n"
                             "4. Evolve you companion\n"
                             "Current companion: " << playerCompPtr->getCompName() <<
                          " HP " << playerCompPtr->getCurrHp() << " / " << playerCompPtr->getHp() <<
                          " SP ";
                if (playerCompPtr->getSpCurrCooldown() != 0)
                    std::cout << "on cooldown " << playerCompPtr->getSpCurrCooldown() << "\n\n";
                else
                    std::cout << "READY\n\n";
                if (playerCompPtr->getStatus() == FAINTED) {
                    std::cout << "Your companion has fainted!\n"
                                 "Please swap\n\n";
                    playerCompPtr = &swampComp();
                }
                std::cin >> choice;
                while (commandHandle(choice))
                    std::cin >> choice;
                std::stoi(choice);
                switch (std::stoi(choice)) {
                    case 1:
                        playerCompPtr->attack(*enemyCompPtr, false);
                        ifCont = false;
                        break;
                    case 2:
                        if (playerCompPtr->getSpCurrCooldown() != 0) {
                            std::cout << "Special ability not ready!\n";
                            break;
                        }
                        playerCompPtr->useSpecial(*player->getCompanionList().getReadyList(), *enemyCompPtr);
                        ifCont = false;
                        break;
                    case 3:
                        playerCompPtr = &swampComp();
                        break;
                    case 4:
                        if (playerCompPtr->isCanEvo()) {
                            playerCompPtr->compEvo();
                            break;
                        } else if (playerCompPtr->getCurrentEvo() < playerCompPtr->getEvoTresh())
                            std::cout << "Cannot evolve yet!\n";
                        else if (playerCompPtr->getIsEvolved()) {
                            std::cout << "Already evolved!\n";
                        }
                        break;
                    case 5:
                        Companion::superAttack(*enemyCompPtr);
                        ifCont = false;
                        break;
                    default:
                        std::cout << "Invalid option\n";
                        break;
                }
            }
        } catch (std::out_of_range &ex) {
            std::cout << "Invalid option\n";
        } catch (std::invalid_argument &ex) {
            std::cout << "Invalid input\n";
        }
        if (enemyCompPtr->getStatus() == FAINTED) {
            int i = 0;
            while (enemyCompPtr->getStatus() == FAINTED) {
                if (i == enemy.getCompanionList().getReadyList()->size()) {
                    enemy.setStatus(DEFEATED);
                    break;
                }
                enemyCompPtr = enemy.getCompanionList().getReadyList()->at(i);
                ++i;
            }
            for (int j = 0; j < enemy.getCompanionList().getReadyList()->size(); ++j) {
                if (enemy.getCompanionList().getReadyList()->at(j)->getStatus() != FAINTED) {
                    enemyCompPtr = enemy.getCompanionList().getReadyList()->at(j);
                    break;
                }
                if (j == enemy.getCompanionList().getReadyList()->size() - 1) {
                    enemy.setStatus(DEFEATED);
                    return;
                }
            }
            std::cout << "Enemy's current team:\n";
            enemy.getCompanionList().showReady();
            std::cout << "With deployed " << enemyCompPtr->getCompName() << "\n";
        }
        if (enemyCompPtr->getSpCurrCooldown() == 0) {
            switch (Companion::genRandom(1, 2)) {
                case 1:
                    enemyCompPtr->attack(*playerCompPtr, false);
                    break;
                case 2:
                    enemyCompPtr->useSpecial(*getEnemyByID(enemyCompPtr->getOwnerId(),
                                                           undefeatedEnemies).getCompanionList().getReadyList(),
                                             *playerCompPtr);
                    break;
            }
        } else
            enemyCompPtr->attack(*playerCompPtr, false);
    }
}

/**
 * Initial enemy choice and picking a companion for the enemy
 * @param enemy Current enemy
 */
void battle(Enemy &enemy) {
    std::string playerCompChoice;
    std::cout << "\nThese are your companions:\n";
    player->getCompanionList().showReady();
    std::cout << "\nHere are enemy's companions:\n";
    enemy.getCompanionList().showReady();
    while (true) {
        std::cout << "\nWhich companion do you want to attack with?\n"
                     "Choose by ID\n\n";
        try {
            std::cin >> playerCompChoice;
            while (commandHandle(playerCompChoice))
                std::cin >> playerCompChoice;
            std::stoi(playerCompChoice);
            getCompByID(std::stoi(playerCompChoice), *player->getCompanionList().getReadyList());
            std::cout << "You've chosen ";
            printComp(getCompByID(std::stoi(playerCompChoice), *player->getCompanionList().getReadyList()));
            std::cout << " as your companion!\n";
            break;
        } catch (std::out_of_range &ex) {
            std::cout << "Companion doesn't exist\n";
        } catch (std::invalid_argument &ex) {
            std::cout << "Invalid ID\n";
        }
    }
    int enemyChoice = Companion::genRandom(0, 3);
    while (enemy.getCompanionList().getReadyList()->at(enemyChoice)->getStatus() == FAINTED)
        enemyChoice = Companion::genRandom(0, 3);
    std::cout << "The enemy will be fighting with: ";
    printComp(*enemy.getCompanionList().getReadyList()->at(enemyChoice));
    std::cout << "!\n";
    battleFight(getCompByID(std::stoi(playerCompChoice), *player->getCompanionList().getReadyList()),
                *enemy.getCompanionList().getReadyList()->at(enemyChoice), enemy);
}

/**
 * Core of the battle, initial enemy choice and later running the rest of the game
 * Deletes a defeated enemy from undefeatedenemies vector
 * @param enemyChoice
 */
void battleCore(std::string &enemyChoice) {
    while (true) {
        try {
            std::cin >> enemyChoice;
            while (commandHandle(enemyChoice))
                std::cin >> enemyChoice;
            std::stoi(enemyChoice);
            getEnemyByID(std::stoi(enemyChoice), undefeatedEnemies);
            canSave = false;
            std::cout << "You've chosen " << getEnemyByID(std::stoi(enemyChoice), undefeatedEnemies)
                      << " as your opponent!\n";
            break;
        } catch (std::out_of_range &ex) {
            std::cout << "Opponent doesn't exist\n";
        } catch (std::invalid_argument &ex) {
            std::cout << "Invalid ID\n";
        }
    }
    battle(getEnemyByID(std::stoi(enemyChoice), undefeatedEnemies));
    std::cout << getEnemyByID(std::stoi(enemyChoice), undefeatedEnemies).getPlayerName() << " has been defeated!\n";
    for (auto it = undefeatedEnemies.begin(); it != undefeatedEnemies.end(); ++it) {
        if (*it == &getEnemyByID(std::stoi(enemyChoice), undefeatedEnemies)) {
            undefeatedEnemies.erase(it);
            break;
        }
    }
}

/**
 * First round of the game
 */
void startGame() {
    std::cout << "Who do you want to attack?\n"
                 "Choose by ID\n";
    printUndefeated();
    std::string enemyChoice;
    battleCore(enemyChoice);
    std::cout << "Tutorial over, now you know how it goes...\n";
}

/**
 * Second and remaining rounds of the game
 */
void endGame() {
    while (true) {
        canSave = true;
        std::cout << "Who do you want to attack?\n"
                     "Choose by ID\n"
                     "Type -h or --help for help (saving possible)\n";
        printUndefeated();
        std::string enemyChoice;
        battleCore(enemyChoice);
        if (undefeatedEnemies.empty())
            break;
    }
    std::cout << "You won!";
}

/**
 * Fills maps responsible for enums tostring functions
 */
void fillToString() {
    if (toStringLevel.empty()) {
        toStringLevel[NORMAL] = "NORMAL";
        toStringLevel[HARD] = "HARD";
    }
    if (toStringStatus.empty()) {
        toStringStatus[READY] = "READY";
        toStringStatus[FAINTED] = "FAINTED";
    }
    if (toStringType.empty()) {
        toStringType[WATER] = "WATER";
        toStringType[EARTH] = "EARTH";
        toStringType[AIR] = "AIR";
        toStringType[FIRE] = "FIRE";
        toStringType[ICE] = "ICE";
        toStringType[IRON] = "IRON";
    }
    if (toStringRelation.empty()) {
        toStringRelation[ALLY] = "ALLY";
        toStringRelation[ENEMY] = "ENEMY";
    }
    if (toStringSptype.empty()) {
        toStringSptype[HEALTEAM] = "HEALTEAM";
        toStringSptype[DEBUFFENEMY] = "DEBUFFENEMY";
        toStringSptype[BUFFSELF] = "BUFFSELF";
        toStringSptype[POWERATTACK] = "POWERATTACK";
    }
    if (toStringContStatus.empty()) {
        toStringContStatus[CREADY] = "CREADY";
        toStringContStatus[DEFEATED] = "DEFEATED";
    }
}

int main() {
    player = new Player();
    fillToString();
    switch (mainMenu()) {
        case 1:
            newGame();
            generateEnemies();
            generateCompanions();
            chooseCompanion();
            enemyCompanions();
            std::cout << "\nGame start!\n";
            startGame();
            endGame();
            break;
        case 2:
            loadGame();
            endGame();
            break;
        case 3:
            exit(0);
    }
    return 0;

}
