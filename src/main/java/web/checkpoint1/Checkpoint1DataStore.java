package web.checkpoint1;

import model.IMonster;
import model.IMonsterFactory;
import model.MonsterType;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * In-memory data storage for Checkpoint 1 app.
 * Manages user-created monsters.
 */
public class Checkpoint1DataStore {
    private final Map<String, IMonster> userMonsters;
    private final IMonsterFactory monsterFactory;
    private final AtomicInteger monsterCounter;

    public Checkpoint1DataStore(IMonsterFactory monsterFactory) {
        this.userMonsters = new HashMap<>();
        this.monsterFactory = monsterFactory;
        this.monsterCounter = new AtomicInteger(1);
    }

    /**
     * Add a monster to the data store.
     * @param monster The monster to add
     * @return The ID assigned to the monster
     */
    public String addMonster(IMonster monster) {
        String id = monster.getId();
        userMonsters.put(id, monster);
        return id;
    }

    /**
     * Get a monster by ID.
     * @param id The monster ID
     * @return The monster, or null if not found
     */
    public IMonster getMonster(String id) {
        return userMonsters.get(id);
    }

    /**
     * Get all user-created monsters.
     * @return List of all monsters
     */
    public List<IMonster> getAllMonsters() {
        return new ArrayList<>(userMonsters.values());
    }

    /**
     * Get monsters filtered by type.
     * @param type The monster type to filter by
     * @return List of monsters of the specified type
     */
    public List<IMonster> getMonstersByType(MonsterType type) {
        return userMonsters.values().stream()
                .filter(m -> m.getType() == type)
                .collect(Collectors.toList());
    }

    /**
     * Get the total number of monsters created.
     * @return Count of monsters
     */
    public int getMonsterCount() {
        return userMonsters.size();
    }

    /**
     * Clear all monsters from the data store.
     */
    public void clearAll() {
        userMonsters.clear();
        monsterCounter.set(1);
    }

    /**
     * Get the monster factory.
     * @return The monster factory
     */
    public IMonsterFactory getMonsterFactory() {
        return monsterFactory;
    }

    /**
     * Check if a monster exists.
     * @param id The monster ID
     * @return true if the monster exists
     */
    public boolean hasMonster(String id) {
        return userMonsters.containsKey(id);
    }

    /**
     * Remove a monster from the data store.
     * @param id The monster ID
     * @return true if the monster was removed
     */
    public boolean removeMonster(String id) {
        return userMonsters.remove(id) != null;
    }
}
