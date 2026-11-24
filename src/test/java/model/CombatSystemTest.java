package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Checkpoint 2: Combat System
 * Tests the IBattleManager and IBattle implementations.
 */
class CombatSystemTest {
    private IMonsterFactory factory;
    private IBattleManager battleManager;

    @BeforeEach
    void setUp() {
        factory = new MonsterFactory();
        battleManager = new BattleManager(12345L); // Use seed for predictable tests
    }

    @Test
    void testStartBattle() {
        IMonster monster1 = factory.createStarterMonster(MonsterType.FIRE);
        IMonster monster2 = factory.createStarterMonster(MonsterType.WATER);

        IBattle battle = battleManager.startBattle(monster1, monster2);

        assertNotNull(battle,
            "startBattle() should return a battle object, not null");
        assertNotNull(battle.getMonster1(),
            "Battle should have monster1 set");
        assertNotNull(battle.getMonster2(),
            "Battle should have monster2 set");
        assertFalse(battle.isBattleOver(),
            "New battle should not be over yet");
        assertEquals(0, battle.getTurnNumber(),
            "New battle should start at turn 0");
    }

    @Test
    void testTypeEffectiveness() {
        // Test fire vs grass (super effective)
        assertEquals(2.0, battleManager.getTypeEffectiveness(MonsterType.FIRE, MonsterType.GRASS),
            "FIRE should be super effective (2.0x) against GRASS");

        // Test fire vs water (not very effective)
        assertEquals(0.5, battleManager.getTypeEffectiveness(MonsterType.FIRE, MonsterType.WATER),
            "FIRE should be not very effective (0.5x) against WATER");

        // Test water vs fire (super effective)
        assertEquals(2.0, battleManager.getTypeEffectiveness(MonsterType.WATER, MonsterType.FIRE),
            "WATER should be super effective (2.0x) against FIRE");

        // Test water vs grass (not very effective)
        assertEquals(0.5, battleManager.getTypeEffectiveness(MonsterType.WATER, MonsterType.GRASS),
            "WATER should be not very effective (0.5x) against GRASS");

        // Test grass vs water (super effective)
        assertEquals(2.0, battleManager.getTypeEffectiveness(MonsterType.GRASS, MonsterType.WATER),
            "GRASS should be super effective (2.0x) against WATER");

        // Test grass vs fire (not very effective)
        assertEquals(0.5, battleManager.getTypeEffectiveness(MonsterType.GRASS, MonsterType.FIRE),
            "GRASS should be not very effective (0.5x) against FIRE");

        // Test electric vs water (super effective)
        assertEquals(2.0, battleManager.getTypeEffectiveness(MonsterType.ELECTRIC, MonsterType.WATER),
            "ELECTRIC should be super effective (2.0x) against WATER");

        // Test normal vs anything (neutral)
        assertEquals(1.0, battleManager.getTypeEffectiveness(MonsterType.NORMAL, MonsterType.FIRE),
            "NORMAL should be neutral (1.0x) against all types");
        assertEquals(1.0, battleManager.getTypeEffectiveness(MonsterType.FIRE, MonsterType.NORMAL),
            "All types should be neutral (1.0x) against NORMAL");
    }

    @Test
    void testCalculateDamage() {
        IMonster attacker = factory.createMonster("Attacker", MonsterType.FIRE, 100, 100, 50, 50);
        IMonster defender = factory.createMonster("Defender", MonsterType.WATER, 100, 50, 50, 50);

        int damage = battleManager.calculateDamage(attacker, defender);

        assertTrue(damage > 0,
            "Damage should be positive (calculated: " + damage + ")");
        assertTrue(damage <= attacker.getAttack() * 2,
            "Damage (" + damage + ") should not exceed attacker's attack stat (" + attacker.getAttack() + ") * 2");
    }

