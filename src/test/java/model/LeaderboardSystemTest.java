package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Checkpoint 4: Leaderboard System
 * Tests the IPlayer and ILeaderboard implementations.
 */
class LeaderboardSystemTest {
    private IMonsterFactory factory;
    private ILeaderboard leaderboard;

    @BeforeEach
    void setUp() {
        factory = new MonsterFactory();
        leaderboard = new Leaderboard();
    }

    @Test
    void testCreatePlayer() {
        IPlayer player = new Player("TestPlayer");

        assertNotNull(player.getId(),
            "Player should have a non-null ID");
        assertEquals("TestPlayer", player.getUsername(),
            "Player username should match constructor parameter");
        assertEquals(0, player.getWins(),
            "New player should start with 0 wins");
        assertEquals(0, player.getLosses(),
            "New player should start with 0 losses");
        assertTrue(player.getMonsters().isEmpty(),
            "New player should start with no monsters");
    }

    @Test
    void testPlayerAddMonster() {
        IPlayer player = new Player("TestPlayer");
        IMonster monster = factory.createStarterMonster(MonsterType.FIRE);

        player.addMonster(monster);

        assertEquals(1, player.getMonsters().size(),
            "Player should have 1 monster after adding one");
        assertTrue(player.getMonsters().contains(monster),
            "Player's monster list should contain the added monster");
    }

    @Test
    void testPlayerRecordWinLoss() {
        IPlayer player = new Player("TestPlayer");

        player.recordWin();
        assertEquals(1, player.getWins(),
            "Player should have 1 win after recordWin()");
        assertEquals(0, player.getLosses(),
            "Player should still have 0 losses");

        player.recordWin();
        player.recordLoss();
        assertEquals(2, player.getWins(),
            "Player should have 2 wins after second recordWin()");
        assertEquals(1, player.getLosses(),
            "Player should have 1 loss after recordLoss()");
    }

    @Test
    void testPlayerWinRate() {
        IPlayer player = new Player("TestPlayer");

        assertEquals(0.0, player.getWinRate(), 0.01,
            "Player with no games should have 0.0% win rate");

        player.recordWin();
        player.recordWin();
        player.recordLoss();

        assertEquals(66.67, player.getWinRate(), 0.1,
            "Player with 2 wins and 1 loss should have ~66.67% win rate (actual: " + player.getWinRate() + "%)");

        player.recordLoss();
        assertEquals(50.0, player.getWinRate(), 0.01,
            "Player with 2 wins and 2 losses should have 50.0% win rate (actual: " + player.getWinRate() + "%)");
    }

    @Test
    void testPlayerScore() {
        IPlayer player = new Player("TestPlayer");

        assertEquals(0, player.getScore(),
            "New player should have score of 0");

        player.recordWin();
        assertEquals(1, player.getScore(),
            "1 win, 0 losses should give score of 1");

        player.recordLoss();
        assertEquals(0, player.getScore(),
            "1 win, 1 loss should give score of 0");

        player.recordWin();
        player.recordWin();
        assertEquals(2, player.getScore(),
            "3 wins, 1 loss should give score of 2");
    }

    @Test
    void testAddPlayerToLeaderboard() {
        IPlayer player = new Player("TestPlayer");

        leaderboard.addPlayer(player);

        assertEquals(1, leaderboard.getTotalPlayers(),
            "Leaderboard should have 1 player after adding one");
        assertNotNull(leaderboard.getPlayer(player.getId()),
            "getPlayer() should return the added player by ID");
        assertNotNull(leaderboard.getPlayerByUsername("TestPlayer"),
            "getPlayerByUsername() should return the added player by username");
    }

    @Test
    void testCannotAddDuplicatePlayer() {
        IPlayer player1 = new Player("TestPlayer");

        leaderboard.addPlayer(player1);

        assertThrows(IllegalArgumentException.class, () -> leaderboard.addPlayer(player1),
            "Should not be able to add the same player instance twice");
    }

    @Test
    void testCannotAddPlayerWithDuplicateUsername() {
        IPlayer player1 = new Player("TestPlayer");
        IPlayer player2 = new Player("TestPlayer");

        leaderboard.addPlayer(player1);

        assertThrows(IllegalArgumentException.class, () -> leaderboard.addPlayer(player2),
            "Should not be able to add two players with the same username");
    }

