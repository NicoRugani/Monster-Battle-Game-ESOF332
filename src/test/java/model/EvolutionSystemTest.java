package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Checkpoint 3: Leveling and Evolution System
 * Tests the IEvolutionManager implementation and monster leveling.
 */
class EvolutionSystemTest {
    private IMonsterFactory factory;
    private IEvolutionManager evolutionManager;

    @BeforeEach
    void setUp() {
        factory = new MonsterFactory();
        evolutionManager = new EvolutionManager(factory);
    }

    @Test
    void testMonsterLevelUp() {
        IMonster monster = factory.createStarterMonster(MonsterType.FIRE);

        int originalLevel = monster.getLevel();
        int originalMaxHp = monster.getMaxHp();
        int originalAttack = monster.getAttack();
        int originalDefense = monster.getDefense();

        // Add enough experience to level up (level 2 requires 200 XP)
        monster.addExperience(200);

        assertEquals(originalLevel + 1, monster.getLevel(),
            "Monster should level up from " + originalLevel + " to " + (originalLevel + 1) + " after gaining 200 XP");
        assertTrue(monster.getMaxHp() > originalMaxHp,
            "Max HP should increase after leveling (was " + originalMaxHp + ", now " + monster.getMaxHp() + ")");
        assertTrue(monster.getAttack() > originalAttack,
            "Attack should increase after leveling (was " + originalAttack + ", now " + monster.getAttack() + ")");
        assertTrue(monster.getDefense() > originalDefense,
            "Defense should increase after leveling (was " + originalDefense + ", now " + monster.getDefense() + ")");
        assertEquals(monster.getMaxHp(), monster.getCurrentHp(),
            "Monster should be fully healed after leveling up (current HP: " + monster.getCurrentHp() + ", max HP: " + monster.getMaxHp() + ")");
    }

    @Test
    void testMultipleLevelUps() {
        IMonster monster = factory.createStarterMonster(MonsterType.FIRE);

        // Add enough experience for multiple levels
        monster.addExperience(500);

        assertTrue(monster.getLevel() > 1,
            "Monster should have leveled up at least once with 500 XP (current level: " + monster.getLevel() + ")");
        assertTrue(monster.getMaxHp() > 45,
            "Monster's max HP should have increased from starter value of 45 (current: " + monster.getMaxHp() + ")");
    }

    @Test
    void testGetNextEvolution() {
        IMonster flameling = factory.getMonsterByName("Flameling");
        IMonster pyroclaw = factory.getMonsterByName("Pyroclaw");
        IMonster infernoth = factory.getMonsterByName("Infernoth");

        assertEquals("Pyroclaw", evolutionManager.getNextEvolution(flameling),
            "Flameling (Stage 1) should evolve into Pyroclaw");
        assertEquals("Infernoth", evolutionManager.getNextEvolution(pyroclaw),
            "Pyroclaw (Stage 2) should evolve into Infernoth");
        assertNull(evolutionManager.getNextEvolution(infernoth),
            "Infernoth is the final evolution and should not have a next evolution");
    }

    @Test
    void testGetEvolutionLevel() {
        IMonster flameling = factory.getMonsterByName("Flameling");
        IMonster pyroclaw = factory.getMonsterByName("Pyroclaw");
        IMonster infernoth = factory.getMonsterByName("Infernoth");

        assertEquals(16, evolutionManager.getEvolutionLevel(flameling),
            "Flameling should evolve at level 16");
        assertEquals(32, evolutionManager.getEvolutionLevel(pyroclaw),
            "Pyroclaw should evolve at level 32");
        assertEquals(-1, evolutionManager.getEvolutionLevel(infernoth),
            "Infernoth is final form and should return -1 (no evolution level)");
    }

