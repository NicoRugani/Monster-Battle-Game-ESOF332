package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of IMonsterFactory interface.
 * Students will complete this implementation for Checkpoint 1.
 */
public class MonsterFactory implements IMonsterFactory {
    private final Map<String, IMonster> monsterTemplates;

    public MonsterFactory() {
        this.monsterTemplates = new HashMap<>();
        initializeMonsterTemplates();
    }

    private void initializeMonsterTemplates() {
        // Fire type monsters
        monsterTemplates.put("Flameling", new Monster("Flameling", MonsterType.FIRE, 45, 52, 43, 65));
        monsterTemplates.put("Pyroclaw", new Monster("Pyroclaw", MonsterType.FIRE, 58, 64, 58, 80));
        monsterTemplates.put("Infernoth", new Monster("Infernoth", MonsterType.FIRE, 78, 84, 78, 100));

        // Water type monsters
        monsterTemplates.put("Aquabit", new Monster("Aquabit", MonsterType.WATER, 44, 48, 65, 43));
        monsterTemplates.put("Tidalwave", new Monster("Tidalwave", MonsterType.WATER, 59, 63, 80, 58));
        monsterTemplates.put("Tidalord", new Monster("Tidalord", MonsterType.WATER, 79, 83, 100, 78));

        // Grass type monsters
        monsterTemplates.put("Leafling", new Monster("Leafling", MonsterType.GRASS, 45, 49, 49, 45));
        monsterTemplates.put("Vinewhip", new Monster("Vinewhip", MonsterType.GRASS, 60, 62, 63, 60));
        monsterTemplates.put("Terravine", new Monster("Terravine", MonsterType.GRASS, 80, 82, 83, 80));

        // Electric type monsters
        monsterTemplates.put("Sparkbit", new Monster("Sparkbit", MonsterType.ELECTRIC, 35, 55, 40, 90));
        monsterTemplates.put("Voltail", new Monster("Voltail", MonsterType.ELECTRIC, 50, 70, 55, 105));
        monsterTemplates.put("Thunderex", new Monster("Thunderex", MonsterType.ELECTRIC, 70, 90, 70, 125));

        // Normal type monsters
        monsterTemplates.put("Champ", new Monster("Champ", MonsterType.NORMAL, 50, 50, 50, 50));
        monsterTemplates.put("Prowler", new Monster("Prowler", MonsterType.NORMAL, 75, 75, 75, 75));
        monsterTemplates.put("Apexar", new Monster("Apexar", MonsterType.NORMAL, 95, 95, 95, 95));
    }

    @Override
    public IMonster createMonster(String name, MonsterType type, int maxHp, int attack, int defense, int speed) {
        return new Monster(name, type, maxHp, attack, defense, speed);
    }

    @Override
    public IMonster createStarterMonster(MonsterType type) {

        switch(type) {
            case ELECTRIC:
                return createMonster("Sparkbit", type, 35, 55, 40, 90);
            case FIRE:
                return createMonster("Flameling", type, 45, 52, 43, 65);
            case WATER:
                return createMonster("Aquabit", type, 44, 48, 65, 433);
            case GRASS:
                return createMonster("Leafling", type, 45, 49, 49, 45);
            case NORMAL:
                return createMonster("Champ", type, 50, 50, 50, 50);
            default:
                throw new IllegalArgumentException("Unknown monster type: " + type);
        }
    }

    @Override
    public List<IMonster> getAllMonsterTemplates() {
        // new list for the copies of the monsters
        List<IMonster> monsterTemplateList = new ArrayList<>();
        for(IMonster monster : monsterTemplates.values()){
            // Adds a copy of each monster to the list
            monsterTemplateList.add(new Monster(monster.getName(), monster.getType(), monster.getMaxHp(), monster.getAttack(), monster.getDefense(), monster.getSpeed()));
        }
        return monsterTemplateList;

    }

    @Override
    public IMonster getMonsterByName(String name) {
        IMonster template = monsterTemplates.get(name);
        return template != null ? template.copy(): null;
    }
}
