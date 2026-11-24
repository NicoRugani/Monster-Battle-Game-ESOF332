package util.checkpoints;

import model.*;
import web.checkpoint4.Checkpoint4DataStore;

import java.util.Random;

/**
 * Generates sample data for Checkpoint 4 app.
 * Creates players with monster teams at various levels.
 */
public class Checkpoint4SampleData {
    private final Checkpoint4DataStore dataStore;
    private final Random random;

    public Checkpoint4SampleData(Checkpoint4DataStore dataStore) {
        this.dataStore = dataStore;
        this.random = new Random(42); // Fixed seed for consistent sample data
    }

    public void generateSampleData() {
        try {

            createPlayer("Professor", new String[]{"Flameling", "Aquabit", "Sparkbit"}, new int[]{25, 10, 5});
            createPlayer("Misty", new String[]{"Aquabit", "Leafling", "Champ"}, new int[]{20, 15, 8});
            createPlayer("Brock", new String[]{"Leafling", "Flameling", "Champ"}, new int[]{18, 12, 22});
            createPlayer("Gary", new String[]{"Sparkbit", "Flameling", "Aquabit"}, new int[]{35, 30, 28});
            createPlayer("Ash", new String[]{"Flameling", "Aquabit", "Sparkbit"}, new int[]{50, 45, 40});

            System.out.println("Checkpoint 4 sample data generated: 5 players with various monster teams");
        } catch (Exception e) {
            System.err.println("Warning: Could not generate sample data for Checkpoint 4");
            System.err.println("This is expected if Player/Leaderboard are not yet implemented");
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void createPlayer(String username, String[] monsterNames, int[] levels) {
        try {
            IPlayer player = new Player(username);

            for (int i = 0; i < monsterNames.length; i++) {
                String monsterName = monsterNames[i];
                int targetLevel = levels[i];

                // Get base monster
                IMonster monster = dataStore.getMonsterFactory().getMonsterByName(monsterName);
                if (monster != null) {
                    // Level up to target level
                    int expNeeded = calculateExpForLevel(targetLevel);
                    monster.addExperience(expNeeded);

                    player.addMonster(monster);
                }
            }

            // Add some random wins/losses
            int wins = random.nextInt(5) + (levels[0] / 10);
            int losses = random.nextInt(3) + 1;
            for (int i = 0; i < wins; i++) {
                player.recordWin();
            }
            for (int i = 0; i < losses; i++) {
                player.recordLoss();
            }

            dataStore.addPlayer(player);
        } catch (Exception e) {
            System.err.println("Could not create player " + username + ": " + e.getMessage());
        }
    }

    private int calculateExpForLevel(int targetLevel) {
        // XP for next level = (currentLevel + 1) × 100, cumulative
        int totalExp = 0;
        for (int level = 1; level < targetLevel; level++) {
            totalExp += (level + 1) * 100;
        }
        return totalExp;
    }
}