    @Test
    void testBattleAttack() {
        IMonster monster1 = factory.createStarterMonster(MonsterType.FIRE);
        IMonster monster2 = factory.createStarterMonster(MonsterType.WATER);

        IBattle battle = battleManager.startBattle(monster1, monster2);

        IMonster firstAttacker = battle.getCurrentAttacker();
        int defenderHpBefore = (firstAttacker == battle.getMonster1()) ?
            battle.getMonster2().getCurrentHp() : battle.getMonster1().getCurrentHp();

        AttackResult result = battle.attack();

        assertNotNull(result,
            "attack() should return an AttackResult, not null");
        assertTrue(result.getDamage() > 0,
            "Attack should deal positive damage (damage: " + result.getDamage() + ")");
        assertEquals(1, battle.getTurnNumber(),
            "Turn number should increment to 1 after first attack");

        // Verify defender took damage
        IMonster defender = (firstAttacker == battle.getMonster1()) ?
            battle.getMonster2() : battle.getMonster1();
        assertTrue(defender.getCurrentHp() < defenderHpBefore,
            "Defender HP should decrease after attack (was " + defenderHpBefore + ", now " + defender.getCurrentHp() + ")");
    }

    @Test
    void testBattleToCompletion() {
        IMonster monster1 = factory.createMonster("Strong", MonsterType.FIRE, 100, 100, 50, 100);
        IMonster monster2 = factory.createMonster("Weak", MonsterType.GRASS, 50, 20, 20, 20);

        IBattle battle = battleManager.startBattle(monster1, monster2);

        int maxTurns = 100; // Prevent infinite loops
        int turns = 0;

        while (!battle.isBattleOver() && turns < maxTurns) {
            battle.attack();
            turns++;
        }

        assertTrue(battle.isBattleOver(),
            "Battle should eventually end (ran for " + turns + " turns)");
        assertNotNull(battle.getWinner(),
            "There should be a winner when battle is over");
        assertTrue(battle.getWinner().getCurrentHp() > 0,
            "Winner should have HP remaining (winner HP: " + battle.getWinner().getCurrentHp() + ")");
        assertTrue(
            battle.getMonster1().isFainted() || battle.getMonster2().isFainted(),
            "One monster should be fainted (Monster1 HP: " + battle.getMonster1().getCurrentHp() +
            ", Monster2 HP: " + battle.getMonster2().getCurrentHp() + ")"
        );
    }

    @Test
    void testBattleLog() {
        IMonster monster1 = factory.createStarterMonster(MonsterType.FIRE);
        IMonster monster2 = factory.createStarterMonster(MonsterType.WATER);

        IBattle battle = battleManager.startBattle(monster1, monster2);

        assertTrue(battle.getBattleLog().isEmpty(),
            "Battle log should be empty at start");

        battle.attack();
        assertEquals(1, battle.getBattleLog().size(),
            "Battle log should have 1 entry after first attack");

        battle.attack();
        assertEquals(2, battle.getBattleLog().size(),
            "Battle log should have 2 entries after second attack");

        List<AttackResult> log = battle.getBattleLog();
        for (AttackResult result : log) {
            assertNotNull(result.getAttackerName(),
                "AttackResult should have attacker name");
            assertNotNull(result.getDefenderName(),
                "AttackResult should have defender name");
            assertTrue(result.getDamage() > 0,
                "AttackResult should record positive damage");
        }
    }

    @Test
    void testSpeedDeterminesTurnOrder() {
        IMonster fast = factory.createMonster("Fast", MonsterType.ELECTRIC, 100, 50, 50, 150);
        IMonster slow = factory.createMonster("Slow", MonsterType.NORMAL, 100, 50, 50, 30);

        IBattle battle = battleManager.startBattle(fast, slow);

        assertEquals(fast.getName(), battle.getCurrentAttacker().getName(),
            "Faster monster should attack first");
    }

