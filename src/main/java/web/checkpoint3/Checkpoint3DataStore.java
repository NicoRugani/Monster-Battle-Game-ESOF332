package web.checkpoint3;

import model.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * In-memory data storage for Checkpoint 3 app.
 * Extends Checkpoint 2 functionality with evolution and leveling.
 */
public class Checkpoint3DataStore {
    private final Map<String, IMonster> userMonsters;
    private final Map<String, IBattle> activeBattles;
    private final Map<String, String> battleMonsterIds; // Track original monster IDs for battles
    private final Set<String> battlesWithXpAwarded; // Track which battles have awarded XP
    private final IMonsterFactory monsterFactory;
    private final IBattleManager battleManager;
    private final IEvolutionManager evolutionManager;
    private final AtomicInteger monsterCounter;
    private final AtomicInteger battleCounter;

    public Checkpoint3DataStore(IMonsterFactory monsterFactory, IBattleManager battleManager, IEvolutionManager evolutionManager) {
        this.userMonsters = new HashMap<>();
        this.activeBattles = new HashMap<>();
        this.battleMonsterIds = new HashMap<>();
        this.battlesWithXpAwarded = new HashSet<>();
        this.monsterFactory = monsterFactory;
        this.battleManager = battleManager;
        this.evolutionManager = evolutionManager;
        this.monsterCounter = new AtomicInteger(1);
        this.battleCounter = new AtomicInteger(1);
    }

    // ===== Monster Management =====

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

    // ===== Battle Management =====

    public String createBattle(String monster1Id, String monster2Id) {
        IMonster m1 = userMonsters.get(monster1Id);
        IMonster m2 = userMonsters.get(monster2Id);

        if (m1 == null || m2 == null) {
            throw new IllegalArgumentException("One or both monsters not found");
        }

        IMonster m1Copy = m1.copy();
        IMonster m2Copy = m2.copy();
        IBattle battle = battleManager.startBattle(m1Copy, m2Copy);
        String battleId = "battle-" + battleCounter.getAndIncrement();
        activeBattles.put(battleId, battle);

        // Store original monster IDs for XP rewards later
        battleMonsterIds.put(battleId + "_m1", monster1Id);
        battleMonsterIds.put(battleId + "_m2", monster2Id);

        return battleId;
    }

    public IBattle getBattle(String battleId) {
        return activeBattles.get(battleId);
    }

    public List<IBattle> getAllBattles() {
        return new ArrayList<>(activeBattles.values());
    }

    public int getBattleCount() {
        return activeBattles.size();
    }

    public boolean removeBattle(String battleId) {
        return activeBattles.remove(battleId) != null;
    }

    public IBattleManager getBattleManager() {
        return battleManager;
    }

    // ===== Evolution Management =====

    public boolean canEvolve(String monsterId) {
        IMonster monster = userMonsters.get(monsterId);
        return monster != null && evolutionManager.canEvolve(monster);
    }

    public IMonster evolveMonster(String monsterId) {
        IMonster monster = userMonsters.get(monsterId);
        if (monster == null) throw new IllegalArgumentException("Monster not found");
        IMonster evolved = evolutionManager.evolve(monster);
        userMonsters.put(monsterId, evolved);
        return evolved;
    }

    public List<IMonster> getEvolvableMonsters() {
        return userMonsters.values().stream()
                .filter(evolutionManager::canEvolve)
                .collect(Collectors.toList());
    }

    public List<IMonster> getMonstersByLevel() {
        List<IMonster> monsters = new ArrayList<>(userMonsters.values());
        // Filter out null monsters before sorting
        monsters.removeIf(m -> m == null);
        monsters.sort(Comparator.comparingInt(IMonster::getLevel).reversed());
        return monsters;
    }

    public void addExperience(String monsterId, int xp) {
        IMonster monster = userMonsters.get(monsterId);
        if (monster != null) monster.addExperience(xp);
    }

    public IEvolutionManager getEvolutionManager() {
        return evolutionManager;
    }

    public BattleReward awardBattleXp(String battleId) {
        // Check if XP already awarded
        if (battlesWithXpAwarded.contains(battleId)) {
            return null;
        }

        IBattle battle = activeBattles.get(battleId);
        if (battle == null || !battle.isBattleOver()) {
            return null;
        }

        IMonster winner = battle.getWinner();
        if (winner == null) {
            return null;
        }

        // Find which monster won (monster1 or monster2)
        String winnerId;
        if (winner.getName().equals(battle.getMonster1().getName()) &&
            winner.getType() == battle.getMonster1().getType()) {
            winnerId = battleMonsterIds.get(battleId + "_m1");
        } else {
            winnerId = battleMonsterIds.get(battleId + "_m2");
        }

        IMonster originalMonster = userMonsters.get(winnerId);
        if (originalMonster == null) {
            return null;
        }

        // Record level before XP
        int levelBefore = originalMonster.getLevel();
        boolean couldEvolveBefore = evolutionManager.canEvolve(originalMonster);

        // Award 50 XP
        originalMonster.addExperience(50);

        // Check what changed
        int levelAfter = originalMonster.getLevel();
        boolean canEvolveAfter = evolutionManager.canEvolve(originalMonster);

        // Mark XP as awarded
        battlesWithXpAwarded.add(battleId);

        return new BattleReward(
            winnerId,
            originalMonster.getName(),
            50,
            levelBefore,
            levelAfter,
            levelAfter > levelBefore,
            canEvolveAfter && !couldEvolveBefore
        );
    }

    public void clearAll() {
        userMonsters.clear();
        activeBattles.clear();
        battleMonsterIds.clear();
        battlesWithXpAwarded.clear();
        monsterCounter.set(1);
        battleCounter.set(1);
    }

    // Helper class to return battle reward information
    public static class BattleReward {
        public final String monsterId;
        public final String monsterName;
        public final int xpGained;
        public final int levelBefore;
        public final int levelAfter;
        public final boolean leveledUp;
        public final boolean canNowEvolve;

        public BattleReward(String monsterId, String monsterName, int xpGained,
                           int levelBefore, int levelAfter, boolean leveledUp, boolean canNowEvolve) {
            this.monsterId = monsterId;
            this.monsterName = monsterName;
            this.xpGained = xpGained;
            this.levelBefore = levelBefore;
            this.levelAfter = levelAfter;
            this.leveledUp = leveledUp;
            this.canNowEvolve = canNowEvolve;
        }
    }
}
