package model;

import java.util.List;

/**
 * Interface for managing the game leaderboard.
 * Students will implement this interface in Checkpoint 4.
 */
public interface ILeaderboard {

    /**
     * Adds a new player to the leaderboard.
     * @param player the player to add
     */
    default void addPlayer(IPlayer player) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets a player by their ID.
     * @param playerId the player's ID
     * @return the player, or null if not found
     */
    default IPlayer getPlayer(String playerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets a player by their username.
     * @param username the player's username
     * @return the player, or null if not found
     */
    default IPlayer getPlayerByUsername(String username) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets all players sorted by score.
     * @return list of all players
     */
    default List<IPlayer> getAllPlayers() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the top players sorted by score.
     * @param limit the maximum number of players to return
     * @return list of top players
     */
    default List<IPlayer> getTopPlayers(int limit) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the total number of players.
     * @return the total number of players
     */
    default int getTotalPlayers() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets a player's rank on the leaderboard.
     * @param playerId the player's ID
     * @return the player's rank (1-based), or -1 if not found
     */
    default int getPlayerRank(String playerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Updates the leaderboard with a player's current stats.
     * @param player the player to update
     */
    default void updatePlayer(IPlayer player) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Removes all players from the leaderboard.
     */
    default void clear() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