    @Test
    void testGetPlayerByUsername() {
        IPlayer player = new Player("TestPlayer");
        leaderboard.addPlayer(player);

        IPlayer found = leaderboard.getPlayerByUsername("TestPlayer");
        assertNotNull(found,
            "getPlayerByUsername() should find player with exact username match");
        assertEquals(player.getId(), found.getId(),
            "Found player should have the same ID as original");

        // Test case insensitive
        found = leaderboard.getPlayerByUsername("testplayer");
        assertNotNull(found,
            "getPlayerByUsername() should be case-insensitive");

        IPlayer notFound = leaderboard.getPlayerByUsername("NonexistentPlayer");
        assertNull(notFound,
            "getPlayerByUsername() should return null for non-existent username");
    }

    @Test
    void testUpdatePlayer() {
        IPlayer player = new Player("TestPlayer");
        leaderboard.addPlayer(player);

        player.recordWin();
        leaderboard.updatePlayer(player);

        IPlayer updated = leaderboard.getPlayer(player.getId());
        assertEquals(1, updated.getWins(),
            "updatePlayer() should update player's stats in leaderboard");
    }

    @Test
    void testGetTopPlayers() {
        IPlayer player1 = new Player("Player1");
        player1.recordWin();
        player1.recordWin();

        IPlayer player2 = new Player("Player2");
        player2.recordWin();

        IPlayer player3 = new Player("Player3");

        leaderboard.addPlayer(player1);
        leaderboard.addPlayer(player2);
        leaderboard.addPlayer(player3);

        List<IPlayer> topPlayers = leaderboard.getTopPlayers(2);

        assertEquals(2, topPlayers.size(),
            "getTopPlayers(2) should return exactly 2 players");
        assertEquals(player1.getId(), topPlayers.get(0).getId(),
            "Top player should be Player1 with 2 wins");
        assertEquals(player2.getId(), topPlayers.get(1).getId(),
            "Second player should be Player2 with 1 win");
    }

    @Test
    void testGetAllPlayersSorted() {
        IPlayer player1 = new Player("Player1");
        player1.recordWin();

        IPlayer player2 = new Player("Player2");
        player2.recordWin();
        player2.recordWin();
        player2.recordWin();

        IPlayer player3 = new Player("Player3");
        player3.recordWin();
        player3.recordWin();

        leaderboard.addPlayer(player1);
        leaderboard.addPlayer(player2);
        leaderboard.addPlayer(player3);

        List<IPlayer> allPlayers = leaderboard.getAllPlayers();

        assertEquals(3, allPlayers.size(),
            "getAllPlayers() should return all 3 players");
        assertEquals(player2.getId(), allPlayers.get(0).getId(),
            "Player2 should be ranked #1 with 3 wins");
        assertEquals(player3.getId(), allPlayers.get(1).getId(),
            "Player3 should be ranked #2 with 2 wins");
        assertEquals(player1.getId(), allPlayers.get(2).getId(),
            "Player1 should be ranked #3 with 1 win");
    }

    @Test
    void testGetPlayerRank() {
        IPlayer player1 = new Player("Player1");
        player1.recordWin();
        player1.recordWin();
        player1.recordWin();

        IPlayer player2 = new Player("Player2");
        player2.recordWin();
        player2.recordWin();

        IPlayer player3 = new Player("Player3");
        player3.recordWin();

        leaderboard.addPlayer(player1);
        leaderboard.addPlayer(player2);
        leaderboard.addPlayer(player3);

        assertEquals(1, leaderboard.getPlayerRank(player1.getId()),
            "Player1 with 3 wins should be rank 1");
        assertEquals(2, leaderboard.getPlayerRank(player2.getId()),
            "Player2 with 2 wins should be rank 2");
        assertEquals(3, leaderboard.getPlayerRank(player3.getId()),
            "Player3 with 1 win should be rank 3");

        assertEquals(-1, leaderboard.getPlayerRank("nonexistent-id"),
            "getPlayerRank() should return -1 for non-existent player");
    }

    @Test
    void testLeaderboardSortingByScore() {
        IPlayer player1 = new Player("Player1");
        player1.recordWin();
        player1.recordLoss();
        // Score: 1 - 1 = 0

        IPlayer player2 = new Player("Player2");
        player2.recordWin();
        player2.recordWin();
        // Score: 2 - 0 = 2

        leaderboard.addPlayer(player1);
        leaderboard.addPlayer(player2);

        List<IPlayer> allPlayers = leaderboard.getAllPlayers();

        // Player2 should be higher because they have higher score (2 vs 0)
        assertEquals(player2.getId(), allPlayers.get(0).getId(),
            "Player2 should rank higher (score: " + player2.getScore() + ")");
        assertEquals(player1.getId(), allPlayers.get(1).getId(),
            "Player1 should rank lower (score: " + player1.getScore() + ")");
    }

