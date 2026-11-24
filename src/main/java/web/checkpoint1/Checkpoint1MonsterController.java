package web.checkpoint1;

import io.javalin.http.Context;
import model.IMonster;
import model.MonsterType;

import java.util.Comparator;
import java.util.List;

/**
 * Controller for monster operations in Checkpoint 1 app.
 */
public class Checkpoint1MonsterController {
    private final Checkpoint1DataStore dataStore;

    public Checkpoint1MonsterController(Checkpoint1DataStore dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Show monster creation form.
     */
    public void showCreateForm(Context ctx) {
        String html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Create Monster - Checkpoint 1</title>
                <style>
                    body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; background: #f5f5f5; }
                    h1 { color: #333; text-align: center; }
                    .nav { margin: 20px 0; text-align: center; }
                    .nav a { display: inline-block; padding: 10px 20px; margin: 5px; background: #007bff; color: white; text-decoration: none; border-radius: 5px; }
                    .nav a:hover { background: #764ba2; }
                    .tabs { display: flex; justify-content: center; margin: 30px 0 0 0; border-bottom: 2px solid #ddd; }
                    .tab { padding: 15px 30px; cursor: pointer; background: #eee; border: none; font-size: 16px; color: #000; }
                    .tab.active { background: white; border-bottom: 2px solid #007bff; margin-bottom: -2px; color: #000; }
                    .tab-content { display: none; background: white; padding: 30px; border-radius: 0 0 10px 10px; }
                    .tab-content.active { display: block; }
                    .form-group { margin: 20px 0; }
                    .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
                    .form-group input, .form-group select { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 5px; }
                    .form-group input[type="number"] { width: 100px; }
                    .stat-slider { width: 100%; }
                    .stat-value { display: inline-block; width: 50px; text-align: right; font-weight: bold; }
                    button { padding: 15px 30px; background: #28a745; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; }
                    button:hover { background: #f1e8fa; }
                    .create-monster-btn:hover {background-color: #268738;}
                    .starter-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 15px; margin: 20px 0; }
                    .starter-btn { padding: 20px; background: white; border: 2px solid #ddd; border-radius: 10px; cursor: pointer; text-align: center; transition: all 0.3s; }
                    .starter-btn:hover { border-color: #007bff; transform: scale(1.05); }
                    .starter-btn.fire { border-color: #ff6b6b; }
                    .starter-btn.water { border-color: #4dabf7; }
                    .starter-btn.grass { border-color: #51cf66; }
                    .starter-btn.electric { border-color: #ffd43b; }
                    .starter-btn.normal { border-color: #adb5bd; }
                    .type-badge { display: inline-block; padding: 5px 15px; border-radius: 20px; color: white; font-weight: bold; }
                    .fire-type { background: #ff6b6b; }
                    .water-type { background: #4dabf7; }
                    .grass-type { background: #51cf66; }
                    .electric-type { background: #ffd43b; color: #333; }
                    .normal-type { background: #adb5bd; }
                </style>
            </head>
            <body>
                <h1>Create a Monster</h1>
                <div class="nav">
                    <a href="/">Home</a>
                    <a href="/monster/all">View All Monsters</a>
                    <a href="/encyclopedia">Encyclopedia</a>
                </div>

                <div class="tabs">
                    <button class="tab active" onclick="showTab('custom')">Custom Monster</button>
                    <button class="tab" onclick="showTab('starter')">Starter Monster</button>
                </div>

                <div id="custom" class="tab-content active">
                    <h2>Create Custom Monster</h2>
                    <form method="POST" action="/monster/create-custom">
                        <div class="form-group">
                            <label>Name:</label>
                            <input type="text" name="name" required placeholder="Enter monster name">
                        </div>
                        <div class="form-group">
                            <label>Type:</label>
                            <select name="type" required>
                                <option value="FIRE">Fire</option>
                                <option value="WATER">Water</option>
                                <option value="GRASS">Grass</option>
                                <option value="ELECTRIC">Electric</option>
                                <option value="NORMAL">Normal</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Max HP: <span class="stat-value" id="hp-value">50</span></label>
                            <input type="range" name="maxHp" min="30" max="100" value="50" class="stat-slider" oninput="document.getElementById('hp-value').textContent = this.value">
                        </div>
                        <div class="form-group">
                            <label>Attack: <span class="stat-value" id="atk-value">25</span></label>
                            <input type="range" name="attack" min="10" max="50" value="25" class="stat-slider" oninput="document.getElementById('atk-value').textContent = this.value">
                        </div>
                        <div class="form-group">
                            <label>Defense: <span class="stat-value" id="def-value">25</span></label>
                            <input type="range" name="defense" min="10" max="50" value="25" class="stat-slider" oninput="document.getElementById('def-value').textContent = this.value">
                        </div>
                        <div class="form-group">
                            <label>Speed: <span class="stat-value" id="spd-value">25</span></label>
                            <input type="range" name="speed" min="10" max="50" value="25" class="stat-slider" oninput="document.getElementById('spd-value').textContent = this.value">
                        </div>
                        <button type="submit" class="create-monster-btn">Create Monster</button>
                    </form>
                </div>

                <div id="starter" class="tab-content">
                    <h2>Choose Your Starter</h2>
                    <p>Select one of the five starter monsters:</p>
                    <form method="POST" action="/monster/create-starter">
                        <div class="starter-grid">
                            <button type="submit" name="type" value="FIRE" class="starter-btn fire">
                                <div class="type-badge fire-type">Fire</div>
                                <h3 style="color: black;">Flameling</h3>
                            </button>
                            <button type="submit" name="type" value="WATER" class="starter-btn water">
                                <div class="type-badge water-type">Water</div>
                                <h3 style="color: black;">Aquabit</h3>
                            </button>
                            <button type="submit" name="type" value="GRASS" class="starter-btn grass">
                                <div class="type-badge grass-type">Grass</div>
                                <h3 style="color: black;">Leafling</h3>
                            </button>
                            <button type="submit" name="type" value="ELECTRIC" class="starter-btn electric">
                                <div class="type-badge electric-type">Electric</div>
                                <h3 style="color: black;">Sparkbit</h3>
                            </button>
                            <button type="submit" name="type" value="NORMAL" class="starter-btn normal">
                                <div class="type-badge normal-type">Normal</div>
                                <h3 style="color: black;">Champ</h3>
                            </button>
                        </div>
                    </form>
                </div>

                <script>
                    function showTab(tabName) {
                        const tabs = document.querySelectorAll('.tab');
                        const contents = document.querySelectorAll('.tab-content');
                        tabs.forEach(t => t.classList.remove('active'));
                        contents.forEach(c => c.classList.remove('active'));
                        event.target.classList.add('active');
                        document.getElementById(tabName).classList.add('active');
                    }
                </script>
            </body>
            </html>
            """;
        ctx.html(html);
    }

    /**
     * Create a custom monster from form data.
     */
    public void createCustomMonster(Context ctx) {
        try {
            String name = ctx.formParam("name");
            MonsterType type = MonsterType.valueOf(ctx.formParam("type"));
            int maxHp = Integer.parseInt(ctx.formParam("maxHp"));
            int attack = Integer.parseInt(ctx.formParam("attack"));
            int defense = Integer.parseInt(ctx.formParam("defense"));
            int speed = Integer.parseInt(ctx.formParam("speed"));

            IMonster monster = dataStore.getMonsterFactory().createMonster(name, type, maxHp, attack, defense, speed);
            String id = dataStore.addMonster(monster);

            ctx.redirect("/monster/" + id);
        } catch (Exception e) {
            ctx.html("<h1>Error</h1><p>Failed to create monster: " + e.getMessage() + "</p><a href='/monster/create'>Try Again</a>");
        }
    }

    /**
     * Create a starter monster from form data.
     */
    public void createStarterMonster(Context ctx) {
        try {
            MonsterType type = MonsterType.valueOf(ctx.formParam("type"));
            IMonster monster = dataStore.getMonsterFactory().createStarterMonster(type);
            String id = dataStore.addMonster(monster);

            ctx.redirect("/monster/" + id);
        } catch (Exception e) {
            ctx.html("<h1>Error</h1><p>Failed to create starter monster: " + e.getMessage() + "</p><a href='/monster/create'>Try Again</a>");
        }
    }

    /**
     * List all user-created monsters.
     */
    public void listAllMonsters(Context ctx) {
        try {
            List<IMonster> monsters = dataStore.getAllMonsters();

            // Filter out null monsters
            monsters.removeIf(m -> m == null);

            // Sort by name
            monsters.sort(Comparator.comparing(IMonster::getName));

            StringBuilder monstersHtml = new StringBuilder();

            if (monsters.isEmpty()) {
                monstersHtml.append("<p style='text-align: center; color: #666; font-size: 18px; margin: 50px 0;'>No monsters created yet. <a href='/monster/create'>Create your first monster!</a></p>");
            } else {
                for (IMonster monster : monsters) {
                    if (monster == null) continue; // Extra safety check
                String typeClass = monster.getType().toString().toLowerCase();
                monstersHtml.append(String.format("""
                    <div class="monster-card">
                        <div class="type-badge %s-type">%s</div>
                        <img src="/images/%s.png" alt="%s" class="monster-image"
                             onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
                        <div class="monster-placeholder" data-type="%s" style="display:none;"></div>
                        <h3>%s</h3>
                        <div class="stats">
                            <div class="stat"><strong>HP:</strong> %d/%d</div>
                            <div class="stat"><strong>ATK:</strong> %d</div>
                            <div class="stat"><strong>DEF:</strong> %d</div>
                            <div class="stat"><strong>SPD:</strong> %d</div>
                        </div>
                        <a href="/monster/%s" class="view-btn">View Details</a>
                    </div>
                    """,
                    typeClass, monster.getType(),
                    monster.getName().toLowerCase(), monster.getName(),
                    typeClass,
                    monster.getName(),
                    monster.getCurrentHp(), monster.getMaxHp(),
                    monster.getAttack(), monster.getDefense(), monster.getSpeed(),
                    monster.getId()
                ));
            }
        }

        String html = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>My Monsters - Checkpoint 1</title>
                <style>
                    body { font-family: Arial, sans-serif; max-width: 1200px; margin: 50px auto; padding: 20px; background: #f5f5f5; }
                    h1 { color: #333; text-align: center; }
                    .nav { margin: 20px 0; text-align: center; }
                    .nav a { display: inline-block; padding: 10px 20px; margin: 5px; background: #007bff; color: white; text-decoration: none; border-radius: 5px; }
                    .nav a:hover { background: #0056b3; }
                    .monster-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 20px; margin: 30px 0; }
                    .monster-card { background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); position: relative; }
                    .monster-image { width: 100%%; max-width: 150px; height: 150px; object-fit: contain; display: block; margin: 10px auto; }
                    .monster-placeholder { width: 150px; height: 150px; margin: 10px auto; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 48px; }
                    .monster-placeholder[data-type="fire"] { background: linear-gradient(135deg, #ff6b6b, #ff8787); }
                    .monster-placeholder[data-type="water"] { background: linear-gradient(135deg, #4dabf7, #74c0fc); }
                    .monster-placeholder[data-type="grass"] { background: linear-gradient(135deg, #51cf66, #8ce99a); }
                    .monster-placeholder[data-type="electric"] { background: linear-gradient(135deg, #ffd43b, #ffe066); }
                    .monster-placeholder[data-type="normal"] { background: linear-gradient(135deg, #adb5bd, #ced4da); }
                    .type-badge { position: absolute; top: 10px; right: 10px; padding: 5px 15px; border-radius: 20px; color: white; font-weight: bold; font-size: 12px; }
                    .fire-type { background: #ff6b6b; }
                    .water-type { background: #4dabf7; }
                    .grass-type { background: #51cf66; }
                    .electric-type { background: #ffd43b; color: #333; }
                    .normal-type { background: #adb5bd; }
                    .monster-card h3 { margin: 10px 0; color: #333; text-align: center; }
                    .stats { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin: 15px 0; }
                    .stat { padding: 8px; background: #f8f9fa; border-radius: 5px; font-size: 14px; }
                    .view-btn { display: block; text-align: center; padding: 10px; background: #007bff; color: white; text-decoration: none; border-radius: 5px; margin-top: 15px; }
                    .view-btn:hover { background: #0056b3; }
                    .count { text-align: center; color: #666; margin: 20px 0; }
                </style>
            </head>
            <body>
                <h1>My Monsters</h1>
                <div class="count">Total Monsters: %d</div>
                <div class="nav">
                    <a href="/">Home</a>
                    <a href="/monster/create">Create Monster</a>
                    <a href="/encyclopedia">Encyclopedia</a>
                </div>
                <div class="monster-grid">
                    %s
                </div>
            </body>
            </html>
            """, monsters.size(), monstersHtml.toString());

            ctx.html(html);
        } catch (Exception e) {
            ctx.html("<h1>Error Loading Monsters</h1><p>Error: " + e.getMessage() + "</p><p>" + e.getClass().getName() + "</p><a href='/'>Back to Home</a>");
            e.printStackTrace();
        }
    }

    /**
     * View monster detail page.
     */
    public void viewMonsterDetail(Context ctx) {
        String id = ctx.pathParam("id");
        IMonster monster = dataStore.getMonster(id);

        if (monster == null) {
            ctx.html("<h1>Monster Not Found</h1><p><a href='/monster/all'>Back to List</a></p>");
            return;
        }

        String typeClass = monster.getType().toString().toLowerCase();

        String html = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>%s - Monster Detail</title>
                <style>
                    body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; background: #f5f5f5; }
                    h1 { color: #333; text-align: center; }
                    .nav { margin: 20px 0; text-align: center; }
                    .nav a { display: inline-block; padding: 10px 20px; margin: 5px; background: #007bff; color: white; text-decoration: none; border-radius: 5px; }
                    .nav a:hover { background: #0056b3; }
                    .monster-detail { background: white; padding: 40px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .monster-image-large { width: 100%%; max-width: 250px; height: 250px; object-fit: contain; display: block; margin: 20px auto; }
                    .monster-placeholder-large { width: 250px; height: 250px; margin: 20px auto; border-radius: 15px; display: flex; align-items: center; justify-content: center; font-size: 72px; }
                    .monster-placeholder-large[data-type="fire"] { background: linear-gradient(135deg, #ff6b6b, #ff8787); }
                    .monster-placeholder-large[data-type="water"] { background: linear-gradient(135deg, #4dabf7, #74c0fc); }
                    .monster-placeholder-large[data-type="grass"] { background: linear-gradient(135deg, #51cf66, #8ce99a); }
                    .monster-placeholder-large[data-type="electric"] { background: linear-gradient(135deg, #ffd43b, #ffe066); }
                    .monster-placeholder-large[data-type="normal"] { background: linear-gradient(135deg, #adb5bd, #ced4da); }
                    .type-badge { display: inline-block; padding: 8px 20px; border-radius: 20px; color: white; font-weight: bold; margin: 10px 0; }
                    .fire-type { background: #ff6b6b; }
                    .water-type { background: #4dabf7; }
                    .grass-type { background: #51cf66; }
                    .electric-type { background: #ffd43b; color: #333; }
                    .normal-type { background: #adb5bd; }
                    .stat-row { display: flex; justify-content: space-between; align-items: center; padding: 15px; background: #f8f9fa; margin: 10px 0; border-radius: 5px; }
                    .stat-label { font-weight: bold; color: #666; }
                    .stat-value { font-size: 24px; color: #333; }
                    .id-info { text-align: center; color: #999; font-size: 12px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <h1>%s</h1>
                <div class="nav">
                    <a href="/">Home</a>
                    <a href="/monster/all">View All Monsters</a>
                    <a href="/monster/create">Create Another</a>
                </div>
                <div class="monster-detail">
                    <div style="text-align: center;">
                        <div class="type-badge %s-type">%s Type</div>
                    </div>
                    <img src="/images/%s.png" alt="%s" class="monster-image-large"
                         onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
                    <div class="monster-placeholder-large" data-type="%s" style="display:none;"></div>
                    <div class="stat-row">
                        <span class="stat-label">Current HP</span>
                        <span class="stat-value">%d</span>
                    </div>
                    <div class="stat-row">
                        <span class="stat-label">Max HP</span>
                        <span class="stat-value">%d</span>
                    </div>
                    <div class="stat-row">
                        <span class="stat-label">Attack</span>
                        <span class="stat-value">%d</span>
                    </div>
                    <div class="stat-row">
                        <span class="stat-label">Defense</span>
                        <span class="stat-value">%d</span>
                    </div>
                    <div class="stat-row">
                        <span class="stat-label">Speed</span>
                        <span class="stat-value">%d</span>
                    </div>
                    <div class="id-info">Monster ID: %s</div>
                </div>
            </body>
            </html>
            """,
            monster.getName(),
            monster.getName(),
            typeClass, monster.getType(),
            monster.getName().toLowerCase(), monster.getName(),
            typeClass,
            monster.getCurrentHp(),
            monster.getMaxHp(),
            monster.getAttack(),
            monster.getDefense(),
            monster.getSpeed(),
            monster.getId()
        );

        ctx.html(html);
    }

    /**
     * Show monster encyclopedia (all templates).
     */
    public void showEncyclopedia(Context ctx) {
        // Define evolution chains
        String[][] evolutionChains = {
            {"Fire", "Flameling", "Pyroclaw", "Infernoth"},
            {"Water", "Aquabit", "Tidalwave", "Tidalord"},
            {"Grass", "Leafling", "Vinewhip", "Terravine"},
            {"Electric", "Sparkbit", "Voltail", "Thunderex"},
            {"Normal", "Champ", "Prowler", "Apexar"}
        };

        StringBuilder monstersHtml = new StringBuilder();

        for (String[] chain : evolutionChains) {
            String typeName = chain[0];

            monstersHtml.append(String.format("""
                <div class="evolution-chain">
                    <h2 class="type-header %s-type">%s Type</h2>
                    <div class="evolution-row">
                """, typeName.toLowerCase(), typeName));

            for (int i = 1; i < chain.length; i++) {
                String monsterName = chain[i];
                IMonster monster = dataStore.getMonsterFactory().getMonsterByName(monsterName);

                if (monster != null) {
                    String arrowHtml = (i < chain.length - 1) ? "<div class='evolution-arrow'>→</div>" : "";

                    monstersHtml.append(String.format("""
                        <div class="monster-card">
                            <div class="stage-badge">Stage %d</div>
                            <img src="/images/%s.png" alt="%s" class="monster-image-small"
                                 onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
                            <div class="monster-placeholder-small" data-type="%s" style="display:none;"></div>
                            <h3>%s</h3>
                            <p class="monster-type">%s Type</p>
                            <div class="stats">
                                <div class="stat"><strong>HP:</strong> %d</div>
                                <div class="stat"><strong>ATK:</strong> %d</div>
                                <div class="stat"><strong>DEF:</strong> %d</div>
                                <div class="stat"><strong>SPD:</strong> %d</div>
                            </div>
                        </div>
                        %s
                        """,
                        i,
                        monster.getName().toLowerCase(), monster.getName(),
                        monster.getType().toString().toLowerCase(),
                        monster.getName(),
                        monster.getType(),
                        monster.getMaxHp(), monster.getAttack(),
                        monster.getDefense(), monster.getSpeed(),
                        arrowHtml
                    ));
                }
            }

            monstersHtml.append("</div></div>");
        }

        String html = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Monster Encyclopedia - Checkpoint 1</title>
                <style>
                    body { font-family: Arial, sans-serif; max-width: 1200px; margin: 50px auto; padding: 20px; background: #f5f5f5; }
                    h1 { color: #333; text-align: center; }
                    .subtitle { text-align: center; color: #666; margin-bottom: 30px; }
                    .nav { margin: 20px 0; text-align: center; }
                    .nav a { display: inline-block; padding: 10px 20px; margin: 5px; background: #007bff; color: white; text-decoration: none; border-radius: 5px; }
                    .nav a:hover { background: #0056b3; }
                    .evolution-chain { background: white; padding: 30px; margin: 20px 0; border-radius: 10px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
                    .type-header { text-align: center; margin: 0 0 30px 0; padding: 15px; border-radius: 10px; color: white; }
                    .fire-type { background: linear-gradient(135deg, #ff6b6b, #ff8787); }
                    .water-type { background: linear-gradient(135deg, #4dabf7, #74c0fc); }
                    .grass-type { background: linear-gradient(135deg, #51cf66, #8ce99a); }
                    .electric-type { background: linear-gradient(135deg, #ffd43b, #ffe066); color: #333; }
                    .normal-type { background: linear-gradient(135deg, #adb5bd, #ced4da); }
                    .evolution-row { display: flex; align-items: center; justify-content: center; flex-wrap: wrap; gap: 20px; }
                    .monster-card { background: #f8f9fa; padding: 20px; border-radius: 10px; min-width: 200px; position: relative; }
                    .monster-image-small { width: 100%%; max-width: 120px; height: 120px; object-fit: contain; display: block; margin: 10px auto; }
                    .monster-placeholder-small { width: 120px; height: 120px; margin: 10px auto; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 36px; }
                    .monster-placeholder-small[data-type="fire"] { background: linear-gradient(135deg, #ff6b6b, #ff8787); }
                    .monster-placeholder-small[data-type="water"] { background: linear-gradient(135deg, #4dabf7, #74c0fc); }
                    .monster-placeholder-small[data-type="grass"] { background: linear-gradient(135deg, #51cf66, #8ce99a); }
                    .monster-placeholder-small[data-type="electric"] { background: linear-gradient(135deg, #ffd43b, #ffe066); }
                    .monster-placeholder-small[data-type="normal"] { background: linear-gradient(135deg, #adb5bd, #ced4da); }
                    .stage-badge { position: absolute; top: 10px; right: 10px; background: #007bff; color: white; padding: 5px 10px; border-radius: 15px; font-size: 12px; }
                    .monster-card h3 { margin: 10px 0 5px 0; color: #333; text-align: center; }
                    .monster-type { color: #666; margin: 0 0 15px 0; font-size: 14px; }
                    .stats { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; }
                    .stat { padding: 8px; background: white; border-radius: 5px; font-size: 14px; text-align: center; }
                    .evolution-arrow { font-size: 40px; color: #007bff; font-weight: bold; }
                </style>
            </head>
            <body>
                <h1>Monster Encyclopedia</h1>
                <div class="subtitle">All 15 monster templates available in the game</div>
                <div class="nav">
                    <a href="/">Home</a>
                    <a href="/monster/create">Create Monster</a>
                    <a href="/monster/all">My Monsters</a>
                </div>
                %s
            </body>
            </html>
            """, monstersHtml.toString());

        ctx.html(html);
    }
}
