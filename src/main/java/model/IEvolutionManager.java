package model;

/**
 * Interface for managing monster evolution.
 * Students will implement this interface in Checkpoint 3.
 */
public interface IEvolutionManager {

    /**
     * Checks if a monster can evolve.
     * @param monster the monster to check
     * @return true if the monster can evolve, false otherwise
     */
    default boolean canEvolve(IMonster monster) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Evolves a monster to its next form.
     * @param monster the monster to evolve
     * @return the evolved monster, or null if evolution is not possible
     */
    default IMonster evolve(IMonster monster) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the name of the next evolution for a monster.
     * @param monster the monster to check
     * @return the name of the next evolution, or null if there is none
     */
    default String getNextEvolution(IMonster monster) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the level required for the next evolution.
     * @param monster the monster to check
     * @return the level required, or -1 if there is no next evolution
     */
    default int getEvolutionLevel(IMonster monster) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the evolution stage of a monster (1, 2, or 3).
     * @param monster the monster to check
     * @return the evolution stage
     */
    default int getEvolutionStage(IMonster monster) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
