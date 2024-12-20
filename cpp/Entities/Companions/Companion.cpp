#include "Companion.h"
#include "../Contestants/Contestant.h"
#include "algorithm"
#include "vector"


int Companion::IDcount = 1;

/**
 * Constructor for a companion, used on a new game
 * @param compName companion name
 * @param str strength
 * @param dex dexterity
 * @param hp HP
 * @param exp experience dropped
 * @param spName special ability name
 * @param type type
 * @param relation relation to the player
 * @param spType special ability type
 * @param spCooldown special ability cooldown
 */
Companion::Companion(std::string compName, int str, int dex, int hp, int exp, std::string spName, Type type,
                     Relation relation,
                     TypeSpecial spType, int spCooldown) : compName(compName), str(str), dex(dex), HP(hp), EXP(exp),
                                                           spName(spName), type(type),
                                                           relation(relation), spType(spType), spCooldown(spCooldown) {
    status = READY;
    currHP = HP;
    evoTresh = 100;
    currentEvo = 0;
    canEvo = false;
    isEvolved = false;
    ID = IDcount;
    IDcount++;
    spCurrCooldown = 0;
    debuffCount=0;
    currStr=str;
}

/**
 * Constructor used for loading the game
 */
Companion::Companion(int id, int str, int dex, int hp, int currHp, int exp, int evoTresh, int currentEvo,
                     int spCooldown, int spCurrCooldown, int currStr, int ownerId, int debuffCount, int buffCount,
                     const std::string &spName, const std::string &compName, Type type, CompanionStatus status,
                     Relation relation, TypeSpecial spType, bool canEvo, bool isEvolved) : ID(id), str(str), dex(dex),
                                                                                           HP(hp), currHP(currHp),
                                                                                           EXP(exp), evoTresh(evoTresh),
                                                                                           currentEvo(currentEvo),
                                                                                           spCooldown(spCooldown),
                                                                                           spCurrCooldown(
                                                                                                   spCurrCooldown),
                                                                                           currStr(currStr),
                                                                                           ownerID(ownerId),
                                                                                           debuffCount(debuffCount),
                                                                                           buffCount(buffCount),
                                                                                           spName(spName),
                                                                                           compName(compName),
                                                                                           type(type), status(status),
                                                                                           relation(relation),
                                                                                           spType(spType),
                                                                                           canEvo(canEvo),
                                                                                           isEvolved(isEvolved) {
}

/**
 * Copying constructor
 */
Companion::Companion(const Companion & c1) : currHP(c1.currHP),
                                             currentEvo(c1.currentEvo),
                                             spCurrCooldown(
                                                     c1.spCurrCooldown),
                                             currStr(c1.currStr),
                                             debuffCount(
                                                     c1.debuffCount),
                                             buffCount(c1.buffCount),
                                             str(c1.str), dex(c1.dex),
                                             HP(c1.HP), EXP(c1.EXP),
                                             spCooldown(c1.spCooldown),
                                             ownerID(c1.ownerID),
                                             evoTresh(c1.evoTresh),
                                             ID(c1.ID), spName(c1.spName),
                                             compName(c1.compName),
                                             type(c1.type),
                                             status(c1.status),
                                             relation(c1.relation),
                                             spType(c1.spType),
                                             canEvo(c1.canEvo),
                                             isEvolved(c1.isEvolved) {}





/**
 * Attack the enemy, checks if the attack is effective or weak
 * Assigns EXP to companion if the enemy has fainted
 * @param enemy Enemy
 * @param isSpecial For type POWERATTACK special ability
 */
