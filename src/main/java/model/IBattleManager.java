package model;

/**
 * Interface for managing battles between monsters.
 * Students will implement this interface in Checkpoint 2.
 */
public interface IBattleManager {

    /**
     * Starts a new battle between two monsters.
     * @param monster1 the first monster
     * @param monster2 the second monster
     * @return the battle instance
     */
    default IBattle startBattle(IMonster monster1, IMonster monster2) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Calculates damage dealt by an attacker to a defender.
     * @param attacker the attacking monster
     * @param defender the defending monster
     * @return the amount of damage dealt
     */
    default int calculateDamage(IMonster attacker, IMonster defender) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Calculates the type effectiveness multiplier.
     * @param attackerType the type of the attacking monster
     * @param defenderType the type of the defending monster
     * @return the type effectiveness multiplier (0.5, 1.0, or 2.0)
     */
    default double getTypeEffectiveness(MonsterType attackerType, MonsterType defenderType) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}