    @Test
    void testCannotAttackAfterBattleEnds() {
        IMonster monster1 = factory.createMonster("Test1", MonsterType.FIRE, 1, 100, 10, 50);
        IMonster monster2 = factory.createMonster("Test2", MonsterType.WATER, 1, 100, 10, 40);

        IBattle battle = battleManager.startBattle(monster1, monster2);

        while (!battle.isBattleOver()) {
            battle.attack();
        }

        assertThrows(IllegalStateException.class, battle::attack,
            "Should not be able to attack after battle is over");
    }

    @Test
    void testAttackResultDetails() {
        IMonster fire = factory.createStarterMonster(MonsterType.FIRE);
        IMonster grass = factory.createStarterMonster(MonsterType.GRASS);

        IBattle battle = battleManager.startBattle(fire, grass);

        // Make sure fire attacks first
        if (battle.getCurrentAttacker().getName().equals(fire.getName())) {
            AttackResult result = battle.attack();

            assertEquals(fire.getName(), result.getAttackerName(),
                "AttackResult should have correct attacker name");
            assertEquals(grass.getName(), result.getDefenderName(),
                "AttackResult should have correct defender name");
            assertEquals(2.0, result.getEffectiveness(),
                "Fire should be super effective (2.0x) against Grass");
            assertTrue(result.getDamage() > 0,
                "Attack should deal positive damage");
            assertEquals("It's super effective!", result.getEffectivenessMessage(),
                "Super effective attacks should show correct message");
        }
    }

    @Test
    void testBattleDoesNotModifyOriginalMonsters() {
        IMonster original1 = factory.createStarterMonster(MonsterType.FIRE);
        IMonster original2 = factory.createStarterMonster(MonsterType.WATER);

        int originalHp1 = original1.getCurrentHp();
        int originalHp2 = original2.getCurrentHp();

        IBattle battle = battleManager.startBattle(original1, original2);
        battle.attack();
        battle.attack();

        // Original monsters should not be affected
        assertEquals(originalHp1, original1.getCurrentHp(),
            "Battle should work on copies - original monster1's HP should not change");
        assertEquals(originalHp2, original2.getCurrentHp(),
            "Battle should work on copies - original monster2's HP should not change");
    }

    @Test
    void testWinnerDetermination() {
        IMonster strong = factory.createMonster("Strong", MonsterType.FIRE, 100, 100, 50, 50);
        IMonster weak = factory.createMonster("Weak", MonsterType.GRASS, 20, 20, 20, 20);

        IBattle battle = battleManager.startBattle(strong, weak);

        assertNull(battle.getWinner(),
            "getWinner() should return null before battle ends");

        while (!battle.isBattleOver()) {
            battle.attack();
        }

        IMonster winner = battle.getWinner();
        assertNotNull(winner,
            "getWinner() should return the winning monster after battle ends");
        assertTrue(winner.getCurrentHp() > 0,
            "Winner should have HP remaining (winner HP: " + winner.getCurrentHp() + ")");
    }

    @Test
    void testBattleBetweenIdenticalMonsters() {
        // Create two monsters with identical stats (lower HP for faster test completion)
        IMonster monster1 = factory.createMonster("Twin1", MonsterType.NORMAL, 30, 50, 40, 50);
        IMonster monster2 = factory.createMonster("Twin2", MonsterType.NORMAL, 30, 50, 40, 50);

        IBattle battle = battleManager.startBattle(monster1, monster2);

        int maxTurns = 100;
        int turns = 0;

        while (!battle.isBattleOver() && turns < maxTurns) {
            battle.attack();
            turns++;
        }

        assertTrue(battle.isBattleOver(),
            "Battle between identical monsters should eventually end");
        assertNotNull(battle.getWinner(),
            "There should be a winner even with identical stats");
        assertTrue(turns > 1,
            "Battle between identical monsters should take multiple turns (took " + turns + " turns)");
    }

