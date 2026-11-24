package model;

import java.util.UUID;

/**
 * Implementation of IMonster interface.
 * Students will complete this implementation for Checkpoint 1.
 *  * Leveling methods (getLevel, getExperience, addExperience) at the bottom if this file are implemented in Checkpoint 3
 *  * You DO NOT need to complete these for Checkpoint 1.
 */
public class Monster implements IMonster {
    private final String id;
    private String name;
    private MonsterType type;
    private int currentHp;
    private int maxHp;
    private int attack;
    private int defense;
    private int speed;
    private int level;
    private int experience;

    public Monster(String name, MonsterType type, int maxHp, int attack, int defense, int speed) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.level = 1;
        this.experience = 0;
    }

    // Constructor that preserves ID (for evolution)
    public Monster(String id, String name, MonsterType type, int maxHp, int attack, int defense, int speed, int level) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.level = level;
        this.experience = 0;
    }


    @Override
    public String getId() { return this.id; }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public MonsterType getType() {
        return this.type;
    }

    @Override
    public int getCurrentHp() {
        return this.currentHp;
    }

    @Override
    public int getMaxHp() { return this.maxHp; }

    @Override
    public int getAttack() {
        return this.attack;
    }

    @Override
    public int getDefense() { return this.defense; }

    @Override
    public int getSpeed() {
        return this.speed;
    }

    // Sets the currentHP
    @Override
    public void setCurrentHp(int hp) {
        if (hp <= 0) {
            this.currentHp = 0;
        } else if (hp >= maxHp) {
            this.currentHp = maxHp;
        } else {
            this.currentHp = hp;
        }
    }

    // Internal setters for evolution (package-private)
    void setMaxHp(int hp) {
        this.maxHp = hp;
    }

    void setAttack(int attack) {
        this.attack = attack;
    }

    void setDefense(int defense) {
        this.defense = defense;
    }

    void setSpeed(int speed) {
        this.speed = speed;
    }

    // Checks the currentHP, returns true if currentHP <= 0, and false is not
    @Override
    public boolean isFainted() {
        if (currentHp <= 0) {
            return true;
        } else {
            return false;
        }
    }

    // "Heals" the monster to full HP
    @Override
    public void heal() { this.currentHp = this.maxHp; }

    // Returns a new monster with the same characteristics of the current monster
    @Override
    public IMonster copy() {
        Monster copy = new Monster(this.name, this.type, this.maxHp, this.attack, this.defense, this.speed);
        copy.setCurrentHp(this.currentHp);
        return copy;
    }

    // ==================== Checkpoint 3: Leveling Methods ====================
    // The methods below are NOT needed until Checkpoint 3.
    // You can ignore these for Checkpoint 1.

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public int getExperience() {
        return this.experience;
    }

    @Override
    public void addExperience(int exp) {
        if (exp <= 0) {
            return;
        }
        this.experience += exp;
        // Progressive XP threshold: 200 XP for level 2, 300 XP for level 3, etc.
        while (this.experience >= (this.level + 1) * 100) {
            this.experience -= (this.level + 1) * 100;
            this.level++;
            // Stats increase every level
            this.maxHp += 5;
            this.attack += 2;
            this.defense += 2;
            this.speed += 1;
            // Full HP after level up
            this.currentHp = this.maxHp;
        }
    }
}
