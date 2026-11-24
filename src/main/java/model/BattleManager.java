package model;

import java.util.Random;
import java.lang.Math;

/**
 * Implementation of IBattleManager interface.
 * Students will complete this implementation for Checkpoint 2.
 */
public class BattleManager implements IBattleManager {
    private final Random random;

    public BattleManager() {
        this.random = new Random();
    }

    public BattleManager(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public IBattle startBattle(IMonster monster1, IMonster monster2) {
        IMonster m1 = monster1.copy();
        IMonster m2 = monster2.copy();

        // Monsters start each battle at full hp
        m1.heal();
        m2.heal();

        if(m1.getSpeed() < m2.getSpeed()){
            return new Battle(m2, m1, this);
        }
        else{
            return new Battle(m1, m2, this);
        }
    }

    @Override
    public int calculateDamage(IMonster attacker, IMonster defender) {

        // "Level" currently hardcoded as 1
        double baseDamage = ((2 * 1 / 5 + 2) * attacker.getAttack() / defender.getDefense()) / 50 + 2;
        double typeMultiplier = getTypeEffectiveness(attacker.getType(), defender.getType());
        double randomMultiplier =  random.nextDouble(0.85, 1.0);
        int damage = (int)(baseDamage * typeMultiplier * randomMultiplier);
        int finalDamage = Math.max(1, damage);
        if(rollCritical(attacker)){
            return (int)(finalDamage * 1.5);
        }
        else{
            return finalDamage;
        }
    }

    @Override
    public double getTypeEffectiveness(MonsterType attackerType, MonsterType defenderType) {
        double multiplier;
        if (attackerType.equals(MonsterType.NORMAL) || defenderType.equals(MonsterType.NORMAL) 
                || defenderType.equals(MonsterType.ELECTRIC) || attackerType.equals(defenderType)
                || (attackerType.equals(MonsterType.ELECTRIC) && defenderType.equals(MonsterType.FIRE))){
            multiplier = 1.0;
        }
        else if ((attackerType.equals(MonsterType.FIRE) && defenderType.equals(MonsterType.WATER))
                || (attackerType.equals(MonsterType.WATER) && defenderType.equals(MonsterType.GRASS))
                || (attackerType.equals(MonsterType.GRASS) && defenderType.equals(MonsterType.FIRE))
                || (attackerType.equals(MonsterType.ELECTRIC) && defenderType.equals(MonsterType.GRASS))){
            multiplier = 0.5;
        }
        else if ((attackerType.equals(MonsterType.FIRE) && defenderType.equals(MonsterType.GRASS))
                || (attackerType.equals(MonsterType.WATER) && defenderType.equals(MonsterType.FIRE))
                || (attackerType.equals(MonsterType.GRASS) && defenderType.equals(MonsterType.WATER))
                || (attackerType.equals(MonsterType.ELECTRIC) && defenderType.equals(MonsterType.WATER))){
            multiplier = 2.0;
        }
        else{
            // Crash early :)
            throw new UnsupportedOperationException("Type invalid or missing.");
        }
        return multiplier;
    }

    public boolean rollCritical(IMonster attacker) {
            double critChance = Math.min(0.3, attacker.getSpeed() / 500.0);
            double rand = random.nextDouble();
            return rand <= critChance;
    }
}
