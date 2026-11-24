package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Checkpoint 1: Monster System
 * Tests the IMonster and IMonsterFactory implementations.
 */
class MonsterSystemTest {
    private IMonsterFactory factory;

    @BeforeEach
    void setUp() {
        factory = new MonsterFactory();
    }

    @Test
    void testCreateMonster() {
        IMonster monster = factory.createMonster("TestMonster", MonsterType.FIRE, 100, 50, 40, 60);

        assertNotNull(monster,
            "createMonster() should return a monster object, not null");
        assertEquals("TestMonster", monster.getName(),
            "Monster name should match the name passed to createMonster()");
        assertEquals(MonsterType.FIRE, monster.getType(),
            "Monster type should be FIRE as specified");
        assertEquals(100, monster.getMaxHp(),
            "Max HP should be 100 as specified in createMonster()");
        assertEquals(100, monster.getCurrentHp(),
            "New monster should start with current HP equal to max HP (100)");
        assertEquals(50, monster.getAttack(),
            "Attack should be 50 as specified in createMonster()");
        assertEquals(40, monster.getDefense(),
            "Defense should be 40 as specified in createMonster()");
        assertEquals(60, monster.getSpeed(),
            "Speed should be 60 as specified in createMonster()");
        assertFalse(monster.isFainted(),
            "New monster with full HP should not be fainted");
    }

    @Test
    void testCreateStarterMonsters() {
        IMonster fire = factory.createStarterMonster(MonsterType.FIRE);
        IMonster water = factory.createStarterMonster(MonsterType.WATER);
        IMonster grass = factory.createStarterMonster(MonsterType.GRASS);
        IMonster electric = factory.createStarterMonster(MonsterType.ELECTRIC);

        assertEquals(MonsterType.FIRE, fire.getType(),
            "Fire starter should have FIRE type");
        assertEquals(MonsterType.WATER, water.getType(),
            "Water starter should have WATER type");
        assertEquals(MonsterType.GRASS, grass.getType(),
            "Grass starter should have GRASS type");
        assertEquals(MonsterType.ELECTRIC, electric.getType(),
            "Electric starter should have ELECTRIC type");

        assertEquals("Flameling", fire.getName(),
            "Fire starter monster should be named Flameling");
        assertEquals("Aquabit", water.getName(),
            "Water starter monster should be named Aquabit");
        assertEquals("Leafling", grass.getName(),
            "Grass starter monster should be named Leafling");
        assertEquals("Sparkbit", electric.getName(),
            "Electric starter monster should be named Sparkbit");
    }

    @Test
    void testMonsterHpManagement() {
        IMonster monster = factory.createMonster("TestMonster", MonsterType.NORMAL, 100, 50, 40, 60);

        // Test taking damage
        monster.setCurrentHp(50);
        assertEquals(50, monster.getCurrentHp(),
            "Current HP should be 50 after setCurrentHp(50)");
        assertFalse(monster.isFainted(),
            "Monster with 50/100 HP should not be fainted");

        // Test healing
        monster.heal();
        assertEquals(100, monster.getCurrentHp(),
            "heal() should restore HP to max (100)");

        // Test fainting
        monster.setCurrentHp(0);
        assertTrue(monster.isFainted(),
            "Monster with 0 HP should be fainted");

        // Test HP bounds
        monster.setCurrentHp(-10);
        assertEquals(0, monster.getCurrentHp(),
            "Setting HP below 0 should clamp to 0 (not allow negative HP)");

        monster.setCurrentHp(150);
        assertEquals(100, monster.getCurrentHp(),
            "Setting HP above max (100) should clamp to max HP");
    }

    @Test
    void testMonsterCopy() {
        IMonster original = factory.createMonster("Original", MonsterType.FIRE, 100, 50, 40, 60);
        original.setCurrentHp(50);

        IMonster copy = original.copy();

        assertNotEquals(original.getId(), copy.getId(),
            "Copied monster should have a different ID than original (IDs: original=" + original.getId() + ", copy=" + copy.getId() + ")");
        assertEquals(original.getName(), copy.getName(),
            "Copied monster should have the same name as original");
        assertEquals(original.getType(), copy.getType(),
            "Copied monster should have the same type as original");
        assertEquals(original.getCurrentHp(), copy.getCurrentHp(),
            "Copied monster should have the same current HP as original");
        assertEquals(original.getMaxHp(), copy.getMaxHp(),
            "Copied monster should have the same max HP as original");

        // Verify they are independent
        copy.setCurrentHp(25);
        assertEquals(50, original.getCurrentHp(),
            "Modifying copy's HP should not affect original (original should still be 50)");
        assertEquals(25, copy.getCurrentHp(),
            "Copy's HP should be 25 after modification");
    }

    @Test
    void testGetAllMonsterTemplates() {
        List<IMonster> templates = factory.getAllMonsterTemplates();

        assertNotNull(templates,
            "getAllMonsterTemplates() should return a list, not null");
        assertTrue(templates.size() >= 5,
            "Should have at least 5 monster templates (found " + templates.size() + ")");

        // Verify each template is valid
        for (IMonster monster : templates) {
            assertNotNull(monster.getName(),
                "Monster template should have a name (not null)");
            assertNotNull(monster.getType(),
                "Monster template should have a type (not null)");
            assertTrue(monster.getMaxHp() > 0,
                "Monster '" + monster.getName() + "' should have positive max HP (found " + monster.getMaxHp() + ")");
            assertTrue(monster.getAttack() > 0,
                "Monster '" + monster.getName() + "' should have positive attack (found " + monster.getAttack() + ")");
            assertTrue(monster.getDefense() > 0,
                "Monster '" + monster.getName() + "' should have positive defense (found " + monster.getDefense() + ")");
            assertTrue(monster.getSpeed() > 0,
                "Monster '" + monster.getName() + "' should have positive speed (found " + monster.getSpeed() + ")");
        }
    }

    @Test
    void testGetMonsterByName() {
        IMonster flameling = factory.getMonsterByName("Flameling");
        assertNotNull(flameling,
            "getMonsterByName(\"Flameling\") should return a monster, not null");
        assertEquals("Flameling", flameling.getName(),
            "Retrieved monster should be named Flameling");
        assertEquals(MonsterType.FIRE, flameling.getType(),
            "Flameling should be FIRE type");

        IMonster aquabit = factory.getMonsterByName("Aquabit");
        assertNotNull(aquabit,
            "getMonsterByName(\"Aquabit\") should return a monster, not null");
        assertEquals("Aquabit", aquabit.getName(),
            "Retrieved monster should be named Aquabit");
        assertEquals(MonsterType.WATER, aquabit.getType(),
            "Aquabit should be WATER type");

        IMonster nonexistent = factory.getMonsterByName("NonexistentMonster");
        assertNull(nonexistent,
            "getMonsterByName() should return null for a monster that doesn't exist");
    }

    @Test
    void testUniqueIds() {
        IMonster monster1 = factory.createMonster("Test", MonsterType.NORMAL, 100, 50, 40, 60);
        IMonster monster2 = factory.createMonster("Test", MonsterType.NORMAL, 100, 50, 40, 60);

        assertNotEquals(monster1.getId(), monster2.getId(), "Each monster should have a unique ID");
    }
}
