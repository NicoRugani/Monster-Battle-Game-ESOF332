package model;

import java.util.List;

/**
 * Interface representing an ongoing battle.
 * Students will implement this interface in Checkpoint 2.
 */
public interface IBattle {

    /**
     * Gets the first monster in the battle.
     * @return monster 1
     */
    default IMonster getMonster1() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the second monster in the battle.
     * @return monster 2
     */
    default IMonster getMonster2() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the monster whose turn it is to attack.
     * @return the current attacker
     */
    default IMonster getCurrentAttacker() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Executes an attack from the current attacker.
     * This method should:
     * - Calculate damage
     * - Apply type effectiveness
     * - Check for critical hits
     * - Reduce the defender's HP
     * - Record the attack in the battle log
     * - Switch the turn to the other monster
     *
     * @return the result of the attack
     */
    default AttackResult attack() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Checks if the battle is over.
     * @return true if the battle is over, false otherwise
     */
    default boolean isBattleOver() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the winner of the battle.
     * @return the winning monster, or null if the battle is not over
     */
    default IMonster getWinner() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the battle log (list of attack results).
     * @return the battle log
     */
    default List<AttackResult> getBattleLog() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the current turn number.
     * @return the turn number
     */
    default int getTurnNumber() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}