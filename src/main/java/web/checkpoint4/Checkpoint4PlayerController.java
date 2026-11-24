package web.checkpoint4;

import io.javalin.http.Context;
import model.*;

import java.util.List;

/**
 * Controller for player-related operations.
 */
public class Checkpoint4PlayerController {
    private final Checkpoint4DataStore dataStore;

    public Checkpoint4PlayerController(Checkpoint4DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void showNewPlayerForm(Context ctx) {
        String html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Create New Player</title>
                <style>
                    body { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    font-family: Arial, sans-serif;
                    max-width: 800px; margin: 50px auto; padding: 20px;}
                    h1 { color: #333; text-align: center; margin-top: 0;}
                    fieldset{background: white; padding: 40px; border-radius: 20px; box-shadow: 0 10px 40px rgba(0,0,0,0.3); color: #333; border: none;}
                    form {  padding: 20px; border-radius: 5px; }
                    label { display: block; margin: 10px 0 8px; font-weight: bold; color: #555;}
                    input, select { width: 100%%; padding: 12px; margin: 10px; border: 2px solid #eee; border-radius: 8px; box-sizing: border-box; font-size: 16px; background: #f9f9f9; }
                    input:focus, select:focus { outline: none; border-color: #667eea; }
                    button { width: 100%%; margin-top: 15px; padding: 14px 20px; background: linear-gradient(135deg, #28a745 0%, #20c997 100%); color: white; border: none; border-radius: 8px; cursor: pointer; font-size: 16px; font-weight: bold; transition: all 0.2s; }
                    button:hover { transform: translateY(-2px); box-shadow: 0 5px 15px rgba(40, 167, 69, 0.4); }
                    .back {display: inline-block; margin-top: 30px; padding: 12px 24px; background: rgba(255, 255, 255, 0.15); color: #ffffff; text-decoration: none; font-size: 16px; font-weight: bold; border-radius: 30px; border: 2px solid rgba(255, 255, 255, 0.3); backdrop-filter: blur(5px); transition: all 0.3s ease;}
                    .back:hover {background: #ffffff; color: #764ba2; transform: translateY(-3px); box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3); text-decoration: none;}
                    .row{display: flex; justify-content:c center;}
                    .col{width: 100%; padding: 0;}
                </style>
            </head>
            <body>
                
                <form method="POST" action="/player/create">

                <fieldset>
                <div class="col">
                    <h1>Create New Player</h1>

                    <label>Username:</label>
                    <input type="text" name="username" required>

                    <label>Choose Your Starter Monster:</label>
                    <select name="starterType" required>
                        <option value="FIRE">Flameling (Fire Type)</option>
                        <option value="WATER">Aquabit (Water Type)</option>
                        <option value="GRASS">Leafling (Grass Type)</option>
                        <option value="ELECTRIC">Sparkbit (Electric Type)</option>
                        <option value="NORMAL">Normie (Normal Type)</option>
                    </select>

                    <button type="submit">Create Player</button>
                </div>
                </fieldset>
                <div class="row">
                    <a href="/" class="back">&larr; Back to Home</a>
                </div>
                </form>
                
            </body>
            </html>
            """;
        ctx.html(html);
    }

    public void createPlayer(Context ctx) {
        String username = ctx.formParam("username");
        String starterTypeStr = ctx.formParam("starterType");

        if (username == null || username.trim().isEmpty()) {
            ctx.html("Error: Username is required. <a href='/player/new'>Try again</a>");
            return;
        }

        // Check if username already exists
        if (dataStore.getLeaderboard().getPlayerByUsername(username) != null) {
            ctx.html("Error: Username already exists. <a href='/player/new'>Try again</a>");
            return;
        }

        IPlayer player = new Player(username);

        // Add starter monster
        if (starterTypeStr != null) {
            MonsterType starterType = MonsterType.valueOf(starterTypeStr);
            IMonster starter = dataStore.getMonsterFactory().createStarterMonster(starterType);
            player.addMonster(starter);
        }

        dataStore.addPlayer(player);

        ctx.redirect("/player/" + player.getId());
    }

    public void showPlayerDashboard(Context ctx) {
        String playerId = ctx.pathParam("playerId");
        IPlayer player = dataStore.getLeaderboard().getPlayer(playerId);

        if (player == null) {
            ctx.html("Error: Player not found. <a href='/'>Go to Home</a>");
            return;
        }

        StringBuilder monstersHtml = new StringBuilder();
        for (IMonster monster : player.getMonsters()) {
            // Calculate HP percentage for bar
            int hpPercent = (monster.getCurrentHp() * 100) / monster.getMaxHp();
            String hpBarClass = hpPercent < 20 ? "critical" : (hpPercent < 50 ? "low" : "");

            monstersHtml.append(String.format("""
                <div class="monster-card">
                    <div class="monster-image-container">
                        <img src="/images/%s.png" alt="%s" class="monster-image"
                             onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
                        <div class="monster-placeholder" data-type="%s" style="display:none;"></div>
                        <div class="level-badge">Lv. %d</div>
                    </div>
                    <div class="monster-info">
                        <h3>%s</h3>
                        <p class="monster-type %s-type">%s Type</p>

                        <div class="hp-bar-container">
                            <div class="hp-label">HP: %d/%d</div>
                            <div class="hp-bar">
                                <div class="hp-fill %s" style="width: %d%%;"></div>
                            </div>
                        </div>

                        <div class="stats-grid">
                            <div class="stat"><span>ATK</span><strong>%d</strong></div>
                            <div class="stat"><span>DEF</span><strong>%d</strong></div>
                            <div class="stat"><span>SPD</span><strong>%d</strong></div>
                            <div class="stat"><span>EXP</span><strong>%d</strong></div>
                        </div>

                        <a href="/battle/new?playerId=%s&monsterId=%s" class="battle-btn">
                            Battle with %s
                        </a>
                    </div>
                </div>
                """,
                monster.getName().toLowerCase(), monster.getName(),
                monster.getType().toString().toLowerCase(),
                monster.getLevel(),
                monster.getName(),
                monster.getType().toString().toLowerCase(), monster.getType(),
                monster.getCurrentHp(), monster.getMaxHp(),
                hpBarClass, hpPercent,
                monster.getAttack(), monster.getDefense(), monster.getSpeed(),
                monster.getExperience(),
                playerId, monster.getId(),
                monster.getName()
            ));
        }

        int rank = dataStore.getLeaderboard().getPlayerRank(playerId);

        String html = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Player Dashboard - %s</title>
                <style>
                    body { font-family: Arial, sans-serif; max-width: 1200px; margin: 50px auto; padding: 20px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); background-attachment: fixed; min-height: 100vh;}
                    h1 { color: #333; text-align: center; }
                    .stats { background: white; padding: 20px; border-radius: 10px; margin: 20px 0; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
                    .stats h2 { margin-top: 0; color: #007bff; }
                    .stats p { margin: 8px 0; font-size: 16px; }

                    h2.monsters-title { color: #333; margin: 30px 0 15px 0; }
                    .monsters-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px; margin: 20px 0; }
                
                    .monster-card { background: white; border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); overflow: hidden; transition: transform 0.2s; }
                    .monster-card:hover { transform: translateY(-5px); box-shadow: 0 4px 12px rgba(0,0,0,0.15); }

                    .monster-image-container { position: relative; height: 200px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); display: flex; align-items: center; justify-content: center; }
                    .monster-image { width: 180px; height: 180px; object-fit: contain; }
                    .monster-placeholder { width: 180px; height: 180px; display: flex; align-items: center; justify-content: center; font-size: 72px; }
                    .monster-placeholder[data-type="fire"]::after { content: '🔥'; }
                    .monster-placeholder[data-type="water"]::after { content: '💧'; }
                    .monster-placeholder[data-type="grass"]::after { content: '🌿'; }
                    .monster-placeholder[data-type="electric"]::after { content: '⚡'; }
                    .monster-placeholder[data-type="normal"]::after { content: '⭐'; }

                    .level-badge { position: absolute; top: 10px; right: 10px; background: rgba(0,0,0,0.7); color: white; padding: 5px 12px; border-radius: 20px; font-weight: bold; font-size: 14px; }

                    .monster-info { padding: 20px; }
                    .monster-info h3 { margin: 0 0 5px 0; color: #333; font-size: 22px; }
                    .monster-type { margin: 5px 0 15px 0; padding: 4px 12px; border-radius: 15px; display: inline-block; color: white; font-size: 13px; font-weight: bold; }
                    .fire-type { background: linear-gradient(135deg, #f12711, #f5af19); }
                    .water-type { background: linear-gradient(135deg, #0575e6, #00f2fe); }
                    .grass-type { background: linear-gradient(135deg, #56ab2f, #a8e063); }
                    .electric-type { background: linear-gradient(135deg, #f2c94c, #f2994a); }
                    .normal-type { background: linear-gradient(135deg, #868f96, #596164); }

                    .hp-bar-container { margin: 15px 0; }
                    .hp-label { font-size: 13px; color: #666; margin-bottom: 5px; }
                    .hp-bar { height: 8px; background: #e0e0e0; border-radius: 4px; overflow: hidden; }
                    .hp-fill { height: 100%%; background: #28a745; transition: width 0.3s; }
                    .hp-fill.low { background: #ffc107; }
                    .hp-fill.critical { background: #dc3545; }

                    .stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px; margin: 15px 0; }
                    .stat { text-align: center; padding: 8px; background: #f8f9fa; border-radius: 5px; }
                    .stat span { display: block; font-size: 11px; color: #666; margin-bottom: 3px; }
                    .stat strong { font-size: 16px; color: #333; }

                    .battle-btn { display: block; width: 100%%; padding: 12px; background: #dc3545; color: white; text-align: center; text-decoration: none; border-radius: 5px; font-weight: bold; margin-top: 15px; transition: background 0.2s; box-sizing: border-box; }
                    .battle-btn:hover { background: #c82333; }

                    .actions { text-align: center; margin: 30px 0; }
                    .actions a { display: inline-block; padding: 12px 24px; margin: 5px; background: #007bff; color: white; text-decoration: none; border-radius: 5px; font-weight: bold; }
                    .actions a:hover { background: #0056b3; }
                    .back{display: inline-block; margin-top: 30px;padding: 12px 24px; background: rgba(255, 255, 255, 0.15);color: #ffffff;text-decoration: none;font-size: 16px; font-weight: bold; border-radius: 30px; border: 2px solid rgba(255, 255, 255, 0.3); backdrop-filter: blur(5px); transition: all 0.3s ease;
                    .back:hover{ background: #ffffff; color: #764ba2; transform: translateY(-3px); box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3); text-decoration: none; }
                </style>
            </head>
            <body>
                <h1>%s's Dashboard</h1>

                <div class="stats">
                    <h2>Player Stats</h2>
                    <p><strong>Rank:</strong> #%d | <strong>Score:</strong> %d</p>
                    <p><strong>Record:</strong> %d-%d (%.1f%% win rate)</p>
                </div>

                <h2 class="monsters-title">Your Monsters</h2>
                <div class="monsters-grid">
                    %s
                </div>

                <div class="actions">
                    <a href="/monster/list">Browse All Monsters</a>
                    <a href="/leaderboard">View Leaderboard</a>
                </div>

                <a href="/" class="back">&larr; Back to Home</a>
            </body>
            </html>
            """,
            player.getUsername(),
            player.getUsername(),
            rank, player.getScore(),
            player.getWins(), player.getLosses(), player.getWinRate(),
            monstersHtml.toString()

        );

        ctx.html(html);
    }

    public void addMonster(Context ctx) {
        String playerId = ctx.pathParam("playerId");
        String monsterName = ctx.formParam("monsterName");

        IPlayer player = dataStore.getLeaderboard().getPlayer(playerId);
        if (player == null) {
            ctx.html("Error: Player not found");
            return;
        }

        IMonster monster = dataStore.getMonsterFactory().getMonsterByName(monsterName);
        if (monster == null) {
            ctx.html("Error: Monster not found");
            return;
        }

        player.addMonster(monster);
        dataStore.getLeaderboard().updatePlayer(player);

        ctx.redirect("/player/" + playerId);
    }

    public void listPlayers(Context ctx) {
        List<IPlayer> allPlayers = dataStore.getLeaderboard().getAllPlayers();

        StringBuilder playersHtml = new StringBuilder();
        for (IPlayer player : allPlayers) {
            // Build monster summary
            StringBuilder monstersInfo = new StringBuilder();
            for (IMonster monster : player.getMonsters()) {
                if (monstersInfo.length() > 0) {
                    monstersInfo.append(", ");
                }
                monstersInfo.append(String.format("%s (Lv.%d)", monster.getName(), monster.getLevel()));
            }

            playersHtml.append(String.format("""
                <div class="player-card">
                    <h3><a href="/player/%s">%s</a></h3>
                    <div class="player-stats">
                        <p><strong>Record:</strong> %d-%d (%.1f%% win rate)</p>
                        <p><strong>Score:</strong> %d</p>
                        <p><strong>Monsters:</strong> %s</p>
                    </div>
                    <div class="player-actions">
                        <a href="/player/%s" class="btn-dashboard">View Dashboard</a>
                        <a href="/battle/new?playerId=%s" class="btn-battle">Start Battle</a>
                    </div>
                </div>
                """,
                player.getId(), player.getUsername(),
                player.getWins(), player.getLosses(), player.getWinRate(),
                player.getScore(),
                monstersInfo.toString(),
                player.getId(), player.getId()
            ));
        }

        String html = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>All Players</title>
                <style>
                    body { font-family: Arial, sans-serif; max-width: 1000px; margin: 50px auto; padding: 20px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); }
                    h1 { color: white; text-align: center; }
                    .subtitle { text-align: center; color: rgba(255,255,255,0.9); margin-bottom: 30px; }
                    .player-card { background: white; margin: 15px 0; padding: 20px; border-radius: 10px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
                    .player-card h3 { margin: 0 0 15px 0; color: #667eea; }
                    .player-card h3 a { color: #667eea; text-decoration: none; }
                    .player-card h3 a:hover { text-decoration: underline; }
                    .player-stats { margin: 10px 0; }
                    .player-stats p { margin: 5px 0; color: #555; }
                    .player-actions { margin-top: 15px; display: flex; gap: 10px; }
                    .player-actions a { padding: 8px 16px; text-decoration: none; border-radius: 5px; font-size: 14px; font-weight: bold; }
                    .btn-dashboard { background: #667eea; color: white; }
                    .btn-dashboard:hover { background: #764ba2; }
                    .btn-battle { background: #28a745; color: white; }
                    .btn-battle:hover { background: #218838; }
                    .actions { text-align: center; margin: 20px 0; }
                    .actions a { display: inline-block; padding: 10px 20px; margin: 5px; background: #667eea; color: white; text-decoration: none; border-radius: 5px; }
                    .actions a:hover { background: #764ba2; }
                    .back{display: inline-block; margin-top: 30px;padding: 12px 24px; background: rgba(255, 255, 255, 0.15);color: #ffffff;text-decoration: none;font-size: 16px; font-weight: bold; border-radius: 30px; border: 2px solid rgba(255, 255, 255, 0.3); backdrop-filter: blur(5px); transition: all 0.3s ease;}
                    .back:hover{ background: #ffffff; color: #764ba2; transform: translateY(-3px); box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3); text-decoration: none; }
                </style>
            </head>
            <body>
                <h1>All Players</h1>
                <p class="subtitle">Select a player to view their dashboard or start a battle</p>

                %s

                <div class="actions">
                    <a href="/player/new">Create New Player</a>
                </div>

                <a href="/" class="back">&larr; Back to Home</a>
            </body>
            </html>
            """,
            playersHtml.toString()
        );

        ctx.html(html);
    }
}
