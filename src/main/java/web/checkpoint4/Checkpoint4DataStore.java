package web.checkpoint4;

import model.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory data storage for Checkpoint 4 app.
 * Manages players, battles, and the leaderboard for the full game.
 */
public class Checkpoint4DataStore {
    private final Map<String, IPlayer> players;
    private final IMonsterFactory monsterFactory;
    private final IBattleManager battleManager;
    private final IEvolutionManager evolutionManager;
    private final ILeaderboard leaderboard;
    private final AtomicInteger playerCounter;

    public Checkpoint4DataStore(IMonsterFactory monsterFactory, IBattleManager battleManager,
                                IEvolutionManager evolutionManager, ILeaderboard leaderboard) {
        this.players = new HashMap<>();
        this.monsterFactory = monsterFactory;
        this.battleManager = battleManager;
        this.evolutionManager = evolutionManager;
        this.leaderboard = leaderboard;
        this.playerCounter = new AtomicInteger(1);
    }

    // ===== Player Management =====

    public String addPlayer(IPlayer player) {
        String id = "player-" + playerCounter.getAndIncrement();
        players.put(id, player);
        leaderboard.addPlayer(player);
        return id;
    }

    public IPlayer getPlayer(String id) {
        return players.get(id);
    }

    public List<IPlayer> getAllPlayers() {
        return new ArrayList<>(players.values());
    }

    public int getPlayerCount() {
        return players.size();
    }

    public boolean hasPlayer(String id) {
        return players.containsKey(id);
    }

    // ===== Accessors =====

    public IMonsterFactory getMonsterFactory() {
        return monsterFactory;
    }

    public IBattleManager getBattleManager() {
        return battleManager;
    }

    public IEvolutionManager getEvolutionManager() {
        return evolutionManager;
    }

    public ILeaderboard getLeaderboard() {
        return leaderboard;
    }

    // ===== Utility =====

    public void clearAll() {
        players.clear();
        leaderboard.clear();
        playerCounter.set(1);
    }
}
