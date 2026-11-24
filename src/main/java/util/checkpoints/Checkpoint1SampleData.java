package util.checkpoints;

import model.IMonster;
import model.MonsterType;
import web.checkpoint1.Checkpoint1DataStore;

/**
 * Generates sample data for Checkpoint 1 app.
 * Creates starter monsters and a few custom examples.
 */
public class Checkpoint1SampleData {
    private final Checkpoint1DataStore dataStore;

    public Checkpoint1SampleData(Checkpoint1DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void generateSampleData() {
        try {
            // Create one of each starter type
            createStarter(MonsterType.FIRE);
            createStarter(MonsterType.WATER);
            createStarter(MonsterType.GRASS);
            createStarter(MonsterType.ELECTRIC);
            createStarter(MonsterType.NORMAL);

            System.out.println("Checkpoint 1 sample data generated: " + dataStore.getMonsterCount() + " monsters created");

        } catch (Exception e) {
            System.err.println("Could not generate sample data for Checkpoint 1");
            System.err.println("This is expected if Monster/MonsterFactory are not yet implemented");
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
