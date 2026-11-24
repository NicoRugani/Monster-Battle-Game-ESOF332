package model;

/**
 * Interface representing a monster in the game.
 * Students will implement basic methods in Checkpoint 1.
 * Leveling methods (getLevel, getExperience, addExperience) at the bottom if this file are implemented in Checkpoint 3
 * You DO NOT need to complete these for Checkpoint 1.
 */
public interface IMonster {

    /**
     * Gets the monster's unique identifier.
     * @return the monster's ID
     */
    default String getId() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the monster's name.
     * @return the monster's name
     */
    default String getName() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the monster's type.
     * @return the monster's type
     */
    default MonsterType getType() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the monster's current HP.
     * @return the current HP
     */
    default int getCurrentHp() { throw new UnsupportedOperationException("Not implemented yet"); }

    /**
     * Gets the monster's maximum HP.
     * @return the maximum HP
     */
    default int getMaxHp() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the monster's attack stat.
     * @return the attack stat
     */
    default int getAttack() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the monster's defense stat.
     * @return the defense stat
     */
    default int getDefense() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the monster's speed stat.
     * @return the speed stat
     */
    default int getSpeed() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Sets the monster's current HP.
     * @param hp the new HP value
     */
    default void setCurrentHp(int hp) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Checks if the monster is fainted (HP <= 0).
     * @return true if the monster is fainted, false otherwise
     */
    default boolean isFainted() { throw new UnsupportedOperationException("Not implemented yet"); }

    /**
     * Heals the monster to full HP.
     */
    default void heal() { throw new UnsupportedOperationException("Not implemented yet"); }

    /**
     * Creates a copy of this monster.
     * @return a new monster with the same properties
     */
    default IMonster copy() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // ==================== Checkpoint 3: Leveling Methods ====================

    /**
     * Gets the monster's current level (for Checkpoint 3).
     * @return the monster's level
     */
    default int getLevel() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the monster's current experience points (for Checkpoint 3).
     * @return the current experience
     */
    default int getExperience() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Adds experience to the monster (for Checkpoint 3).
     * @param exp the amount of experience to add
     */
    default void addExperience(int exp) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}