    @Test
    void testBattleWithHighDefense() {
        // Defender has higher defense than attacker's attack - should still deal minimum damage
        IMonster attacker = factory.createMonster("WeakAttacker", MonsterType.FIRE, 100, 20, 50, 60);
        IMonster defender = factory.createMonster("TankDefender", MonsterType.WATER, 100, 50, 100, 40);

        IBattle battle = battleManager.startBattle(attacker, defender);

        // First attack should be from attacker (higher speed)
        AttackResult result = battle.attack();

        assertTrue(result.getDamage() >= 1,
            "Damage should be at least 1 even when defense > attack (damage: " + result.getDamage() + ")");

        // Battle should still be able to complete
        int maxTurns = 200;
        int turns = 1;

        while (!battle.isBattleOver() && turns < maxTurns) {
            battle.attack();
            turns++;
        }

        assertTrue(battle.isBattleOver(),
            "Battle should eventually end even with high defense (ran for " + turns + " turns)");
    }

    @Test
    void testBattleLowLevelVsHighLevel() {
        // Create a low-level monster (base stats)
        IMonster lowLevel = factory.createStarterMonster(MonsterType.FIRE);

        // Create a high-level monster (much higher stats simulating leveling)
        IMonster highLevel = factory.createMonster("HighLevel", MonsterType.WATER, 150, 100, 80, 90);

        IBattle battle = battleManager.startBattle(lowLevel, highLevel);

        int maxTurns = 100;
        int turns = 0;

        while (!battle.isBattleOver() && turns < maxTurns) {
            battle.attack();
            turns++;
        }

        assertTrue(battle.isBattleOver(),
            "Battle should end");
        assertEquals("HighLevel", battle.getWinner().getName(),
            "High-level monster should win against low-level monster");
    }

    @Test
    void testMultipleConsecutiveBattlesSameMonsters() {
        IMonster monster1 = factory.createStarterMonster(MonsterType.FIRE);
        IMonster monster2 = factory.createStarterMonster(MonsterType.GRASS);

        int monster1OriginalHp = monster1.getCurrentHp();
        int monster2OriginalHp = monster2.getCurrentHp();

        // Run 3 consecutive battles with the same monsters
        for (int battleNum = 1; battleNum <= 3; battleNum++) {
            IBattle battle = battleManager.startBattle(monster1, monster2);

            while (!battle.isBattleOver()) {
                battle.attack();
            }

            assertNotNull(battle.getWinner(),
                "Battle " + battleNum + " should have a winner");

            // Original monsters should not be modified (battle uses copies)
            assertEquals(monster1OriginalHp, monster1.getCurrentHp(),
                "Original monster1 HP should not change after battle " + battleNum);
            assertEquals(monster2OriginalHp, monster2.getCurrentHp(),
                "Original monster2 HP should not change after battle " + battleNum);
        }
    }

    @Test
    void testCriticalHitOccursAndIsRecorded() {
        // Use high-speed monster to maximize crit chance (30% at speed 150+)
        IMonster fast = factory.createMonster("FastCritter", MonsterType.ELECTRIC, 200, 50, 50, 150);
        IMonster slow = factory.createMonster("SlowTarget", MonsterType.NORMAL, 200, 50, 50, 30);

        // Run multiple battles until we see at least one crit
        boolean foundCrit = false;
        int totalAttacks = 0;
        int maxAttempts = 500;

        for (int attempt = 0; attempt < maxAttempts && !foundCrit; attempt++) {
            // Use different seeds to get different random sequences
            IBattleManager seededManager = new BattleManager(attempt);
            IBattle battle = seededManager.startBattle(fast, slow);

            while (!battle.isBattleOver() && !foundCrit) {
                AttackResult result = battle.attack();
                totalAttacks++;

                if (result.isCritical()) {
                    foundCrit = true;
                    // Verify the AttackResult correctly reports the crit
                    assertTrue(result.isCritical(),
                        "isCritical() should return true for critical hits");
                    assertTrue(result.toString().contains("Critical hit!"),
                        "Critical hit message should appear in toString()");
                }
            }
        }

        assertTrue(foundCrit,
            "With 30% crit chance, should see at least one crit in " + totalAttacks + " attacks");
    }

}
