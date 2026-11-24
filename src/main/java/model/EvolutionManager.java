package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of IEvolutionManager interface.
 * Students will complete this implementation for Checkpoint 3.
 */
public class EvolutionManager implements IEvolutionManager {
    private final Map<String, EvolutionData> evolutionChain;
    private final IMonsterFactory monsterFactory;

    public EvolutionManager(IMonsterFactory monsterFactory) {
        this.monsterFactory = monsterFactory;
        this.evolutionChain = new HashMap<>();
        initializeEvolutionChains();
    }

    private void initializeEvolutionChains() {
        // Fire evolution chain
        evolutionChain.put("Flameling", new EvolutionData("Pyroclaw", 16, 1));
        evolutionChain.put("Pyroclaw", new EvolutionData("Infernoth", 32, 2));
        evolutionChain.put("Infernoth", new EvolutionData(null, -1, 3));

        // Water evolution chain
        evolutionChain.put("Aquabit", new EvolutionData("Tidalwave", 16, 1));
        evolutionChain.put("Tidalwave", new EvolutionData("Tidalord", 32, 2));
        evolutionChain.put("Tidalord", new EvolutionData(null, -1, 3));

        // Grass evolution chain
        evolutionChain.put("Leafling", new EvolutionData("Vinewhip", 16, 1));
        evolutionChain.put("Vinewhip", new EvolutionData("Terravine", 32, 2));
        evolutionChain.put("Terravine", new EvolutionData(null, -1, 3));

        // Electric evolution chain
        evolutionChain.put("Sparkbit", new EvolutionData("Voltail", 16, 1));
        evolutionChain.put("Voltail", new EvolutionData("Thunderex", 32, 2));
        evolutionChain.put("Thunderex", new EvolutionData(null, -1, 3));

        // Normal evolution chain (MSU Bobcat themed)
        evolutionChain.put("Champ", new EvolutionData("Prowler", 16, 1));
        evolutionChain.put("Prowler", new EvolutionData("Apexar", 32, 2));
        evolutionChain.put("Apexar", new EvolutionData(null, -1, 3));
    }

    @Override
    public boolean canEvolve(IMonster monster) {
        // Checks if next evolution exists and
        // if monster is at high enough level
        return (getNextEvolution(monster) != null) &&
        (monster.getLevel() >= getEvolutionLevel(monster));
    }

    @Override
    public IMonster evolve(IMonster monster) {     

        if(canEvolve(monster)){
            String nextName = getNextEvolution(monster);
            IMonster template = monsterFactory.getMonsterByName(nextName);

            // Create evolved monster with transferred level
            Monster newMonster = new Monster(
                monster.getId(),  // Preserve the ID
                template.getName(),
                template.getType(),
                template.getMaxHp(),
                template.getAttack(),
                template.getDefense(),
                template.getSpeed(),
                monster.getLevel()  // Transfer the current level (XP resets to 0)
            );

            // Apply level-up bonuses to the base stats
            int levelUps = monster.getLevel() - 1;
            newMonster.setMaxHp(template.getMaxHp() + (levelUps * 5));
            newMonster.setAttack(template.getAttack() + (levelUps * 2));
            newMonster.setDefense(template.getDefense() + (levelUps * 2));
            newMonster.setSpeed(template.getSpeed() + (levelUps * 1));
            newMonster.setCurrentHp(newMonster.getMaxHp());  // Fully heal after evolution

            return newMonster;
        } else{
            return null;
        }
     }

    @Override
    public String getNextEvolution(IMonster monster) {
        EvolutionData evData = evolutionChain.get(monster.getName());
        return evData.nextEvolution;

    }

    @Override
    public int getEvolutionLevel(IMonster monster) {
        EvolutionData evData = evolutionChain.get(monster.getName());
        return evData.levelRequired;
    }

    @Override
    public int getEvolutionStage(IMonster monster) {
        EvolutionData evData = evolutionChain.get(monster.getName());
        return evData.stage;
    }

    private static class EvolutionData {
        final String nextEvolution;
        final int levelRequired;
        final int stage;

        EvolutionData(String nextEvolution, int levelRequired, int stage) {
            this.nextEvolution = nextEvolution;
            this.levelRequired = levelRequired;
            this.stage = stage;
        }
    }
}
