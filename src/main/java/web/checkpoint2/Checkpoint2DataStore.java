package web.checkpoint2;

import model.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * In-memory data storage for Checkpoint 2 app.
 * Extends Checkpoint 1 functionality with battle management.
 */
public class Checkpoint2DataStore {
    private final Map<String, IMonster> userMonsters;
    private final Map<String, IBattle> activeBattles;
    private final IMonsterFactory monsterFactory;
    private final IBattleManager battleManager;
    private final AtomicInteger monsterCounter;
    private final AtomicInteger battleCounter;

    public Checkpoint2DataStore(IMonsterFactory monsterFactory, IBattleManager battleManager) {
        this.userMonsters = new HashMap<>();
        this.activeBattles = new HashMap<>();
        this.monsterFactory = monsterFactory;
        this.battleManager = battleManager;
        this.monsterCounter = new AtomicInteger(1);
        this.battleCounter = new AtomicInteger(1);
    }

    // ===== Monster Management (from Checkpoint 1) =====

    public String addMonster(IMonster monster) {
        String id = monster.getId();
        userMonsters.put(id, monster);
        return id;
    }

    public IMonster getMonster(String id) {
        return userMonsters.get(id);
    }

    public List<IMonster> getAllMonsters() {
        return new ArrayList<>(userMonsters.values());
    }

    public List<IMonster> getMonstersByType(MonsterType type) {
        return userMonsters.values().stream()
                .filter(m -> m.getType() == type)
                .collect(Collectors.toList());
    }

    public int getMonsterCount() {
        return userMonsters.size();
    }

    public boolean hasMonster(String id) {
        return userMonsters.containsKey(id);
    }

    public boolean removeMonster(String id) {
        return userMonsters.remove(id) != null;
    }

    public IMonsterFactory getMonsterFactory() {
        return monsterFactory;
    }

    // ===== Battle Management (new for Checkpoint 2) =====

    /**
     * Create a new battle between two monsters.
     * @param monster1Id ID of first monster
     * @param monster2Id ID of second monster
     * @return Battle ID
     */
    public String createBattle(String monster1Id, String monster2Id) {
        IMonster m1 = userMonsters.get(monster1Id);
        IMonster m2 = userMonsters.get(monster2Id);

        if (m1 == null || m2 == null) {
            throw new IllegalArgumentException("One or both monsters not found");
        }

        // Create copies to preserve originals
        IMonster m1Copy = m1.copy();
        IMonster m2Copy = m2.copy();

        // Start the battle
        IBattle battle = battleManager.startBattle(m1Copy, m2Copy);

        // Generate unique battle ID
        String battleId = "battle-" + battleCounter.getAndIncrement();
        activeBattles.put(battleId, battle);

        return battleId;
    }

    /**
     * Get a battle by ID.
     * @param battleId The battle ID
     * @return The battle, or null if not found
     */
    public IBattle getBattle(String battleId) {
        return activeBattles.get(battleId);
    }

    /**
     * Get all active battles.
     * @return List of all battles
     */
    public List<IBattle> getAllBattles() {
        return new ArrayList<>(activeBattles.values());
    }

    /**
     * Get the number of battles.
     * @return Battle count
     */
    public int getBattleCount() {
        return activeBattles.size();
    }

    /**
     * Remove a battle.
     * @param battleId The battle ID
     * @return true if removed
     */
    public boolean removeBattle(String battleId) {
        return activeBattles.remove(battleId) != null;
    }

    /**
     * Get all completed battles.
     * @return List of completed battles
     */
    public List<IBattle> getCompletedBattles() {
        return activeBattles.values().stream()
                .filter(IBattle::isBattleOver)
                .collect(Collectors.toList());
    }

    /**
     * Get all ongoing battles.
     * @return List of ongoing battles
     */
    public List<IBattle> getOngoingBattles() {
        return activeBattles.values().stream()
                .filter(b -> !b.isBattleOver())
                .collect(Collectors.toList());
    }

    /**
     * Clear all data.
     */
    public void clearAll() {
        userMonsters.clear();
        activeBattles.clear();
        monsterCounter.set(1);
        battleCounter.set(1);
    }

    /**
     * Get the battle manager.
     * @return The battle manager
     */
    public IBattleManager getBattleManager() {
        return battleManager;
    }
}
