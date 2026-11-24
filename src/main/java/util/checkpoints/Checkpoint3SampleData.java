package util.checkpoints;

import model.IMonster;
import model.MonsterType;
import web.checkpoint3.Checkpoint3DataStore;

/**
 * Generates sample data for Checkpoint 3 app.
 * Creates monsters at various levels to demonstrate progression.
 */
public class Checkpoint3SampleData {
    private final Checkpoint3DataStore dataStore;

    public Checkpoint3SampleData(Checkpoint3DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void generateSampleData() {
        try {
            // FIRE - Level 15 (on verge of first evolution at 16)
            IMonster fire = createStarterWithLevel(MonsterType.FIRE, 15);
            if (fire != null) {
                fire.addExperience(1550);
            }

            // WATER - Level 1 (just created starter)
            IMonster water = createStarterWithLevel(MonsterType.WATER, 1);
            if (water != null) {

                water.addExperience(150);
            }

            // GRASS - Level 31, already evolved once (on verge of final evolution at 32)
            IMonster grass = createStarterWithLevel(MonsterType.GRASS, 31);
            if (grass != null && grass.getLevel() >= 16) {
                String id = grass.getId();
                if (dataStore.canEvolve(id)) {
                    dataStore.evolveMonster(id);
                    grass = dataStore.getMonster(id); // Get the evolved monster
                }
                grass.addExperience(3150);
            }

            // ELECTRIC - Level 25, already evolved once (mid-stage 2)
            IMonster electric = createStarterWithLevel(MonsterType.ELECTRIC, 25);
            if (electric != null && electric.getLevel() >= 16) {
                String id = electric.getId();
                if (dataStore.canEvolve(id)) {
                    dataStore.evolveMonster(id);
                    electric = dataStore.getMonster(id); // Get the evolved monster
                }
                electric.addExperience(1150);
            }

            // NORMAL - Level 10 (mid-stage 1)
            IMonster normal = createStarterWithLevel(MonsterType.NORMAL, 10);
            if (normal != null) {
                normal.addExperience(325);
            }

            System.out.println("Checkpoint 3 sample data generated: " + dataStore.getMonsterCount() + " monsters at various levels");

        } catch (Exception e) {
            System.err.println("Could not generate sample data for Checkpoint 3");
            System.err.println("This is expected if EvolutionManager is not yet implemented");
            System.err.println("Error: " + e.getMessage());
        }
    }

    private IMonster createStarterWithLevel(MonsterType type, int targetLevel) {
        try {
            IMonster monster = dataStore.getMonsterFactory().createStarterMonster(type);
            
            // Add experience to reach target level
            while (monster.getLevel() < targetLevel) {
                int xpNeeded = (monster.getLevel() + 1) * 100;
                monster.addExperience(xpNeeded);
            }
            
            dataStore.addMonster(monster);
            return monster;
        } catch (Exception e) {
            System.err.println("Could not create leveled monster: " + e.getMessage());
            return null;
        }
    }
}
