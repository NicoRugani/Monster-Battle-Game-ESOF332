package util.checkpoints;

import model.IMonster;
import model.MonsterType;
import web.checkpoint2.Checkpoint2DataStore;

/**
 * Generates sample data for Checkpoint 2 app.
 * Creates monsters and demonstrates battle functionality.
 */
public class Checkpoint2SampleData {
    private final Checkpoint2DataStore dataStore;

    public Checkpoint2SampleData(Checkpoint2DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void generateSampleData() {
        try {
            // Create starters of each type
            createStarter(MonsterType.FIRE);
            createStarter(MonsterType.WATER);
            createStarter(MonsterType.GRASS);
            createStarter(MonsterType.ELECTRIC);
            createStarter(MonsterType.NORMAL);

            System.out.println("Checkpoint 2 sample data generated: " + dataStore.getMonsterCount() + " monsters created");

        } catch (Exception e) {
            System.err.println("Warning: Could not generate sample data for Checkpoint 2");
            System.err.println("This is expected if Battle/BattleManager are not yet implemented");
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void createStarter(MonsterType type) {
        try {
            IMonster monster = dataStore.getMonsterFactory().createStarterMonster(type);
            dataStore.addMonster(monster);
        } catch (Exception e) {
            System.err.println("Could not create starter monster of type " + type + ": " + e.getMessage());
        }
    }

    private void createCustomMonster(String name, MonsterType type, int hp, int attack, int defense, int speed) {
        try {
            IMonster monster = dataStore.getMonsterFactory().createMonster(name, type, hp, attack, defense, speed);
            dataStore.addMonster(monster);
        } catch (Exception e) {
            System.err.println("Could not create custom monster " + name + ": " + e.getMessage());
        }
    }
}
