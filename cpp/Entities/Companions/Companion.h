#ifndef GRATUROWA_COMPANION_H
#define GRATUROWA_COMPANION_H

#include <string>
#include <iostream>
#include <random>
#include <map>


class Player;
class Enemy;
class Contestant;

enum Type {WATER, EARTH, AIR, FIRE, ICE, IRON};
enum CompanionStatus{READY, FAINTED};
enum Relation{ALLY, ENEMY};
enum TypeInteraction{WEAK, NEUTRAL, STRONG};
enum TypeSpecial{HEALTEAM, DEBUFFENEMY, BUFFSELF, POWERATTACK};

class Companion {
protected:
    int currHP, currentEvo,spCurrCooldown, currStr, debuffCount, buffCount;
    int str, dex, HP, EXP, spCooldown, ownerID;
    int evoTresh;
    int ID;
    static int IDcount;
    const std::string spName, compName;
    const Type type;
    CompanionStatus status;
    Relation relation;
    const TypeSpecial spType;
    bool canEvo, isEvolved;
public:
    int getCurrStr() const;

    Relation getRelation() const;

    TypeSpecial getSpType() const;

    Companion(std::string compName, int str, int dex, int hp, int exp, std::string spName, Type type,
              Relation relation, TypeSpecial spType, int spCooldown);

    Companion(int id, int str, int dex, int hp, int currHp, int exp, int evoTresh, int currentEvo, int spCooldown,
              int spCurrCooldown, int currStr, int ownerId, int debuffCount, int buffCount, const std::string &spName,
              const std::string &compName, Type type, CompanionStatus status, Relation relation, TypeSpecial spType,
              bool canEvo, bool isEvolved);

    Companion(const Companion & c1);

    friend std::ostream& operator<<(std::ostream& out, const CompanionStatus status);
    friend std::ostream& operator<<(std::ostream& out, const Companion& companion);
    friend std::ostream& operator<<(std::ostream& out, const Type type);
    friend std::ostream& operator<<(std::ostream& out, const Relation relation);
    friend std::ostream& operator<<(std::ostream& out, const TypeSpecial spType);


    virtual ~Companion();

    static int genRandom(int min, int max);
    void useSpecial(std::vector<Companion*> const &contTeam, Companion &comp);
    void dealDamage(double DMG);
    TypeInteraction checkInter(Companion &enemy) const;

    Type getType() const;
    CompanionStatus getStatus() const;
    const std::string &getCompName() const;
    const std::string &getSpName() const;
    static void superAttack(Companion &enemy);
    int getOwnerId() const;
    int getCurrHp() const;
    int getDebuffCount() const;
    int getExp() const;
    int getHp() const;
    int getBuffCount() const;
    int getStr() const;
    void setCurrStr(int currStr);
    void setBuffCount(int buffCount);
    void setDebuffCount(int debuffCount);
    void setSpCurrCooldown(int spCurrCooldown);
    void setStatus(CompanionStatus status);
    void attack(Companion &enemy, bool isSpecial);
    void setOwnerId(int ownerId);
    void compEvo();
    int getDex() const;
    int getEvoTresh() const;
    int getCurrentEvo() const;
    bool isCanEvo() const;
    int getId() const;
    int getSpCooldown() const;
    int getSpCurrCooldown() const;
    bool getIsEvolved() const;
    void setCurrentEvo(int currentEvo);
    void setRelation(Relation relation);
    void setCurrHp(int currHp);
};


#endif //GRATUROWA_COMPANION_H