    @Test
    void testLeaderboardSortingTiebreakerByWins() {
        // When scores are equal, total wins is the first tiebreaker
        IPlayer player1 = new Player("Player1");
        player1.recordWin();
        player1.recordWin();
        player1.recordWin();
        player1.recordLoss();
        // Score: 3 - 1 = 2, Wins: 3

        IPlayer player2 = new Player("Player2");
        player2.recordWin();
        player2.recordWin();
        // Score: 2 - 0 = 2, Wins: 2

        leaderboard.addPlayer(player1);
        leaderboard.addPlayer(player2);

        List<IPlayer> allPlayers = leaderboard.getAllPlayers();

        // Same score (2), but Player1 has more wins (3 vs 2)
        assertEquals(player1.getId(), allPlayers.get(0).getId(),
            "Player1 should rank higher (same score but more wins: 3 vs 2)");
        assertEquals(player2.getId(), allPlayers.get(1).getId(),
            "Player2 should rank lower (same score but fewer wins)");
    }

    @Test
    void testLeaderboardSortingTiebreakerByUsername() {
        // When scores AND wins are equal, username is the final tiebreaker
        IPlayer player1 = new Player("Alice");
        player1.recordWin();
        player1.recordWin();
        // Score: 2, Wins: 2

        IPlayer player2 = new Player("Bob");
        player2.recordWin();
        player2.recordWin();
        // Score: 2, Wins: 2

        leaderboard.addPlayer(player1);
        leaderboard.addPlayer(player2);

        List<IPlayer> allPlayers = leaderboard.getAllPlayers();

        // Same score (2), same wins (2), so alphabetical order by username
        assertEquals("Alice", allPlayers.get(0).getUsername(),
            "When scores and wins are equal, players should be sorted alphabetically (Alice before Bob)");
        assertEquals("Bob", allPlayers.get(1).getUsername(),
            "Bob should be ranked after Alice alphabetically");
    }

    @Test
    void testEmptyLeaderboard() {
        assertEquals(0, leaderboard.getTotalPlayers(),
            "Empty leaderboard should have 0 players");
        assertTrue(leaderboard.getAllPlayers().isEmpty(),
            "getAllPlayers() should return empty list for empty leaderboard");
        assertTrue(leaderboard.getTopPlayers(10).isEmpty(),
            "getTopPlayers() should return empty list for empty leaderboard");
    }

    @Test
    void testPlayerWithMultipleMonsters() {
        IPlayer player = new Player("TestPlayer");

        IMonster monster1 = factory.createStarterMonster(MonsterType.FIRE);
        IMonster monster2 = factory.createStarterMonster(MonsterType.WATER);
        IMonster monster3 = factory.createStarterMonster(MonsterType.GRASS);

        player.addMonster(monster1);
        player.addMonster(monster2);
        player.addMonster(monster3);

        assertEquals(3, player.getMonsters().size(),
            "Player should have 3 monsters after adding 3");
    }

    @Test
    void testPlayerScoreCalculation() {
        IPlayer player = new Player("TestPlayer");

        // Base score: 0 wins - 0 losses = 0
        assertEquals(0, player.getScore(),
            "New player should have score of 0");

        // Add 3 wins: 3 - 0 = 3
        player.recordWin();
        player.recordWin();
        player.recordWin();
        assertEquals(3, player.getScore(),
            "3 wins, 0 losses should give score of 3");

        // Add 1 loss: 3 - 1 = 2
        player.recordLoss();
        assertEquals(2, player.getScore(),
            "3 wins, 1 loss should give score of 2");

        // Add 2 more losses: 3 - 3 = 0
        player.recordLoss();
        player.recordLoss();
        assertEquals(0, player.getScore(),
            "3 wins, 3 losses should give score of 0");

        // Score can be negative: 3 - 4 = -1
        player.recordLoss();
        assertEquals(-1, player.getScore(),
            "3 wins, 4 losses should give score of -1");
    }

