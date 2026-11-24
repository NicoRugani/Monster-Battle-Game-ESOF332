package web.checkpoint1;

import io.javalin.http.Context;

/**
 * Home controller for Checkpoint 1 app.
 */
public class Checkpoint1HomeController {
    private final Checkpoint1DataStore dataStore;

    public Checkpoint1HomeController(Checkpoint1DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void showHome(Context ctx) {
        int monsterCount = dataStore.getMonsterCount();

        String html = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Checkpoint 1 - Monster System</title>
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
                    .nav a {
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
                    }
                    .nav a:hover {
                        background: #764ba2;
                        transform: translateY(-2px);
                        box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
                    }
                    .features {
                        margin: 30px 0;
                    }
                    .features h2 {
                        
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
                    .info-box {
                        background: #fff3cd;
                        border: 1px solid #ffc107;
                        padding: 15px;
                        border-radius: 8px;
                        margin: 20px 0;
                    }
                    .info-box strong {
                        color: #856404;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Monster Battle Game</h1>
                    <div class="checkpoint-badge">CHECKPOINT 1: Monster System</div>

                    <div class="description">
                        <strong>Checkpoint 1: Monster System</strong>
                        <p>This web application helps you test your <code>Monster</code> and <code>MonsterFactory</code> implementations.</p>
                        <p>You can create custom monsters, generate starter monsters, and explore all 15 available monster templates in the encyclopedia.</p>
                    </div>

                    <div class="stats">
                        <div class="stats-number">%d</div>
                        <div class="stats-label">Monsters Created</div>
                    </div>

                    <div class="nav">
                        <a href="/monster/create">Create Monster</a>
                        <a href="/monster/all">View My Monsters</a>
                        <a href="/encyclopedia">Encyclopedia</a>
                    </div>

                    <div class="features">
                        <h2>Game Functionality:</h2>
                        <ul>
                            <li>Create custom monsters with specific stats</li>
                            <li>Create starter monsters (Fire, Water, Grass, Electric, Normal)</li>
                            <li>View monster properties (HP, Attack, Defense, Speed)</li>
                            <li>View all 15 monster templates in the Encyclopedia </li>
                        </ul>
                    </div>

                    <div class="clear-btn">
                        <form method="POST" action="/clear" style="display: inline;">
                            <button type="submit" onclick="return confirm('Are you sure you want to delete all monsters?')">Clear All Data</button>
                        </form>
                        <p style="color: #666; font-size: 12px; margin-top: 10px;">Use this to reset and start fresh</p>
                    </div>
                </div>
            </body>
            </html>
            """, monsterCount);

        ctx.html(html);
    }
}