    @Test
    void testGetEvolutionStage() {
        IMonster flameling = factory.getMonsterByName("Flameling");
        IMonster pyroclaw = factory.getMonsterByName("Pyroclaw");
        IMonster infernoth = factory.getMonsterByName("Infernoth");

        assertEquals(1, evolutionManager.getEvolutionStage(flameling),
            "Flameling should be Stage 1 (starter form)");
        assertEquals(2, evolutionManager.getEvolutionStage(pyroclaw),
            "Pyroclaw should be Stage 2 (first evolution)");
        assertEquals(3, evolutionManager.getEvolutionStage(infernoth),
            "Infernoth should be Stage 3 (final evolution)");
    }

    @Test
    void testCannotEvolveAtLowLevel() {
        IMonster flameling = factory.getMonsterByName("Flameling");

        assertFalse(evolutionManager.canEvolve(flameling),
            "Flameling at level " + flameling.getLevel() + " should not be able to evolve (needs level 16)");
    }

    @Test
    void testCanEvolveAtCorrectLevel() {
        IMonster flameling = factory.getMonsterByName("Flameling");

        // Level up to evolution level (need 13500 exp to reach level 16)
        flameling.addExperience(13500);

        assertTrue(flameling.getLevel() >= 16,
            "Flameling should have reached level 16 or higher after gaining 13500 XP (current level: " + flameling.getLevel() + ")");
        assertTrue(evolutionManager.canEvolve(flameling),
            "Flameling at level " + flameling.getLevel() + " should be able to evolve");
    }

    @Test
    void testEvolveMonster() {
        IMonster flameling = factory.getMonsterByName("Flameling");

        // Level up to evolution level (need 13500 exp to reach level 16)
        flameling.addExperience(13500);

        assertTrue(evolutionManager.canEvolve(flameling),
            "Flameling should be ready to evolve at level " + flameling.getLevel());

        IMonster pyroclaw = evolutionManager.evolve(flameling);

        assertNotNull(pyroclaw,
            "evolve() should return the evolved monster (Pyroclaw), not null");
        assertEquals("Pyroclaw", pyroclaw.getName(),
            "Evolved monster should be named Pyroclaw");
        assertEquals(MonsterType.FIRE, pyroclaw.getType(),
            "Evolved monster should maintain FIRE type");
        assertTrue(pyroclaw.getLevel() >= 16,
            "Evolved monster should be at least level 16 (current: " + pyroclaw.getLevel() + ")");
        assertEquals(pyroclaw.getMaxHp(), pyroclaw.getCurrentHp(),
            "Monster should be fully healed after evolution (current HP: " + pyroclaw.getCurrentHp() + ", max HP: " + pyroclaw.getMaxHp() + ")");
    }

    @Test
    void testCannotEvolveFinalForm() {
        IMonster infernoth = factory.getMonsterByName("Infernoth");

        // Add lots of experience
        infernoth.addExperience(10000);

        assertFalse(evolutionManager.canEvolve(infernoth),
            "Infernoth is the final evolution and should not be able to evolve further");

        IMonster result = evolutionManager.evolve(infernoth);
        assertNull(result,
            "Attempting to evolve a final form should return null");
    }

    @Test
    void testEvolutionPreservesLevel() {
        IMonster flameling = factory.getMonsterByName("Flameling");

        // Level up beyond evolution level
        flameling.addExperience(15000);
        int levelBeforeEvolution = flameling.getLevel();

        IMonster emberon = evolutionManager.evolve(flameling);

        assertNotNull(emberon,
            "evolve() should return the evolved monster, not null");
        assertEquals(levelBeforeEvolution, emberon.getLevel(),
            "Evolution should preserve the monster's level (was " + levelBeforeEvolution + ", now " + emberon.getLevel() + ")");
    }

    @Test
    void testFullEvolutionChain() {
        // Test Fire evolution chain
        IMonster stage1 = factory.getMonsterByName("Flameling");
        stage1.addExperience(13500); // Reach level 16

        IMonster stage2 = evolutionManager.evolve(stage1);
        assertNotNull(stage2,
            "First evolution should return Pyroclaw, not null");
        assertEquals("Pyroclaw", stage2.getName(),
            "Stage 1 should evolve into Pyroclaw");

        // From level 16, need additional experience to reach level 32
        // Sum from level 17 to 32: 1700 + 1800 + ... + 3200 = 100 * (17+18+...+32) = 100 * 392 = 39200
        stage2.addExperience(39200);

        IMonster stage3 = evolutionManager.evolve(stage2);
        assertNotNull(stage3,
            "Second evolution should return Infernoth, not null");
        assertEquals("Infernoth", stage3.getName(),
            "Stage 2 should evolve into Infernoth");

        assertFalse(evolutionManager.canEvolve(stage3),
            "Infernoth (final form) should not be able to evolve further");
    }

