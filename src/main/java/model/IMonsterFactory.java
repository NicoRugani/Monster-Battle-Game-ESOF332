package model;

import java.util.List;

/**
 * Interface for creating monsters.
 * Students will implement this interface in Checkpoint 1.
 */
public interface IMonsterFactory {

    /**
     * Creates a new monster with the given parameters.
     * @param name the monster's name
     * @param type the monster's type
     * @param maxHp the monster's maximum HP
     * @param attack the monster's attack stat
     * @param defense the monster's defense stat
     * @param speed the monster's speed stat
     * @return a new monster instance
     */
    default IMonster createMonster(String name, MonsterType type, int maxHp, int attack, int defense, int speed) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Creates a starter monster for a new player.
     * @param type the type of starter monster to create
     * @return a new starter monster
     */
    default IMonster createStarterMonster(MonsterType type) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets a list of all available monster templates.
     * @return list of all available monsters
     */
    default List<IMonster> getAllMonsterTemplates() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets a monster template by name.
     * @param name the name of the monster
     * @return the monster template, or null if not found
     */
    default IMonster getMonsterByName(String name) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}