package web.checkpoint3;

import io.javalin.http.Context;
import model.*;

import java.util.List;

/**
 * Controller for battle operations in Checkpoint 2 app.
 */
public class Checkpoint3BattleController {
    private final Checkpoint3DataStore dataStore;

    public Checkpoint3BattleController(Checkpoint3DataStore dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Show battle setup form.
     */
    public void showBattleSetup(Context ctx) {
        try {
            List<IMonster> monsters = dataStore.getAllMonsters();

            if (monsters.size() < 2) {
                ctx.html("""
                    <h1>Not Enough Monsters</h1>
                    <p>You need at least 2 monsters to battle. <a href='/monster/create'>Create more monsters</a></p>
                    <a href='/'>Back to Home</a>
                    """);
                return;
            }

            StringBuilder monsterOptions = new StringBuilder();
            for (IMonster monster : monsters) {
                if (monster == null) continue; // Skip null monsters
                monsterOptions.append(String.format(
                    "<option value='%s'>%s (%s) - HP: %d/%d</option>",
                    monster.getId(), monster.getName(), monster.getType(),
                    monster.getCurrentHp(), monster.getMaxHp()
                ));
            }

            String html = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Start Battle - Checkpoint 2</title>
                <style>
                    body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; background: #f5f5f5; }
                    h1 { color: #333; text-align: center; }
                    .nav { margin: 20px 0; text-align: center; }
                    .nav a { display: inline-block; padding: 10px 20px; margin: 5px; background: #007bff; color: white; text-decoration: none; border-radius: 5px; }
                    .nav a:hover { background: #0056b3; }
                    .battle-setup { background: white; padding: 40px; border-radius: 15px; box-shadow: 0 5px 15px rgba(0,0,0,0.1); }
                    .form-group { margin: 25px 0; }
                    .form-group label { display: block; font-weight: bold; margin-bottom: 10px; font-size: 18px; color: #555; }
                    .form-group select { width: 100%%; padding: 12px; border: 2px solid #ddd; border-radius: 8px; font-size: 16px; }
                    .vs-divider { text-align: center; font-size: 36px; font-weight: bold; color: #dc3545; margin: 30px 0; }
                    button { width: 100%%; padding: 18px; background: #dc3545; color: white; border: none; border-radius: 8px; cursor: pointer; font-size: 18px; font-weight: bold; }
                    button:hover { background: #c82333; }
                    .info-box { background: #d1ecf1; border: 1px solid #bee5eb; padding: 15px; border-radius: 8px; margin: 20px 0; color: #0c5460; }
                </style>
            </head>
            <body>
                <h1>Start a Battle</h1>
                <div class="nav">
                    <a href="/">Home</a>
                    <a href="/monster/all">View Monsters</a>
                    <a href="/monster/create">Create Monster</a>
                </div>
                <div class="battle-setup">
                    <div class="info-box">
                        <strong>Battle Rules:</strong> Turn order is determined by Speed stat. Faster monster attacks first!
                    </div>
                    <form method="POST" action="/battle/start">
                        <div class="form-group">
                            <label>Monster 1 (Challenger):</label>
                            <select name="monster1" required>
                                <option value="">-- Select Monster --</option>
                                %s
                            </select>
                        </div>
                        <div class="vs-divider">VS</div>
                        <div class="form-group">
                            <label>Monster 2 (Opponent):</label>
                            <select name="monster2" required>
                                <option value="">-- Select Monster --</option>
                                %s
                            </select>
                        </div>
                        <button type="submit">Start Battle!</button>
                    </form>
                </div>
            </body>
            </html>
            """, monsterOptions.toString(), monsterOptions.toString());

            ctx.html(html);
        } catch (Exception e) {
            ctx.html("<h1>Error Loading Battle Setup</h1><p>Error: " + e.getMessage() + "</p><p>" + e.getClass().getName() + "</p><a href='/'>Back to Home</a>");
            e.printStackTrace();
        }
    }

    /**
     * Start a new battle.
     */
    public void startBattle(Context ctx) {
        try {
            String monster1Id = ctx.formParam("monster1");
            String monster2Id = ctx.formParam("monster2");

            if (monster1Id == null || monster2Id == null || monster1Id.isEmpty() || monster2Id.isEmpty()) {
                ctx.html("<h1>Error</h1><p>Please select both monsters!</p><a href='/battle/new'>Try Again</a>");
                return;
            }

            if (monster1Id.equals(monster2Id)) {
                ctx.html("<h1>Error</h1><p>Cannot battle a monster against itself!</p><a href='/battle/new'>Try Again</a>");
                return;
            }

            String battleId = dataStore.createBattle(monster1Id, monster2Id);
            ctx.redirect("/battle/" + battleId);

        } catch (Exception e) {
            ctx.html("<h1>Error</h1><p>Failed to start battle: " + e.getMessage() + "</p><a href='/battle/new'>Try Again</a>");
        }
    }

    /**
     * View battle arena.
     */
    public void viewBattle(Context ctx) {
        String battleId = ctx.pathParam("battleId");
        IBattle battle = dataStore.getBattle(battleId);

        if (battle == null) {
            ctx.html("<h1>Battle Not Found</h1><p><a href='/battle/new'>Start New Battle</a></p>");
            return;
        }

        IMonster m1 = battle.getMonster1();
        IMonster m2 = battle.getMonster2();
        boolean battleOver = battle.isBattleOver();
        IMonster currentAttacker = battle.getCurrentAttacker();
        IMonster winner = battle.getWinner();

        // Award XP if battle just ended (only awards once)
        Checkpoint3DataStore.BattleReward reward = null;
        if (battleOver) {
            reward = dataStore.awardBattleXp(battleId);
        }

        // Calculate HP percentages for bar colors
        int m1HpPercent = (m1.getCurrentHp() * 100) / m1.getMaxHp();
        int m2HpPercent = (m2.getCurrentHp() * 100) / m2.getMaxHp();

        String m1HpColor = m1HpPercent > 50 ? "#51cf66" : (m1HpPercent > 20 ? "#ffd43b" : "#ff6b6b");
        String m2HpColor = m2HpPercent > 50 ? "#51cf66" : (m2HpPercent > 20 ? "#ffd43b" : "#ff6b6b");

        // Build battle log
        StringBuilder battleLog = new StringBuilder();
        List<AttackResult> log = battle.getBattleLog();
        for (int i = log.size() - 1; i >= 0; i--) { // Reverse order (newest first)
            AttackResult result = log.get(i);
            String logClass = "";
            if (result.getEffectiveness() > 1.5) {
                logClass = "super-effective";
            } else if (result.getEffectiveness() < 0.75) {
                logClass = "not-effective";
            }
            battleLog.append(String.format(
                "<div class='log-entry %s'>%s</div>",
                logClass,
                result.toString()
            ));
        }

        String attackButton = battleOver ? "" :
            String.format("<form method='POST' action='/battle/%s/attack' style='text-align: center;'>" +
                         "<button type='submit' class='attack-btn'>ATTACK!</button></form>", battleId);

        String winnerAnnouncement = battleOver ?
            String.format("<div class='winner-box'>🏆 %s Wins! 🏆</div>", winner.getName()) : "";

        // Build reward notifications
        StringBuilder rewardNotifications = new StringBuilder();
        if (reward != null) {
            rewardNotifications.append(String.format(
                "<div class='reward-box'>💎 %s gained %d XP!</div>",
                reward.monsterName, reward.xpGained
            ));

            if (reward.leveledUp) {
                rewardNotifications.append(String.format(
                    "<div class='level-up-box'>⭐ %s leveled up! Level %d → %d</div>",
                    reward.monsterName, reward.levelBefore, reward.levelAfter
                ));
            }

            if (reward.canNowEvolve) {
                rewardNotifications.append(String.format(
                    "<div class='evolution-ready-box'>✨ %s is ready to evolve! <a href='/monster/%s/evolution' style='color: white; text-decoration: underline;'>Evolve Now!</a></div>",
                    reward.monsterName, reward.monsterId
                ));
            }
        }

        String currentTurnIndicator = battleOver ? "" :
            String.format("<div class='turn-indicator'>%s's Turn</div>", currentAttacker.getName());

        String html = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Battle Arena - Checkpoint 2</title>
                <style>
                    body { font-family: Arial, sans-serif; max-width: 1400px; margin: 20px auto; padding: 20px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); }
                    h1 { color: white; text-align: center; margin-bottom: 30px; }
                    .nav { margin: 20px 0; text-align: center; }
                    .nav a { display: inline-block; padding: 10px 20px; margin: 5px; background: white; color: #667eea; text-decoration: none; border-radius: 5px; font-weight: bold; }
                    .nav a:hover { background: #f8f9fa; }
                    .battle-arena { display: flex; justify-content: space-between; align-items: center; gap: 30px; margin: 30px 0; }
                    .monster-side { flex: 1; background: white; padding: 30px; border-radius: 15px; box-shadow: 0 10px 30px rgba(0,0,0,0.3); }
                    .monster-side h2 { margin: 0 0 20px 0; text-align: center; color: #333; }
                    .type-badge { display: inline-block; padding: 5px 15px; border-radius: 20px; color: white; font-weight: bold; margin: 10px 0; }
                    .fire-type { background: #ff6b6b; }
                    .water-type { background: #4dabf7; }
                    .grass-type { background: #51cf66; }
                    .electric-type { background: #ffd43b; color: #333; }
                    .normal-type { background: #adb5bd; }
                    .hp-bar-container { background: #e9ecef; border-radius: 10px; height: 30px; margin: 15px 0; overflow: hidden; position: relative; }
                    .hp-bar { height: 100%%; transition: width 0.5s, background-color 0.3s; }
                    .hp-text { position: absolute; top: 0; left: 0; right: 0; bottom: 0; display: flex; align-items: center; justify-content: center; font-weight: bold; color: #333; text-shadow: 0 0 3px white, 0 0 3px white; }
                    .stats { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; margin: 20px 0; }
                    .stat { background: #f8f9fa; padding: 12px; border-radius: 8px; text-align: center; }
                    .stat strong { display: block; color: #666; font-size: 12px; }
                    .stat span { display: block; font-size: 20px; color: #333; margin-top: 5px; }
                    .vs-section { text-align: center; }
                    .vs-badge { font-size: 48px; font-weight: bold; color: white; text-shadow: 0 3px 10px rgba(0,0,0,0.3); }
                    .turn-indicator { background: rgba(255,255,255,0.2); padding: 15px; border-radius: 10px; color: white; font-weight: bold; text-align: center; margin: 15px 0; font-size: 18px; }
                    .attack-btn { padding: 20px 60px; background: #dc3545; color: white; border: none; border-radius: 50px; cursor: pointer; font-size: 24px; font-weight: bold; box-shadow: 0 5px 20px rgba(220, 53, 69, 0.4); transition: all 0.3s; }
                    .attack-btn:hover { background: #c82333; transform: translateY(-2px); box-shadow: 0 8px 25px rgba(220, 53, 69, 0.6); }
                    .battle-log { background: white; padding: 30px; border-radius: 15px; margin: 30px 0; max-height: 400px; overflow-y: auto; box-shadow: 0 5px 15px rgba(0,0,0,0.2); }
                    .battle-log h3 { margin: 0 0 20px 0; color: #333; }
                    .log-entry { padding: 12px; margin: 8px 0; background: #f8f9fa; border-left: 4px solid #007bff; border-radius: 5px; }
                    .log-entry.super-effective { border-left-color: #51cf66; background: #d3f9d8; }
                    .log-entry.not-effective { border-left-color: #ffd43b; background: #fff3bf; }
                    .winner-box { background: linear-gradient(135deg, #ffd700, #ffed4e); padding: 30px; border-radius: 15px; text-align: center; font-size: 36px; font-weight: bold; color: #333; margin: 30px 0; box-shadow: 0 10px 30px rgba(255, 215, 0, 0.5); animation: pulse 1s infinite; }
                    @keyframes pulse { 0%%, 100%% { transform: scale(1); } 50%% { transform: scale(1.05); } }
                    .reward-box { background: linear-gradient(135deg, #667eea, #764ba2); padding: 20px; border-radius: 10px; text-align: center; font-size: 24px; font-weight: bold; color: white; margin: 15px 0; box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4); }
                    .level-up-box { background: linear-gradient(135deg, #f093fb, #f5576c); padding: 20px; border-radius: 10px; text-align: center; font-size: 24px; font-weight: bold; color: white; margin: 15px 0; box-shadow: 0 5px 15px rgba(245, 87, 108, 0.4); animation: pulse 1.5s infinite; }
                    .evolution-ready-box { background: linear-gradient(135deg, #4facfe, #00f2fe); padding: 20px; border-radius: 10px; text-align: center; font-size: 24px; font-weight: bold; color: white; margin: 15px 0; box-shadow: 0 5px 15px rgba(79, 172, 254, 0.4); animation: pulse 1.5s infinite; }
                    .fainted { opacity: 0.5; }
                </style>
            </head>
            <body>
                <h1>Battle Arena</h1>
                <div class="nav">
                    <a href="/">Home</a>
                    <a href="/battle/new">New Battle</a>
                    <a href="/monster/all">View Monsters</a>
                </div>

                %s
                %s
                %s

                <div class="battle-arena">
                    <div class="monster-side %s">
                        <h2>%s</h2>
                        <div style="text-align: center;">
                            <span class="type-badge %s-type">%s</span>
                        </div>
                        <div class="hp-bar-container">
                            <div class="hp-bar" style="width: %d%%; background-color: %s;"></div>
                            <span class="hp-text">HP: %d / %d</span>
                        </div>
                        <div class="stats">
                            <div class="stat"><strong>ATK</strong><span>%d</span></div>
                            <div class="stat"><strong>DEF</strong><span>%d</span></div>
                            <div class="stat"><strong>SPD</strong><span>%d</span></div>
                            <div class="stat"><strong>LVL</strong><span>%d</span></div>
                        </div>
                    </div>

                    <div class="vs-section">
                        <div class="vs-badge">VS</div>
                        %s
                        %s
                    </div>

                    <div class="monster-side %s">
                        <h2>%s</h2>
                        <div style="text-align: center;">
                            <span class="type-badge %s-type">%s</span>
                        </div>
                        <div class="hp-bar-container">
                            <div class="hp-bar" style="width: %d%%; background-color: %s;"></div>
                            <span class="hp-text">HP: %d / %d</span>
                        </div>
                        <div class="stats">
                            <div class="stat"><strong>ATK</strong><span>%d</span></div>
                            <div class="stat"><strong>DEF</strong><span>%d</span></div>
                            <div class="stat"><strong>SPD</strong><span>%d</span></div>
                            <div class="stat"><strong>LVL</strong><span>%d</span></div>
                        </div>
                    </div>
                </div>

                <div class="battle-log">
                    <h3>Battle Log</h3>
                    %s
                </div>
            </body>
            </html>
            """,
            winnerAnnouncement,
            currentTurnIndicator,
            rewardNotifications.toString(),
            m1.isFainted() ? "fainted" : "",
            m1.getName(),
            m1.getType().toString().toLowerCase(), m1.getType(),
            m1HpPercent, m1HpColor,
            m1.getCurrentHp(), m1.getMaxHp(),
            m1.getAttack(), m1.getDefense(), m1.getSpeed(), m1.getLevel(),
            currentTurnIndicator,
            attackButton,
            m2.isFainted() ? "fainted" : "",
            m2.getName(),
            m2.getType().toString().toLowerCase(), m2.getType(),
            m2HpPercent, m2HpColor,
            m2.getCurrentHp(), m2.getMaxHp(),
            m2.getAttack(), m2.getDefense(), m2.getSpeed(), m2.getLevel(),
            battleLog.length() > 0 ? battleLog.toString() : "<p style='text-align: center; color: #999;'>Battle just started...</p>"
        );

        ctx.html(html);
    }

    /**
     * Execute an attack in the battle.
     */
    public void executeAttack(Context ctx) {
        String battleId = ctx.pathParam("battleId");
        IBattle battle = dataStore.getBattle(battleId);

        if (battle == null) {
            ctx.html("<h1>Battle Not Found</h1><p><a href='/battle/new'>Start New Battle</a></p>");
            return;
        }

        try {
            if (!battle.isBattleOver()) {
                battle.attack();
            }
            ctx.redirect("/battle/" + battleId);
        } catch (Exception e) {
            ctx.html("<h1>Error</h1><p>" + e.getMessage() + "</p><a href='/battle/" + battleId + "'>Back to Battle</a>");
        }
    }
}
