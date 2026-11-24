package web.checkpoint4;

import io.javalin.http.Context;

/**
 * Home controller for Checkpoint 4 app.
 */
public class Checkpoint4HomeController {
    private final Checkpoint4DataStore dataStore;

    public Checkpoint4HomeController(Checkpoint4DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void showHome(Context ctx) {
        int playerCount = dataStore.getPlayerCount();

        String html = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Checkpoint 4 - Players &amp; Leaderboard</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        max-width: 900px;
                        margin: 50px auto;
                        padding: 20px;
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                        color: white;
                    }
                    .container {
                        background: rgba(255, 255, 255, 0.95);
                        padding: 40px;
                        border-radius: 20px;
                        box-shadow: 0 10px 40px rgba(0,0,0,0.3);
                        color: #333;
                    }
                    h1 {
                        color: #black;
                        text-align: center;
                        font-size: 42px;
                        margin: 0 0 10px 0;
                    }
                    .checkpoint-badge {
                        text-align: center;
                        color: #667eea;
                        font-weight: bold;
                        margin-bottom: 30px;
                        font-size: 18px;
                    }
                    .description {
                        background: #f8f9fa;
                        padding: 20px;
                        border-left: 5px solid #667eea;
                        margin: 30px 0;
                        border-radius: 5px;
                    }
                    .stats {
                        text-align: center;
                        padding: 30px;
                        background: linear-gradient(135deg, #667eea, #764ba2);
                        color: white;
                        border-radius: 15px;
                        margin: 30px 0;
                    }
                    .stats-number {
                        font-size: 64px;
                        font-weight: bold;
                        margin: 10px 0;
                    }
                    .stats-label {
                        font-size: 20px;
                        opacity: 0.9;
                    }
                    .nav {
                        display: grid;
                        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                        gap: 15px;
                        margin: 30px 0;
                    }
                    .nav a, .nav button {
                        display: block;
                        padding: 20px;
                        background: #667eea;
                        color: white;
                        text-decoration: none;
                        border-radius: 10px;
                        text-align: center;
                        font-weight: bold;
                        transition: all 0.3s;
                        font-size: 16px;
                        border: none;
                        cursor: pointer;
                    }
                    .nav a:hover, .nav button:hover {
                        background: #764ba2;
                        transform: translateY(-2px);
                        box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
                    }
                    .features {
                        margin: 30px 0;
                    }
                    .features h2 {
                        color: #667eea;
                        margin-bottom: 15px;
                    }
                    .features ul {
                        list-style: none;
                        padding: 0;
                    }
                    .features li {
                        padding: 12px 0;
                        padding-left: 30px;
                        position: relative;
                    }
                    .features li:before {
                        content: "-";
                        position: absolute;
                        left: 0;
                        color: #black;
                        font-weight: bold;
                        font-size: 20px;
                    }
                    .clear-btn {
                        text-align: center;
                        margin-top: 30px;
                    }
                    .clear-btn button {
                        padding: 10px 20px;
                        background: #dc3545;
                        color: white;
                        border: none;
                        border-radius: 5px;
                        cursor: pointer;
                        font-size: 14px;
                    }
                    .clear-btn button:hover {
                        background: #c82333;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Monster Battle Game</h1>

                    <div class="description">
                        <strong>Introduction</strong>
                        <p>Welcome to the Monster Battle Game, here you can create players battle monsters and check the leaderboard!</p>
                        <p>You're allowed to create new players and battle them with your own monsters!</p>
                    </div>

                    <div class="stats">
                        <div class="stats-number">%d</div>
                        <div class="stats-label">Players</div>
                    </div>

                    <div class="nav">
                        <a href="/player/new">Create Player</a>
                        <a href="/player/list">View All Players</a>
                        <a href="/monster/list">Monster Encyclopedia</a>
                        <a href="/leaderboard">Leaderboard</a>
                        <a href="/player/list">Battle (Select Player)</a>
                    </div>

                    <div class="features">
                        <h2>Game Functionality:</h2>
                        <ul>
                            <li>All previous checkpoint features (monsters, battles, evolution)</li>
                            <li>Player creation and management</li>
                            <li>Multiple monsters per player</li>
                            <li>Player vs Player battles</li>
                            <li>Win/Loss tracking and statistics</li>
                            <li>Leaderboard rankings by score</li>
                        </ul>
                    </div>

                    <div class="clear-btn">
                        <form method="POST" action="/clear" style="display: inline;">
                            <button type="submit" onclick="return confirm('Are you sure you want to delete all players and data?')">Clear All Data</button>
                        </form>
                        <p style="color: #666; font-size: 12px; margin-top: 10px;">Use this to reset and start fresh</p>
                    </div>
                </div>
            </body>
            </html>
            """, playerCount);

        ctx.html(html);
    }
}
