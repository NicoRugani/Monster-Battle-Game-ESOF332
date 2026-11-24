package model;

import java.util.List;

/**
 * Interface representing a player in the game.
 * Students will implement this interface in Checkpoint 4.
 */
public interface IPlayer {

    /**
     * Gets the player's unique identifier.
     * @return the player's ID
     */
    default String getId() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the player's username.
     * @return the player's username
     */
    default String getUsername() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the player's monsters.
     * @return list of the player's monsters
     */
    default List<IMonster> getMonsters() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Adds a monster to the player's collection.
     * @param monster the monster to add
     */
    default void addMonster(IMonster monster) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the player's win count.
     * @return the number of battles won
     */
    default int getWins() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the player's loss count.
     * @return the number of battles lost
     */
    default int getLosses() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Increments the player's win count.
     */
    default void recordWin() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Increments the player's loss count.
     */
    default void recordLoss() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the player's total score (used for leaderboard ranking).
     * @return the player's score
     */
    default int getScore() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Calculates the player's win rate.
     * @return the win rate as a percentage (0-100)
     */
    default double getWinRate() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