void Companion::attack(Companion &enemy, bool isSpecial) {
    std::cout<<"\n"<<compName<<" attacks "<<enemy.getCompName()<<"!\n";
    if (genRandom(1, 100) % 100 + 1 > enemy.getDex()) {
        double dmgMult;
        switch (this->checkInter(enemy)) {
            case (STRONG):
                std::cout << "The attack is very effective!\n";
                dmgMult = 1.5;
                break;
            case (WEAK):
                std::cout << "The attack is weak!\n";
                dmgMult = 0.5;
                break;
            case (NEUTRAL):
                dmgMult = 1;
                break;
        }
        int damage = (this->getStr() * dmgMult) - genRandom(1, this->getStr() * 0.2) + (isSpecial ? 5 : 0);
        enemy.dealDamage(damage);
        std::cout << this->getCompName() << " deals " << damage << " points of damage!\n\n" <<
                  enemy.getCompName() << "'s current HP " << enemy.getCurrHp() << " / " << enemy.getHp() << "\n\n";
        if (enemy.getStatus() == FAINTED) {
            std::cout<<enemy.getCompName()<<" has fainted!\n";
            this->setCurrentEvo(this->getCurrentEvo() + enemy.getExp());
            if (currentEvo >= evoTresh) {
                canEvo = true;
                std::cout<<compName<<" can now evolve!\n";
            }
        }
    } else
        std::cout <<"But "<<enemy.getCompName()<< " evaded!\n";
}

/**
 * Using the special ability
 * @param contTeam Own team
 * @param comp Enemy
 */
void Companion::useSpecial(std::vector<Companion*> const &contTeam, Companion &comp) {
    this->setSpCurrCooldown(this->getSpCooldown());
    switch (this->spType) {
        case HEALTEAM:
            std::cout<<this->getCompName()<<" uses team heal!\n"
                                            "Every companion in the team HP+15\n";
            for(int i=0; i<contTeam.size(); i++){
                if(contTeam.at(i)->getCurrHp()+15>contTeam.at(i)->getHp())
                    contTeam.at(i)->setCurrHp(contTeam.at(i)->getHp());
                else
                    contTeam.at(i)->setCurrHp(contTeam.at(i)->getCurrHp()+15);
            }
            break;
        case DEBUFFENEMY:
            std::cout<<this->getCompName()<<" uses debuff enemy!\n"
                                            "Enemy's strength -10 for 4 rounds\n";
            comp.currStr=comp.getStr()-10;
            comp.setDebuffCount(4);
            break;
        case BUFFSELF:
            std::cout<<this->getCompName()<<" uses buff self!\n"
                                            "Own strength +10 for 3 rounds\n";
            currStr=str+10;
            buffCount=3;
            break;
        case POWERATTACK:
            std::cout<<this->getCompName()<<" uses a supercharged attack!\n";
            attack(comp,true );
            break;
    }
}

/**
 * Evolving a companion, picking which stats to evolve
 */
void Companion::compEvo() {
    for (int i = 0; i < 2; ++i) {
        std::cout<<"Which stats do you want to evolve?\n"
                   "1. Strength\n"
                   "2. Dexterity\n"
                   "3. HP\n";
        std::cout<< (i == 1 ? "Enter your first choice by number\n" : "Enter your second choice\n");
        std::string choice;
        std::cin>>choice;
        int choiceInt;
        try{
            choiceInt = std::stoi(choice);
            if(choiceInt > 3 || choiceInt < 1)
                throw std::invalid_argument("Invalid arg");
        }catch(std::invalid_argument &ex){
            std::cout<<"Invalid choice\n";
        }
        switch (choiceInt) {
            case 1:
                currStr=str+10;
                std::cout<<"Strength evolved!\n";
                break;
            case 2:
                dex+=10;
                std::cout<<"Dexterity evolved!\n";
                break;
            case 3:
                currHP=HP+10;
                std::cout<<"HP evolved!\n";
            default:
                std::cout<<"err\n";
        }
    }
    std::cout<<compName<<" evolved!\n"
                         "Str ="<<str<<
                         "Dex ="<<dex<<
                         "MAX HP ="<<currHP<<
                         "Current HP full!\n";
        isEvolved = true;
        canEvo = false;
}

/**
 * Overpowered attack, for faster games
 * Doesn't trigger evolving
 * @param enemy
 */
void Companion::superAttack(Companion &enemy) {
    enemy.dealDamage(100);
}

/**
 * Deals damage to self
 * @param DMG Points of damage
 */
void Companion::dealDamage(double DMG) {
    this->currHP -= DMG;
    if (this->getCurrHp() <= 0) {
        this->setStatus(FAINTED);
        this->setCurrHp(0);
    }
}