    @Test
    void testCheckpoint4SampleData() {
        web.checkpoint4.Checkpoint4DataStore dataStore = new web.checkpoint4.Checkpoint4DataStore(
            factory, new BattleManager(), new EvolutionManager(factory), leaderboard);
        util.checkpoints.Checkpoint4SampleData sampleData = new util.checkpoints.Checkpoint4SampleData(dataStore);
        sampleData.generateSampleData();

        // Should have created 5 players
        assertEquals(5, leaderboard.getTotalPlayers(),
            "Checkpoint4SampleData should create 5 players");

        // Verify players were created with monsters
        IPlayer ash = leaderboard.getPlayerByUsername("Ash");
        assertNotNull(ash,
            "Checkpoint4SampleData should create player 'Ash'");
        assertTrue(ash.getMonsters().size() > 0,
            "Ash should have at least one monster");
        assertTrue(ash.getWins() > 0 || ash.getLosses() > 0,
            "Ash should have some battle record");

        IPlayer professor = leaderboard.getPlayerByUsername("Professor");
        assertNotNull(professor,
            "Checkpoint4SampleData should create player 'Professor'");
        assertTrue(professor.getMonsters().size() > 0,
            "Professor should have at least one monster");

        // Verify all players have different IDs
        List<IPlayer> allPlayers = leaderboard.getAllPlayers();
        long uniqueIds = allPlayers.stream()
            .map(IPlayer::getId)
            .distinct()
            .count();
        assertEquals(5, uniqueIds,
            "All 5 players should have unique IDs");
    }

    @Test
    void testCheckpoint4SampleDataMonsterLevels() {
        web.checkpoint4.Checkpoint4DataStore dataStore = new web.checkpoint4.Checkpoint4DataStore(
            factory, new BattleManager(), new EvolutionManager(factory), leaderboard);
        util.checkpoints.Checkpoint4SampleData sampleData = new util.checkpoints.Checkpoint4SampleData(dataStore);
        sampleData.generateSampleData();

        // Ash should have high-level monsters (final forms)
        IPlayer ash = leaderboard.getPlayerByUsername("Ash");
        assertNotNull(ash,
            "Checkpoint4SampleData should create player 'Ash'");
        boolean hasHighLevelMonster = ash.getMonsters().stream()
            .anyMatch(m -> m.getLevel() >= 40);
        assertTrue(hasHighLevelMonster,
            "Ash should have at least one high-level monster (level >= 40)");

        // Professor should have mid-level monsters
        IPlayer professor = leaderboard.getPlayerByUsername("Professor");
        assertNotNull(professor,
            "Checkpoint4SampleData should create player 'Professor'");
        boolean hasMidLevelMonster = professor.getMonsters().stream()
            .anyMatch(m -> m.getLevel() >= 10 && m.getLevel() <= 30);
        assertTrue(hasMidLevelMonster,
            "Professor should have at least one mid-level monster (level 10-30)");
    }

    @Test
    void testPlayerVsPlayerBattleRecords() {
        IBattleManager battleManager = new BattleManager();

        IPlayer player1 = new Player("Player1");
        IMonster monster1 = factory.createMonster("Strong", MonsterType.FIRE, 200, 100, 50, 100);
        player1.addMonster(monster1);

        IPlayer player2 = new Player("Player2");
        IMonster monster2 = factory.createMonster("Weak", MonsterType.GRASS, 50, 20, 20, 20);
        player2.addMonster(monster2);

        leaderboard.addPlayer(player1);
        leaderboard.addPlayer(player2);

        // Simulate a battle
        IBattle battle = battleManager.startBattle(monster1, monster2);
        while (!battle.isBattleOver()) {
            battle.attack();
        }

        // Record results for both players
        IMonster winner = battle.getWinner();
        if (winner.getId().equals(monster1.getId())) {
            player1.recordWin();
            player2.recordLoss();
        } else {
            player2.recordWin();
            player1.recordLoss();
        }

        // Verify both players' records were updated
        assertEquals(1, player1.getWins() + player1.getLosses(),
            "Player1 should have exactly 1 game recorded (win or loss)");
        assertEquals(1, player2.getWins() + player2.getLosses(),
            "Player2 should have exactly 1 game recorded (win or loss)");
        assertEquals(player1.getWins(), player2.getLosses(),
            "Player1's wins should equal Player2's losses");
        assertEquals(player1.getLosses(), player2.getWins(),
            "Player1's losses should equal Player2's wins");
    }
}
