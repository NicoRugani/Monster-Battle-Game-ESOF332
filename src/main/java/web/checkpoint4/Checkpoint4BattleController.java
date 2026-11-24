package web.checkpoint4;

import io.javalin.http.Context;
import model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for battle-related operations.
 */
public class Checkpoint4BattleController {
    private final Checkpoint4DataStore dataStore;
    private final Map<String, IBattle> activeBattles;
    private final Map<String, String> battlePlayerMap; // battleId -> playerId
    private final Map<String, String> battleOpponentMap; // battleId -> opponentPlayerId (null for wild battles)
    private final Map<String, String> battleMessages; // battleId -> special message (e.g., AI ran away)

    public Checkpoint4BattleController(Checkpoint4DataStore dataStore) {
        this.dataStore = dataStore;
        this.activeBattles = new HashMap<>();
        this.battlePlayerMap = new HashMap<>();
        this.battleOpponentMap = new HashMap<>();
        this.battleMessages = new HashMap<>();
    }

    public void showNewBattleForm(Context ctx) {
        String playerId = ctx.queryParam("playerId");
        String preselectedMonsterId = ctx.queryParam("monsterId");

        if (playerId == null) {
            ctx.html("Error: Player ID required. <a href='/'>Go to Home</a>");
            return;
        }

        IPlayer player = dataStore.getLeaderboard().getPlayer(playerId);
        if (player == null) {
            ctx.html("Error: Player not found. <a href='/'>Go to Home</a>");
            return;
        }

        if (player.getMonsters().isEmpty()) {
            ctx.html("Error: You need at least one monster to battle. <a href='/player/" + playerId + "'>Back to Dashboard</a>");
            return;
        }

        // Find the preselected monster if provided
        IMonster selectedMonster = null;
        if (preselectedMonsterId != null) {
            for (IMonster m : player.getMonsters()) {
                if (m.getId().equals(preselectedMonsterId)) {
                    selectedMonster = m;
                    break;
                }
            }
        }

        // Build monster selection HTML (only if no monster preselected)
        String monsterSelectionHtml = "";
        if (selectedMonster == null) {
            StringBuilder monsterOptions = new StringBuilder();
            for (IMonster monster : player.getMonsters()) {
                monsterOptions.append(String.format(
                    "<option value='%s'>%s (Level %d, HP: %d/%d)</option>",
                    monster.getId(), monster.getName(), monster.getLevel(),
                    monster.getCurrentHp(), monster.getMaxHp()
                ));
            }
            monsterSelectionHtml = String.format("""
                <label>Choose Your Monster:</label>
                <select name="monsterIndex" required>
                    %s
                </select>
                """, monsterOptions.toString());
        } else {
            int hpPercent = (selectedMonster.getCurrentHp() * 100) / selectedMonster.getMaxHp();
            monsterSelectionHtml = String.format("""
                <input type="hidden" name="monsterIndex" value="%s">
                <div class="selected-monster">
                    <h3>Your Monster:</h3>
                    <div class="monster-display">
                        <img src="/images/%s.png" alt="%s" class="monster-preview"
                             onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
                        <div class="monster-placeholder-preview" data-type="%s" style="display:none;"></div>
                        <div class="monster-details">
                            <h4>%s</h4>
                            <p class="type-badge %s-type">%s Type</p>
                            <p><strong>Level:</strong> %d</p>
                            <p><strong>HP:</strong> %d/%d</p>
                            <div class="hp-bar-small">
                                <div class="hp-fill-small" style="width: %d%%;"></div>
                            </div>
                        </div>
                    </div>
                </div>
                """,
                selectedMonster.getId(),
                selectedMonster.getName().toLowerCase(), selectedMonster.getName(),
                selectedMonster.getType().toString().toLowerCase(),
                selectedMonster.getName(),
                selectedMonster.getType().toString().toLowerCase(), selectedMonster.getType(),
                selectedMonster.getLevel(),
                selectedMonster.getCurrentHp(), selectedMonster.getMaxHp(),
                hpPercent
            );
        }

        // Build list of opponent players
        StringBuilder opponentOptions = new StringBuilder();
        for (IPlayer p : dataStore.getLeaderboard().getAllPlayers()) {
            if (!p.getId().equals(playerId) && !p.getMonsters().isEmpty()) {
                opponentOptions.append(String.format(
                    "<option value='%s'>%s (%d-%d, Score: %d)</option>",
                    p.getId(), p.getUsername(), p.getWins(), p.getLosses(), p.getScore()
                ));
            }
        }

        String html = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Start Battle</title>
                <style>
                    body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); }
                    h1 { color: #333; text-align: center; }
                    form { background: white; padding: 25px; border-radius: 10px; margin-bottom: 20px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
                    h2 { color: #555; margin-top: 0; }
                    label { display: block; margin: 15px 0 5px; font-weight: bold; color: #333; }
                    select { width: 100%%; padding: 10px; margin-bottom: 10px; border: 1px solid #ddd; border-radius: 5px; font-size: 14px; }
                    button { padding: 12px 24px; background: #28a745; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; font-weight: bold; width: 100%%; margin-top: 15px; }
                    button:hover { background: #218838; }
                    .back { display: block; text-align: center; margin-top: 20px; color: #007bff; text-decoration: none; }
                    .back:hover { text-decoration: underline; }

                    .selected-monster { background: #f8f9fa; padding: 15px; border-radius: 8px; margin-bottom: 20px; }
                    .selected-monster h3 { margin: 0 0 10px 0; color: #333; }
                    .monster-display { display: flex; gap: 15px; align-items: center; }
                    .monster-preview { width: 100px; height: 100px; object-fit: contain; }
                    .monster-placeholder-preview { width: 100px; height: 100px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 48px; }
                    .monster-placeholder-preview[data-type="fire"]::after { content: '🔥'; }
                    .monster-placeholder-preview[data-type="water"]::after { content: '💧'; }
                    .monster-placeholder-preview[data-type="grass"]::after { content: '🌿'; }
                    .monster-placeholder-preview[data-type="electric"]::after { content: '⚡'; }
                    .monster-placeholder-preview[data-type="normal"]::after { content: '⭐'; }
                    .monster-details { flex: 1; }
                    .monster-details h4 { margin: 0 0 5px 0; font-size: 20px; color: #333; }
                    .monster-details p { margin: 5px 0; font-size: 14px; }
                    .type-badge { display: inline-block; padding: 3px 10px; border-radius: 12px; color: white; font-weight: bold; font-size: 12px; }
                    .fire-type { background: linear-gradient(135deg, #f12711, #f5af19); }
                    .water-type { background: linear-gradient(135deg, #0575e6, #00f2fe); }
                    .grass-type { background: linear-gradient(135deg, #56ab2f, #a8e063); }
                    .electric-type { background: linear-gradient(135deg, #f2c94c, #f2994a); }
                    .normal-type { background: linear-gradient(135deg, #868f96, #596164); }
                    .hp-bar-small { height: 6px; background: #e0e0e0; border-radius: 3px; overflow: hidden; margin-top: 5px; }
                    .hp-fill-small { height: 100%%; background: #28a745; }
                </style>
                <script>
                    function updateOpponentType(battleType) {
                        document.getElementById('wildOpponent').style.display = battleType === 'wild' ? 'block' : 'none';
                        document.getElementById('playerOpponent').style.display = battleType === 'player' ? 'block' : 'none';
                    }
                </script>
            </head>
            <body>
                <h1>Start a Battle</h1>

                <form method="POST" action="/battle/start">
                    <input type="hidden" name="playerId" value="%s">

                    %s

                    <label>Choose Opponent:</label>
                    <select name="battleType" onchange="updateOpponentType(this.value)" required>
                        <option value="wild">Wild Monster (Computer)</option>
                        <option value="player">Player vs Player</option>
                    </select>

                    <div id="wildOpponent">
                        <label>Wild Monster Type:</label>
                        <select name="opponentType">
                            <option value="FIRE">Fire Type</option>
                            <option value="WATER">Water Type</option>
                            <option value="GRASS">Grass Type</option>
                            <option value="ELECTRIC">Electric Type</option>
                            <option value="NORMAL">Normal Type</option>
                        </select>
                    </div>

                    <div id="playerOpponent" style="display:none;">
                        <label>Opponent Player:</label>
                        <select name="opponentPlayerId">
                            %s
                        </select>
                    </div>

                    <button type="submit">Start Battle!</button>
                </form>

                <a href="/player/%s" class="back">&larr; Back to Dashboard</a>
            </body>
            </html>
            """,
            playerId, monsterSelectionHtml, opponentOptions.toString(), playerId
        );

        ctx.html(html);
    }

    public void startBattle(Context ctx) {
        String playerId = ctx.formParam("playerId");
        String monsterIndexStr = ctx.formParam("monsterIndex");
        String battleType = ctx.formParam("battleType");

        IPlayer player = dataStore.getLeaderboard().getPlayer(playerId);
        if (player == null) {
            ctx.html("Error: Player not found");
            return;
        }

        // Find the player's monster by ID
        IMonster playerMonster = null;
        for (IMonster m : player.getMonsters()) {
            if (m.getId().equals(monsterIndexStr)) {
                playerMonster = m;
                break;
            }
        }

        if (playerMonster == null) {
            ctx.html("Error: Monster not found");
            return;
        }

        IMonster opponent;
        String opponentPlayerId = null;

        if ("player".equals(battleType)) {
            // Player vs Player battle
            opponentPlayerId = ctx.formParam("opponentPlayerId");
            IPlayer opponentPlayer = dataStore.getLeaderboard().getPlayer(opponentPlayerId);

            if (opponentPlayer == null || opponentPlayer.getMonsters().isEmpty()) {
                ctx.html("Error: Opponent player not found or has no monsters");
                return;
            }

            // Select a random monster from opponent's team
            List<IMonster> opponentMonsters = opponentPlayer.getMonsters();
            opponent = opponentMonsters.get(new java.util.Random().nextInt(opponentMonsters.size()));
        } else {
            // Wild monster battle
            String opponentTypeStr = ctx.formParam("opponentType");
            MonsterType opponentType = MonsterType.valueOf(opponentTypeStr);
            opponent = new MonsterFactory().createStarterMonster(opponentType);
        }

        if (opponent == null) {
            ctx.html("Error: Could not create opponent");
            return;
        }

        // Start battle
        IBattle battle = dataStore.getBattleManager().startBattle(playerMonster, opponent);
        String battleId = UUID.randomUUID().toString();
        activeBattles.put(battleId, battle);
        battlePlayerMap.put(battleId, playerId);
        battleOpponentMap.put(battleId, opponentPlayerId);

        ctx.redirect("/battle/" + battleId);
    }

    public void showBattle(Context ctx) {
        String battleId = ctx.pathParam("battleId");
        IBattle battle = activeBattles.get(battleId);

        if (battle == null) {
            // Check if there's a special message for this battle (e.g., opponent ran away)
            String message = battleMessages.get(battleId);
            if (message != null) {
                String playerId = battlePlayerMap.get(battleId);
                battleMessages.remove(battleId);
                battlePlayerMap.remove(battleId);
                battleOpponentMap.remove(battleId);
                ctx.html(String.format("""
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <title>Battle Over</title>
                        <style>
                            body { font-family: Arial, sans-serif; max-width: 600px; margin: 100px auto; padding: 20px; text-align: center; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); }
                            .message-box { background: white; padding: 40px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                            h1 { color: #28a745; }
                            p { font-size: 18px; color: #333; margin: 20px 0; }
                            a { display: inline-block; padding: 12px 24px; margin: 10px; background: #007bff; color: white; text-decoration: none; border-radius: 5px; font-weight: bold; }
                            a:hover { background: #0056b3; }
                        </style>
                    </head>
                    <body>
                        <div class="message-box">
                            <h1>You Win!</h1>
                            <p>%s</p>
                            <a href="/player/%s">Back to Dashboard</a>
                            <a href="/">Home</a>
                        </div>
                    </body>
                    </html>
                    """, message, playerId != null ? playerId : ""));
                return;
            }
            ctx.html("Error: Battle not found. <a href='/'>Go to Home</a>");
            return;
        }

        IMonster monster1 = battle.getMonster1();
        IMonster monster2 = battle.getMonster2();
        String playerId = battlePlayerMap.get(battleId);
        String opponentPlayerId = battleOpponentMap.get(battleId);

        // Battle log (showing newest first)
        java.util.List<AttackResult> battleLog = battle.getBattleLog();
        StringBuilder logHtml = new StringBuilder();
        java.time.format.DateTimeFormatter timeFormat = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");




        for (int i = battleLog.size() - 1; i >= 0; i--) {
            AttackResult result = battleLog.get(i);
            String flashClass = (i == battleLog.size() - 1) ? " flash-new" : "";
            String timestamp = java.time.LocalTime.now().minusSeconds(battleLog.size() - 1 - i).format(timeFormat);

            // Parse the result message to create a more readable format
            String resultMsg = result.toString();
            String formattedMessage = formatBattleLogMessage(resultMsg, battle.getMonster1().getName(), battle.getMonster2().getName());

            logHtml.append(String.format(
                "<div class='log-entry%s'><span class='log-timestamp'>[%s]</span>%s</div>",
                flashClass, timestamp, formattedMessage
            ));
        }
        // Add opening message
        String openingTime = java.time.LocalTime.now().format(timeFormat);
        logHtml.append(String.format("""
            <div class="log-entry log-opening">
                <span class="log-timestamp">[%s]</span>
                <span class="log-icon">⚔️</span>
                <span class="log-text">Battle started!</span>
            </div>
            """, openingTime));

        // Determine which monster is the player's
        String monster1Label = "Your Monster";
        String monster2Label;
        if (opponentPlayerId != null) {
            IPlayer opponentPlayer = dataStore.getLeaderboard().getPlayer(opponentPlayerId);
            monster2Label = opponentPlayer != null ? opponentPlayer.getUsername() + "'s " + monster2.getName() : monster2.getName();
        } else {
            monster2Label = "Wild " + monster2.getName();
        }

        String battleControls = "";
        if (battle.isBattleOver()) {
            IMonster winner = battle.getWinner();

            // Update player stats
            if (playerId != null) {
                IPlayer player = dataStore.getLeaderboard().getPlayer(playerId);
                if (player != null) {
                    boolean playerWon = winner.getId().equals(monster1.getId());
                    if (playerWon) {
                        player.recordWin();
                        // Add experience to the winning monster
                        monster1.addExperience(50);
                    } else {
                        player.recordLoss();
                    }
                    dataStore.getLeaderboard().updatePlayer(player);

                    // If PvP, update opponent's stats too
                    if (opponentPlayerId != null) {
                        IPlayer opponentPlayer = dataStore.getLeaderboard().getPlayer(opponentPlayerId);
                        if (opponentPlayer != null) {
                            if (playerWon) {
                                opponentPlayer.recordLoss();
                            } else {
                                opponentPlayer.recordWin();
                                // Add experience to opponent's winning monster
                                monster2.addExperience(50);
                            }
                            dataStore.getLeaderboard().updatePlayer(opponentPlayer);
                        }
                    }
                }
            }

            String winnerLabel = winner.getName().equals(monster1.getName()) ? "Your Monster" : monster2.getName();
            IPlayer player = dataStore.getLeaderboard().getPlayer(playerId);
            boolean playerWon = winner.getId().equals(monster1.getId());
            String resultType = playerWon ? "Victory!" : "Defeat";
            String resultColor = playerWon ? "#28a745" : "#dc3545";
            String resultEmoji = playerWon ? "🎉" : "😢";

            // Calculate battle stats
            int totalTurns = battleLog.size();
            int damageDealt = monster1.getMaxHp() - monster2.getCurrentHp();
            int damageTaken = monster2.getMaxHp() - monster1.getCurrentHp();
            int experienceGained = playerWon ? 50 : 0;
            String leveledUp = monster1.getLevel() > 1 && playerWon ? "✓ Level Up!" : "";

            battleControls = String.format("""
                <div class="battle-results">
                    <div class="result-header" style="color: %s;">
                        <div class="result-emoji">%s</div>
                        <h2>%s</h2>
                        <p class="winner-name">%s Wins!</p>
                    </div>
                    
                    <div class="result-details">
                        <div class="detail-grid">
                            <div class="detail-card">
                                <div class="detail-label">Turns Taken</div>
                                <div class="detail-value">%d</div>
                            </div>
                            <div class="detail-card">
                                <div class="detail-label">Damage Dealt</div>
                                <div class="detail-value">%d</div>
                            </div>
                            <div class="detail-card">
                                <div class="detail-label">Damage Taken</div>
                                <div class="detail-value">%d</div>
                            </div>
                            <div class="detail-card">
                                <div class="detail-label">Experience Gained</div>
                                <div class="detail-value">%d %s</div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="result-monster-summary">
                        <div class="monster-summary-card">
                            <h4>Your Monster</h4>
                            <p class="monster-name">%s</p>
                            <div class="stat-summary">
                                <div class="stat-row">
                                    <span>Level:</span>
                                    <strong>%d</strong>
                                </div>
                                <div class="stat-row">
                                    <span>HP:</span>
                                    <strong>%d/%d</strong>
                                </div>
                                <div class="stat-row">
                                    <span>Status:</span>
                                    <strong class="%s">%s</strong>
                                </div>
                            </div>
                        </div>
                        
                        <div class="monster-summary-card">
                            <h4>Opponent</h4>
                            <p class="monster-name">%s</p>
                            <div class="stat-summary">
                                <div class="stat-row">
                                    <span>Level:</span>
                                    <strong>%d</strong>
                                </div>
                                <div class="stat-row">
                                    <span>HP:</span>
                                    <strong>%d/%d</strong>
                                </div>
                                <div class="stat-row">
                                    <span>Status:</span>
                                    <strong class="fainted-text">Fainted</strong>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="result-actions">
                        <a href="/player/%s" class="btn-primary">Back to Dashboard</a>
                        <a href="/battle/new?playerId=%s" class="btn-secondary">Next Battle</a>
                    </div>
                </div>
                """,
                resultColor, resultEmoji, resultType, winnerLabel,
                totalTurns, damageDealt, damageTaken, experienceGained, leveledUp,
                monster1.getName(), monster1.getLevel(), monster1.getCurrentHp(), monster1.getMaxHp(),
                playerWon ? "active-text" : "fainted-text", playerWon ? "Victory" : "Defeated",
                monster2.getName(), monster2.getLevel(), monster2.getCurrentHp(), monster2.getMaxHp(),
                playerId, playerId
            );
        }

        // Calculate monster 1 classes
        String monster1Class = getMonsterSpriteClass(monster1, battle);
        String monster1HpClass = getHpBarClass(monster1);
        boolean isMonster1Turn = !battle.isBattleOver() && battle.getCurrentAttacker().getId().equals(monster1.getId());

        // Calculate monster 2 classes
        String monster2Class = getMonsterSpriteClass(monster2, battle);
        String monster2HpClass = getHpBarClass(monster2);
        boolean isMonster2Turn = !battle.isBattleOver() && battle.getCurrentAttacker().getId().equals(monster2.getId());

        // Create attack button for current attacker
        String monster1Controls = "";
        String monster2Controls = "";
        String autoSubmitScript = "";

        if (!battle.isBattleOver()) {
            String attackButton = String.format("""
                <form method="POST" action="/battle/%s/attack" id="attackForm">
                    <button type="submit" class="attack-button">Attack!</button>
                </form>
                <form method="POST" action="/battle/%s/runaway">
                    <button type="submit" class="run-button">Run Away</button>
                </form>
                """, battleId, battleId);

            if (isMonster1Turn) {
                monster1Controls = attackButton;
            } else if (isMonster2Turn) {
                monster2Controls = attackButton;

                // If this is a wild battle (computer opponent), auto-submit after delay
                if (opponentPlayerId == null) {
                    autoSubmitScript = """
                        <script>
                            // Disable attack button and show countdown
                            const form = document.getElementById('attackForm');
                            const btn = form.querySelector('.attack-button');
                            let countdown = 2;
                            btn.disabled = true;
                            btn.textContent = 'Computer thinking... ' + countdown;

                            const interval = setInterval(() => {
                                countdown--;
                                if (countdown > 0) {
                                    btn.textContent = 'Computer thinking... ' + countdown;
                                } else {
                                    btn.textContent = 'Computer attacks!';
                                }
                            }, 1000);

                            setTimeout(() => {
                                clearInterval(interval);
                                form.submit();
                            }, 2000);
                        </script>
                        """;
                }
            }
        }

        String html = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Battle - %s vs %s</title>
                <link rel="stylesheet" href="/css/battle.css">
                <style>
                    body { font-family: Arial, sans-serif; max-width: 1000px; margin: 50px auto; padding: 20px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); }
                    h1 { color: #333; text-align: center; font-size: 32px; margin-bottom: 30px; }
                    .battle-log { background: #fff; border: 2px solid #ddd; padding: 20px; margin: 20px 0; border-radius: 5px; max-height: 400px; overflow-y: auto; }
                    .battle-log h3 { margin-top: 0; color: #333; }
                    .log-entry { margin: 12px 0; padding: 15px; border-radius: 8px; background: linear-gradient(135deg, #f8f9fa 0%%, #f0f1f5 100%%); border-left: 4px solid #667eea; display: flex; align-items: center; gap: 12px; animation: slideIn 0.3s ease-out; }
                    @keyframes slideIn { from { opacity: 0; transform: translateX(-20px); } to { opacity: 1; transform: translateX(0); } }
                    .log-entry .log-timestamp { color: #999; font-size: 11px; font-weight: bold; font-family: monospace; min-width: 70px; }
                    .log-entry .log-icon { font-size: 18px; min-width: 24px; }
                    .log-entry .log-text { color: #333; font-size: 14px; line-height: 1.4; }
                    .log-opening { background: linear-gradient(135deg, #e3f2fd 0%%, #bbdefb 100%%); border-left-color: #2196F3; font-weight: bold; }
                    .log-entry.flash-new { animation: flashEntry 0.8s ease-in-out; background: linear-gradient(135deg, #fff9c4 0%%, #fffde7 100%%); border-left-color: #fbc02d; }
                    @keyframes flashEntry { 0%% { background: linear-gradient(135deg, #fff9c4 0%%, #fffde7 100%%); } 50%% { box-shadow: 0 0 15px rgba(251, 192, 45, 0.6); } 100%% { background: linear-gradient(135deg, #f8f9fa 0%%, #f0f1f5 100%%); } }
                    .log-entry.damage { border-left-color: #f44336; }
                    .log-entry.heal { border-left-color: #4caf50; }
                    .log-entry.weak { border-left-color: #ff9800; }
                    .log-entry.strong { border-left-color: #9c27b0; }
                    .log-text strong { color: #667eea; font-weight: bold; }
                    .log-text .damage-value { color: #f44336; font-weight: bold; }
                    .log-text .heal-value { color: #4caf50; font-weight: bold; }
                    .attack-button { padding: 10px 20px; background: #dc3545; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; font-weight: bold; width: 100%%; max-width: 200px; margin: 5px auto; display: block; }
                    .attack-button:hover { background: #c82333; transform: scale(1.05); transition: all 0.2s; }
                    .run-button { padding: 10px 20px; background: #6c757d; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; font-weight: bold; width: 100%%; max-width: 200px; margin: 5px auto; display: block; }
                    .run-button:hover { background: #5a6268; transform: scale(1.05); transition: all 0.2s; }
                    .monster-controls { text-align: center; margin-top: 10px; min-height: 100px; }
                    .monster-controls form { margin: 0; }
                    
                    /* Battle Results Styles */
                    .battle-results { background: white; border-radius: 15px; padding: 40px; box-shadow: 0 8px 25px rgba(0,0,0,0.15); }
                    .result-header { text-align: center; margin-bottom: 40px; padding: 30px; background: linear-gradient(135deg, rgba(102,126,234,0.1) 0%%, rgba(118,75,162,0.1) 100%%); border-radius: 12px; }
                    .result-emoji { font-size: 64px; margin-bottom: 15px; animation: bounce 0.6s ease-in-out; }
                    @keyframes bounce { 0%%, 100%% { transform: translateY(0); } 50%% { transform: translateY(-10px); } }
                    .result-header h2 { margin: 10px 0; font-size: 36px; font-weight: bold; }
                    .winner-name { font-size: 24px; font-weight: bold; margin: 10px 0 0; }
                    
                    /* Detail Grid */
                    .result-details { margin: 30px 0; }
                    .detail-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 15px; }
                    .detail-card { background: #f8f9fa; padding: 20px; border-radius: 10px; text-align: center; border-left: 4px solid #667eea; }
                    .detail-label { font-size: 12px; text-transform: uppercase; color: #666; font-weight: bold; margin-bottom: 8px; }
                    .detail-value { font-size: 32px; font-weight: bold; color: #333; }
                    
                    /* Monster Summary */
                    .result-monster-summary { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin: 30px 0; }
                    .monster-summary-card { background: linear-gradient(135deg, #f8f9fa 0%%, #e9ecef 100%%); padding: 25px; border-radius: 12px; border: 2px solid #dee2e6; }
                    .monster-summary-card h4 { margin: 0 0 10px 0; font-size: 18px; color: #333; text-transform: uppercase; letter-spacing: 1px; }
                    .monster-summary-card .monster-name { font-size: 24px; font-weight: bold; color: #667eea; margin: 5px 0 20px 0; }
                    .stat-summary { display: flex; flex-direction: column; gap: 12px; }
                    .stat-row { display: flex; justify-content: space-between; padding: 10px; background: white; border-radius: 6px; }
                    .stat-row span { color: #666; font-size: 14px; }
                    .stat-row strong { color: #333; font-weight: bold; }
                    .active-text { color: #28a745; }
                    .fainted-text { color: #dc3545; }
                    
                    /* Result Actions */
                    .result-actions { display: flex; gap: 15px; justify-content: center; margin-top: 40px; flex-wrap: wrap; }
                    .btn-primary, .btn-secondary { padding: 14px 32px; border-radius: 8px; text-decoration: none; font-weight: bold; font-size: 16px; transition: all 0.3s; display: inline-block; }
                    .btn-primary { background: #667eea; color: white; }
                    .btn-primary:hover { background: #764ba2; transform: translateY(-2px); box-shadow: 0 5px 15px rgba(102,126,234,0.4); }
                    .btn-secondary { background: #28a745; color: white; }
                    .btn-secondary:hover { background: #218838; transform: translateY(-2px); box-shadow: 0 5px 15px rgba(40,167,69,0.4); }
                    
                    .back{display: inline-block; margin-top: 30px;padding: 12px 24px; background: rgba(255, 255, 255, 0.15);color: #ffffff;text-decoration: none;font-size: 16px; font-weight: bold; border-radius: 30px; border: 2px solid rgba(255, 255, 255, 0.3); backdrop-filter: blur(5px); transition: all 0.3s ease;}
                    .back:hover{ background: #ffffff; color: #764ba2; transform: translateY(-3px); box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3); text-decoration: none; }
                </style>
            </head>
            <body>
                <h1>Battle Arena</h1>

                <div class="battle-arena">
                    <div class="%s" data-type="%s">
                        <div class="monster-name">%s</div>
                        <img src="/images/%s.png" alt="%s"
                             onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
                        <div class="monster-placeholder" data-type="%s" style="display:none;"></div>
                        <div class="hp-bar-container">
                            <div class="hp-text">Level %d | HP: %d/%d</div>
                            <div class="hp-bar">
                                <div class="%s" style="width: %d%%;"></div>
                            </div>
                        </div>
                        <div class="monster-controls">
                            %s
                        </div>
                    </div>

                    <div class="%s" data-type="%s">
                        <div class="monster-name">%s</div>
                        <img src="/images/%s.png" alt="%s"
                             onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
                        <div class="monster-placeholder" data-type="%s" style="display:none;"></div>
                        <div class="hp-bar-container">
                            <div class="hp-text">Level %d | HP: %d/%d</div>
                            <div class="hp-bar">
                                <div class="%s" style="width: %d%%;"></div>
                            </div>
                        </div>
                        <div class="monster-controls">
                            %s
                        </div>
                    </div>
                </div>

                <div class="battle-log">
                    <h3>Battle Log (Newest First)</h3>
                    %s
                </div>

                %s

                <a href="/" class="back">&larr; Back to Home</a>

                %s
            </body>
            </html>
            """,
            monster1.getName(), monster2.getName(),
            // Monster 1
            monster1Class, monster1.getType().toString().toLowerCase(),
            monster1Label,
            monster1.getName().toLowerCase(), monster1.getName(),
            monster1.getType().toString().toLowerCase(),
            monster1.getLevel(), monster1.getCurrentHp(), monster1.getMaxHp(),
            monster1HpClass, (monster1.getCurrentHp() * 100 / monster1.getMaxHp()),
            monster1Controls,
            // Monster 2
            monster2Class, monster2.getType().toString().toLowerCase(),
            monster2Label,
            monster2.getName().toLowerCase(), monster2.getName(),
            monster2.getType().toString().toLowerCase(),
            monster2.getLevel(), monster2.getCurrentHp(), monster2.getMaxHp(),
            monster2HpClass, (monster2.getCurrentHp() * 100 / monster2.getMaxHp()),
            monster2Controls,
            // Battle log
            logHtml.toString(),
            // Controls
            battleControls,
            // Auto-submit script
            autoSubmitScript
        );

        ctx.html(html);
    }

    public void attack(Context ctx) {
        String battleId = ctx.pathParam("battleId");
        IBattle battle = activeBattles.get(battleId);

        if (battle == null) {
            // Check if there's a special message for this battle
            String message = battleMessages.get(battleId);
            if (message != null) {
                String playerId = battlePlayerMap.get(battleId);
                battleMessages.remove(battleId);
                battlePlayerMap.remove(battleId);
                battleOpponentMap.remove(battleId);
                ctx.html(String.format("""
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <title>Battle Over</title>
                        <style>
                            body { font-family: Arial, sans-serif; max-width: 600px; margin: 100px auto; padding: 20px; text-align: center; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); }
                            .message-box { background: white; padding: 40px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                            h1 { color: #28a745; }
                            p { font-size: 18px; color: #333; margin: 20px 0; }
                            a { display: inline-block; padding: 12px 24px; margin: 10px; background: #007bff; color: white; text-decoration: none; border-radius: 5px; font-weight: bold; }
                            a:hover { background: #0056b3; }
                        </style>
                    </head>
                    <body>
                        <div class="message-box">
                            <h1>You Win!</h1>
                            <p>%s</p>
                            <a href="/player/%s">Back to Dashboard</a>
                            <a href="/">Home</a>
                        </div>
                    </body>
                    </html>
                    """, message, playerId != null ? playerId : ""));
                return;
            }
            ctx.html("Error: Battle not found. <a href='/'>Go to Home</a>");
            return;
        }

        if (battle.isBattleOver()) {
            ctx.redirect("/battle/" + battleId);
            return;
        }

        // Check if this is the opponent's turn in a wild battle (computer controlled)
        String opponentPlayerId = battleOpponentMap.get(battleId);
        boolean isComputerTurn = battle.getCurrentAttacker().getId().equals(battle.getMonster2().getId()) && opponentPlayerId == null;

        // Execute the attack
        battle.attack();

        // If after this attack it's now the computer's turn, handle AI decision
        if (!battle.isBattleOver() && opponentPlayerId == null) {
            // Check if it's now the opponent's turn
            boolean isNowOpponentTurn = battle.getCurrentAttacker().getId().equals(battle.getMonster2().getId());

            if (isNowOpponentTurn && !isComputerTurn) {
                // Player just attacked, now it's computer's turn
                // DON'T auto-attack here - let the page reload and the JavaScript delay will trigger it
                // Just redirect and let the auto-submit script handle it
            } else if (isComputerTurn) {
                // This WAS the computer's turn (triggered by auto-submit), so make AI decision
                IMonster opponent = battle.getMonster2();
                boolean shouldRunAway = makeAIDecision(opponent, battle.getMonster1());

                if (shouldRunAway) {
                    // AI decides to run away - player wins
                    String playerId = battlePlayerMap.get(battleId);
                    if (playerId != null) {
                        IPlayer player = dataStore.getLeaderboard().getPlayer(playerId);
                        if (player != null) {
                            player.recordWin();
                            battle.getMonster1().addExperience(25); // Less exp for opponent running
                            dataStore.getLeaderboard().updatePlayer(player);
                        }
                    }
                    // Store message and end battle
                    battleMessages.put(battleId, "The wild " + opponent.getName() + " ran away!");
                    activeBattles.remove(battleId);
                    battleOpponentMap.remove(battleId);
                }
            }
        }

        ctx.redirect("/battle/" + battleId);
    }

    /**
     * Format battle log messages to be more readable and game-like
     */
    private String formatBattleLogMessage(String rawMessage, String monster1Name, String monster2Name) {
        String icon = "⚔️";
        String displayText = rawMessage;
        String attacker = "";
        String defender = "";
        String damageAmount = "";
        String effectiveness = "";

        // Determine attacker and defender
        if (rawMessage.contains(monster1Name)) {
            attacker = monster1Name;
            defender = monster2Name;
        } else {
            attacker = monster2Name;
            defender = monster1Name;
        }

        // Parse and format the message
        if (rawMessage.contains("attacked")) {
            icon = "🔥";

            if (rawMessage.contains("dealt")) {
                damageAmount = extractNumber(rawMessage);
            }

            // Check for effectiveness
            if (rawMessage.contains("super effective") || rawMessage.contains("super-effective")) {
                effectiveness = " <span class='effectiveness super-effective'>⚡ Super Effective!</span>";
                icon = "✨";
            } else if (rawMessage.contains("not very effective")) {
                effectiveness = " <span class='effectiveness not-effective'>🛡️ Not Very Effective</span>";
            }

            if (!damageAmount.isEmpty()) {
                displayText = String.format(
                    "<strong>%s</strong> attacks <strong>%s</strong> for <span class='damage-value'>%s damage</span>%s",
                    attacker, defender, damageAmount, effectiveness
                );
            } else {
                displayText = String.format(
                    "<strong>%s</strong> attacks <strong>%s</strong>%s",
                    attacker, defender, effectiveness
                );
            }
        } else if (rawMessage.contains("missed")) {
            icon = "💨";
            displayText = String.format("<strong>%s</strong>'s attack on <strong>%s</strong> missed!", attacker, defender);
        } else if (rawMessage.contains("fainted")) {
            icon = "💤";
            displayText = String.format("<strong>%s</strong> has fainted!", attacker);
        }

        return String.format("<span class='log-icon'>%s</span><span class='log-text'>%s</span>", icon, displayText);
    }

    /**
     * Extract numeric damage value from message
     */
    private String extractNumber(String text) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\d+");
        java.util.regex.Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    /**
     * AI decision making for computer opponents.
     * Returns true if AI decides to run away, false if it decides to attack.
     */
    private boolean makeAIDecision(IMonster aiMonster, IMonster playerMonster) {
        java.util.Random random = new java.util.Random();

        // Calculate HP percentages
        int aiHpPercent = (aiMonster.getCurrentHp() * 100) / aiMonster.getMaxHp();
        int playerHpPercent = (playerMonster.getCurrentHp() * 100) / playerMonster.getMaxHp();

        // Decision factors:
        // 1. If AI HP is very low (< 20%), high chance to run (60%)
        if (aiHpPercent < 20) {
            return random.nextInt(100) < 60;
        }

        // 2. If AI HP is low (< 40%) and player HP is high (> 60%), moderate chance to run (35%)
        if (aiHpPercent < 40 && playerHpPercent > 60) {
            return random.nextInt(100) < 35;
        }

        // 3. If player HP is very low (< 25%) and AI HP is decent (> 40%), almost never run (5%)
        if (playerHpPercent < 25 && aiHpPercent > 40) {
            return random.nextInt(100) < 5;
        }

        // 4. Consider level difference - if player much stronger, more likely to run
        if (playerMonster.getLevel() > aiMonster.getLevel() + 5 && aiHpPercent < 50) {
            return random.nextInt(100) < 40;
        }

        // 5. Default: small random chance to run (10%)
        return random.nextInt(100) < 10;
    }

    public void runaway(Context ctx) {
        String battleId = ctx.pathParam("battleId");
        String playerId = battlePlayerMap.get(battleId);
        String opponentPlayerId = battleOpponentMap.get(battleId);

        // Remove battle
        activeBattles.remove(battleId);
        battlePlayerMap.remove(battleId);
        battleOpponentMap.remove(battleId);

        // Record as loss for player who ran away, win for opponent
        if (playerId != null) {
            IPlayer player = dataStore.getLeaderboard().getPlayer(playerId);
            if (player != null) {
                player.recordLoss();
                dataStore.getLeaderboard().updatePlayer(player);
            }

            // If PvP, give opponent a win
            if (opponentPlayerId != null) {
                IPlayer opponentPlayer = dataStore.getLeaderboard().getPlayer(opponentPlayerId);
                if (opponentPlayer != null) {
                    opponentPlayer.recordWin();
                    dataStore.getLeaderboard().updatePlayer(opponentPlayer);
                }
            }

            ctx.redirect("/player/" + playerId);
        } else {
            ctx.redirect("/");
        }
    }

    // Helper methods for battle display

    private String getMonsterSpriteClass(IMonster monster, IBattle battle) {
        String baseClass = "monster-sprite";
        if (battle.isBattleOver() && monster.isFainted()) {
            return baseClass + " fainted";
        }
        if (!battle.isBattleOver() && battle.getCurrentAttacker().getId().equals(monster.getId())) {
            return baseClass + " idle current-turn";
        }
        return baseClass + " idle";
    }

    private String getHpBarClass(IMonster monster) {
        int hpPercent = (monster.getCurrentHp() * 100 / monster.getMaxHp());
        if (hpPercent < 20) return "hp-fill critical";
        if (hpPercent < 50) return "hp-fill low";
        return "hp-fill";
    }
}