    @Test
    void testWaterEvolutionChain() {
        IMonster aquabit = factory.getMonsterByName("Aquabit");
        assertEquals("Tidalwave", evolutionManager.getNextEvolution(aquabit),
            "Aquabit should evolve into Tidalwave");

        IMonster tidalwave = factory.getMonsterByName("Tidalwave");
        assertEquals("Tidalord", evolutionManager.getNextEvolution(tidalwave),
            "Tidalwave should evolve into Tidalord");

        IMonster tidalord = factory.getMonsterByName("Tidalord");
        assertNull(evolutionManager.getNextEvolution(tidalord),
            "Tidalord is the final form and should not have a next evolution");
    }

    @Test
    void testGrassEvolutionChain() {
        IMonster leafling = factory.getMonsterByName("Leafling");
        assertEquals("Vinewhip", evolutionManager.getNextEvolution(leafling),
            "Leafling should evolve into Vinewhip");

        IMonster vinewhip = factory.getMonsterByName("Vinewhip");
        assertEquals("Terravine", evolutionManager.getNextEvolution(vinewhip),
            "Vinewhip should evolve into Terravine");

        IMonster terravine = factory.getMonsterByName("Terravine");
        assertNull(evolutionManager.getNextEvolution(terravine),
            "Terravine is the final form and should not have a next evolution");
    }

    @Test
    void testElectricEvolutionChain() {
        IMonster sparkbit = factory.getMonsterByName("Sparkbit");
        assertEquals("Voltail", evolutionManager.getNextEvolution(sparkbit),
            "Sparkbit should evolve into Voltail");

        IMonster voltail = factory.getMonsterByName("Voltail");
        assertEquals("Thunderex", evolutionManager.getNextEvolution(voltail),
            "Voltail should evolve into Thunderex");

        IMonster thunderex = factory.getMonsterByName("Thunderex");
        assertNull(evolutionManager.getNextEvolution(thunderex),
            "Thunderex is the final form and should not have a next evolution");
    }

    @Test
    void testEvolvedMonsterIsStronger() {
        // Compare base forms to show evolution increases base stats
        IMonster baseFlamesling = factory.getMonsterByName("Flameling");
        IMonster basePyroclaw = factory.getMonsterByName("Pyroclaw");

        assertTrue(basePyroclaw.getMaxHp() > baseFlamesling.getMaxHp(),
            "Pyroclaw base HP (" + basePyroclaw.getMaxHp() + ") should be higher than Flameling base HP (" + baseFlamesling.getMaxHp() + ")");
        assertTrue(basePyroclaw.getAttack() > baseFlamesling.getAttack(),
            "Pyroclaw base Attack (" + basePyroclaw.getAttack() + ") should be higher than Flameling base Attack (" + baseFlamesling.getAttack() + ")");

        // Also test that evolved monster at same level is stronger
        IMonster flameling = factory.getMonsterByName("Flameling");
        flameling.addExperience(13500); // Reach level 16

        IMonster pyroclaw = evolutionManager.evolve(flameling);

        // Evolved form should maintain level and have better base stats
        assertEquals(flameling.getLevel(), pyroclaw.getLevel(),
            "Evolved monster should maintain the same level as before evolution");
        assertNotNull(pyroclaw,
            "Evolution should return the evolved monster, not null");
    }

