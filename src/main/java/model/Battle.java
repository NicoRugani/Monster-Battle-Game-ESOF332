package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of IBattle interface.
 * Students will complete this implementation for Checkpoint 2.
 */
public class Battle implements IBattle {
    private final IMonster monster1;
    private final IMonster monster2;
    private final BattleManager battleManager;
    private final List<AttackResult> battleLog;
    private int turnNumber;
    private IMonster currentAttacker;
    private IMonster currentDefender;

    public Battle(IMonster monster1, IMonster monster2, BattleManager battleManager) {
        this.monster1 = monster1;
        this.monster2 = monster2;
        this.battleManager = battleManager;
        this.battleLog = new ArrayList<>();
        this.turnNumber = 0;
        this.currentAttacker = null;
        this.currentDefender = null;

        if (monster1.getSpeed() >= monster2.getSpeed()) {
            currentAttacker = monster1;
            currentDefender = monster2;
        }
        else {
            currentAttacker = monster2;
            currentDefender = monster1;
        }
    }

    @Override
    public IMonster getMonster1() {
        return monster1;
    }

    @Override
    public IMonster getMonster2() {
        return monster2;
    }

    @Override
    public IMonster getCurrentAttacker() {
        return currentAttacker;
    }

@Override
public AttackResult attack() {
    if (isBattleOver()) {
        throw new IllegalStateException("Battle should have ended");
    }

    boolean critical = battleManager.rollCritical(currentAttacker);
    int damage = battleManager.calculateDamage(currentAttacker, currentDefender);

    int defenderHealth = currentDefender.getCurrentHp();
    defenderHealth -= damage;
    currentDefender.setCurrentHp(defenderHealth);

    AttackResult result = new AttackResult(
        currentAttacker.getName(),
        currentDefender.getName(),
        damage,
        battleManager.getTypeEffectiveness(currentAttacker.getType(), currentDefender.getType()),
        defenderHealth,
        critical
    );

    battleLog.add(result);

    turnNumber++;

    // swap turns
    IMonster temp = currentAttacker;
    currentAttacker = currentDefender;
    currentDefender = temp;

    return result;
}

    @Override
    public boolean isBattleOver() {
        if (monster1.isFainted() || monster2.isFainted()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public IMonster getWinner() {

        if(monster1.isFainted()){
            monster2.addExperience(50);
            return monster2;
        } 
        if (monster2.isFainted()){
            monster1.addExperience(50);
            return monster1;
        }
        return null;
    }

    @Override
    public List<AttackResult> getBattleLog() {
        return battleLog;
    }

    @Override
    public int getTurnNumber() {
     return turnNumber;
    }
}