/**
 * Generating random numbers from a given range <min, max>
 * @param min
 * @param max
 * @return
 */
int Companion::genRandom(int min, int max) {
    std::random_device rdev;
    std::mt19937 gen(rdev());
    std::uniform_int_distribution<> dist(min, max);
    return dist(gen);
}



/**
 * Checks if companion is effective or weak against an enemy
 * @param enemy Enemy to compare
 * @return WEAK, NEUTRAL or STRONG
 */
TypeInteraction Companion::checkInter(Companion &enemy) const {
    switch (this->getType()) {
        case (WATER):
            switch (enemy.getType()) {
                case EARTH:
                case FIRE:
                    return STRONG;
                case WATER:
                    return WEAK;
                default:
                    return NEUTRAL;
            }
        case EARTH:
            switch (enemy.getType()) {
                case FIRE:
                case ICE:
                case IRON:
                    return STRONG;
                case AIR:
                    return WEAK;
                default:
                    return NEUTRAL;
            }
        case AIR:
            switch (enemy.getType()) {
                case ICE:
                    return STRONG;
                case EARTH:
                case IRON:
                    return WEAK;
                default:
                    return NEUTRAL;
            }
        case FIRE:
            switch (enemy.getType()) {
                case ICE:
                case IRON:
                    return STRONG;
                case WATER:
                case EARTH:
                    return WEAK;
                default:
                    return NEUTRAL;
            }
        case ICE:
            switch (enemy.getType()) {
                case EARTH:
                    return STRONG;
                case WATER:
                case FIRE:
                case ICE:
                    return WEAK;
                default:
                    return NEUTRAL;
            }
        case IRON:
            switch (enemy.getType()) {
                case WATER:
                case AIR:
                    return STRONG;
                case FIRE:
                case IRON:
                    return WEAK;
                default:
                    return NEUTRAL;
            }
        default:
            return NEUTRAL;
    }
}

void Companion::setStatus(CompanionStatus compStatus) {
    this->status = compStatus;
}

Type Companion::getType() const {
    return this->type;
}

int Companion::getStr() const {
    return str;
}

int Companion::getHp() const {
    return HP;
}

int Companion::getDex() const {
    return dex;
}

int Companion::getExp() const {
    return EXP;
}

const std::string &Companion::getSpName() const {
    return spName;
}

CompanionStatus Companion::getStatus() const {
    return status;
}



int Companion::getEvoTresh() const {
    return evoTresh;
}

int Companion::getCurrentEvo() const {
    return currentEvo;
}

bool Companion::isCanEvo() const {
    return canEvo;
}

void Companion::setCurrentEvo(int currentEvo) {
    Companion::currentEvo = currentEvo;
}

void Companion::setRelation(Relation relation) {
    Companion::relation = relation;
}


int Companion::getCurrHp() const {
    return currHP;
}

void Companion::setCurrHp(int currHp) {
    currHP = currHp;
}

Companion::~Companion() = default;

const std::string &Companion::getCompName() const {
    return compName;
}

int Companion::getId() const {
    return ID;
}

int Companion::getSpCooldown() const {
    return spCooldown;
}

int Companion::getSpCurrCooldown() const {
    return spCurrCooldown;
}

bool Companion::getIsEvolved() const {
    return isEvolved;
}

void Companion::setOwnerId(int ownerId) {
    ownerID = ownerId;
}

int Companion::getOwnerId() const {
    return ownerID;
}


void Companion::setSpCurrCooldown(int spCurrCooldown) {
    Companion::spCurrCooldown = spCurrCooldown;
}

void Companion::setDebuffCount(int debuffCount) {
    Companion::debuffCount = debuffCount;
}

int Companion::getDebuffCount() const {
    return debuffCount;
}

int Companion::getBuffCount() const {
    return buffCount;
}

void Companion::setCurrStr(int currStr) {
    Companion::currStr = currStr;
}

void Companion::setBuffCount(int buffCount) {
    Companion::buffCount = buffCount;
}

int Companion::getCurrStr() const {
    return currStr;
}

Relation Companion::getRelation() const {
    return relation;
}

TypeSpecial Companion::getSpType() const {
    return spType;
}