    @Test
    void testExperienceRequirementIncreasesWithLevel() {
        IMonster monster = factory.createStarterMonster(MonsterType.FIRE);

        // Level 1 to 2 requires 200 exp
        monster.addExperience(199);
        assertEquals(1, monster.getLevel(),
            "Monster should still be level 1 with 199/200 XP");
        assertEquals(199, monster.getExperience(),
            "Monster should have 199 current XP");

        monster.addExperience(1);
        assertEquals(2, monster.getLevel(),
            "Monster should level up to 2 after reaching 200 XP");
        assertEquals(0, monster.getExperience(),
            "Current XP should reset to 0 after leveling up");

        // Level 2 to 3 requires 300 exp
        monster.addExperience(299);
        assertEquals(2, monster.getLevel(),
            "Monster should still be level 2 with 299/300 XP");
        assertEquals(299, monster.getExperience(),
            "Monster should have 299 current XP");

        monster.addExperience(1);
        assertEquals(3, monster.getLevel(),
            "Monster should level up to 3 after reaching 300 XP");
        assertEquals(0, monster.getExperience(),
            "Current XP should reset to 0 after leveling up");
    }

    @Test
    void testNormalEvolutionChain() {
        IMonster champ = factory.getMonsterByName("Champ");
        assertEquals("Prowler", evolutionManager.getNextEvolution(champ),
            "Champ should evolve into Prowler");

        IMonster prowler = factory.getMonsterByName("Prowler");
        assertEquals("Apexar", evolutionManager.getNextEvolution(prowler),
            "Prowler should evolve into Apexar");

        IMonster apexar = factory.getMonsterByName("Apexar");
        assertNull(evolutionManager.getNextEvolution(apexar),
            "Apexar is the final form and should not have a next evolution");
    }

    @Test
    void testSpeedIncreasesOnLevelUp() {
        IMonster monster = factory.createStarterMonster(MonsterType.FIRE);
        int originalSpeed = monster.getSpeed();

        // Level up once
        monster.addExperience(200);

        assertTrue(monster.getSpeed() > originalSpeed,
            "Speed should increase after leveling up (was " + originalSpeed + ", now " + monster.getSpeed() + ")");
    }

    @Test
    void testSpecificStatGrowthValues() {
        IMonster monster = factory.createStarterMonster(MonsterType.NORMAL);

        int originalHp = monster.getMaxHp();
        int originalAttack = monster.getAttack();
        int originalDefense = monster.getDefense();
        int originalSpeed = monster.getSpeed();

        // Level up once (200 XP for level 1 -> 2)
        monster.addExperience(200);

        assertEquals(originalHp + 5, monster.getMaxHp(),
            "HP should increase by 5 per level (was " + originalHp + ", expected " + (originalHp + 5) + ", got " + monster.getMaxHp() + ")");
        assertEquals(originalAttack + 2, monster.getAttack(),
            "Attack should increase by 2 per level (was " + originalAttack + ", expected " + (originalAttack + 2) + ", got " + monster.getAttack() + ")");
        assertEquals(originalDefense + 2, monster.getDefense(),
            "Defense should increase by 2 per level (was " + originalDefense + ", expected " + (originalDefense + 2) + ", got " + monster.getDefense() + ")");
        assertEquals(originalSpeed + 1, monster.getSpeed(),
            "Speed should increase by 1 per level (was " + originalSpeed + ", expected " + (originalSpeed + 1) + ", got " + monster.getSpeed() + ")");
    }

    @Test
    void testBattleXpReward() {
        IBattleManager battleManager = new BattleManager();

        // Create a strong monster that will definitely win
        IMonster strong = factory.createMonster("Strong", MonsterType.FIRE, 200, 100, 50, 100);
        IMonster weak = factory.createMonster("Weak", MonsterType.GRASS, 30, 20, 20, 20);

        IBattle battle = battleManager.startBattle(strong, weak);

        while (!battle.isBattleOver()) {
            battle.attack();
        }

        // The winner should automatically gain 50 XP when battle ends
        IMonster winner = battle.getWinner();
        assertNotNull(winner, "Battle should have a winner");
        assertEquals(50, winner.getExperience(),
            "Winner should automatically gain 50 XP from battle victory");
    }
}
