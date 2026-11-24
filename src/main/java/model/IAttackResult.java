package model;

/**
 * Interface representing the result of an attack in a battle.
 * Students will implement this interface in Checkpoint 2.
 */
public interface IAttackResult {

    /**
     * Gets the name of the attacking monster.
     * @return the attacker's name
     */
    default String getAttackerName() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the name of the defending monster.
     * @return the defender's name
     */
    default String getDefenderName() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the amount of damage dealt by the attack.
     * @return the damage amount
     */
    default int getDamage() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the type effectiveness multiplier for the attack.
     * @return the effectiveness multiplier (0.5, 1.0, or 2.0)
     */
    default double getEffectiveness() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the defender's remaining HP after the attack.
     * @return the defender's remaining HP
     */
    default int getDefenderRemainingHp() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Checks if the attack was a critical hit.
     * @return true if the attack was a critical hit, false otherwise
     */
    default boolean isCritical() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets a message describing the type effectiveness.
     * @return "It's super effective!", "It's not very effective...", or empty string for neutral
     */
    default String getEffectivenessMessage() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
