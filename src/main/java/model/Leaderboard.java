package model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of ILeaderboard interface.
 * Students will complete this implementation for Checkpoint 4.
 */
public class Leaderboard implements ILeaderboard {
    private final Map<String, IPlayer> players;

    public Leaderboard() {
        this.players = new HashMap<>();
    }

    // Searches the hashmap, places the top players in a list, and stops after hitting the limit
    @Override
    public List<IPlayer> getTopPlayers(int limit) {
        List<IPlayer> allSorted = this.getAllPlayers();
        List<IPlayer> topPlayers = new ArrayList<>();

        for (int i = 0; i < limit && i < allSorted.size(); i++) {
            topPlayers.add(allSorted.get(i));
        }
        return topPlayers;
    }

    @Override
    public List<IPlayer> getAllPlayers() {
        return this.players.values().stream()
            .sorted(Comparator
                .comparingInt(IPlayer::getScore).reversed()
                .thenComparing(Comparator.comparingInt(IPlayer::getWins).reversed())
                .thenComparing(IPlayer::getUsername))
            .collect(Collectors.toList());
    }

    @Override
    public int getPlayerRank(String playerId) {
        List<IPlayer> sorted = getAllPlayers();

        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).getId().equals(playerId)){
                return i + 1;
            }
        }
        return -1;
    }

    @Override
    public void updatePlayer(IPlayer player) {
        if (player == null) {
            throw new IllegalArgumentException("Player not found");
        }

        players.put(player.getId(), player);
    }

    @Override
    public IPlayer getPlayer(String playerId) { return this.players.get(playerId); }

    // Searches the hashmap for the player's username
    @Override
    public IPlayer getPlayerByUsername(String username) {
        for (IPlayer player : this.players.values()) {
            if (player.getUsername().equalsIgnoreCase(username)) {
                return player;
            }
        }
        return null; // Should only be reached if a match is not found
    }

    @Override
    public void addPlayer(IPlayer player) { 
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }

        if (players.containsKey(player.getId())) {
            throw new IllegalArgumentException("Player already exists");
        }

        for (IPlayer p : players.values()) {
            if (p.getUsername().equalsIgnoreCase(player.getUsername())) {
                throw new IllegalArgumentException("Username already exists");
            }
        }

        players.put(player.getId(), player);
    }

    @Override
    public int getTotalPlayers() {
        return players.size();
    }

    @Override
    public void clear() {
        players.clear();
    }
}