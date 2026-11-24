package web.checkpoint3;

import io.javalin.http.Context;
import model.IMonster;
import java.util.List;

/**
 * Controller for evolution operations in Checkpoint 3 app.
 */
public class Checkpoint3EvolutionController {
    private final Checkpoint3DataStore dataStore;

    public Checkpoint3EvolutionController(Checkpoint3DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void showProgression(Context ctx) {
        try {
            List<IMonster> monsters = dataStore.getMonstersByLevel();

            // Filter out null monsters
            monsters.removeIf(m -> m == null);

            StringBuilder rows = new StringBuilder();

            for (IMonster m : monsters) {
                if (m == null) continue; // Extra safety check

                int xpForNext = (m.getLevel() + 1) * 100;
                int xpProgress = (m.getExperience() * 100) / xpForNext;

                rows.append(String.format("""
                <tr>
                    <td><strong>%s</strong></td>
                    <td><span class="type-badge %s-type">%s</span></td>
                    <td>%d</td>
                    <td>
                        <div class="xp-bar-container">
                            <div class="xp-bar" style="width: %d%%"></div>
                            <div class="xp-text">%d / %d</div>
                        </div>
                    </td>
                </tr>
                """,
                m.getName(),
                m.getType().toString().toLowerCase(), m.getType(),
                m.getLevel(),
                xpProgress, m.getExperience(), xpForNext
            ));
        }

        String html = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Monster Progression</title>
                <style>
                    body { font-family: Arial; max-width: 1200px; margin: 50px auto; padding: 20px; background: #f5f5f5; }
                    h1 { text-align: center; color: #333; }
                    .nav { margin: 20px 0; text-align: center; }
                    .nav a { display: inline-block; padding: 10px 20px; margin: 5px; background: #007bff; color: white; text-decoration: none; border-radius: 5px; }
                    table { width: 100%%; background: white; border-collapse: collapse; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    th { background: #667eea; color: white; padding: 15px; text-align: left; }
                    td { padding: 12px 15px; border-bottom: 1px solid #eee; }
                    .type-badge { padding: 5px 12px; border-radius: 15px; color: white; font-size: 12px; font-weight: bold; }
                    .fire-type { background: #ff6b6b; }
                    .water-type { background: #4dabf7; }
                    .grass-type { background: #51cf66; }
                    .electric-type { background: #ffd43b; color: #333; }
                    .normal-type { background: #adb5bd; }
                    .xp-bar-container { background: #e9ecef; height: 20px; border-radius: 10px; position: relative; overflow: hidden; }
                    .xp-bar { background: linear-gradient(90deg, #a8b4ff, #c5a8e0); height: 100%%; position: absolute; top: 0; left: 0; }
                    .xp-text { position: absolute; top: 0; left: 0; width: 100%%; height: 100%%; display: flex; align-items: center; justify-content: center; color: #333; font-size: 12px; font-weight: bold; }
                    .btn-evolve { padding: 5px 15px; background: #28a745; color: white; text-decoration: none; border-radius: 5px; font-size: 12px; }
                </style>
            </head>
            <body>
                <h1>Monster Progression Dashboard</h1>
                <div class="nav">
                    <a href="/">Home</a>
                    <a href="/monster/all">View Monsters</a>
                    <a href="/battle/new">Start Battle</a>
                </div>
                <table>
                    <thead>
                        <tr><th>Name</th><th>Type</th><th>Level</th><th>Experience</th></tr>
                    </thead>
                    <tbody>%s</tbody>
                </table>
            </body>
            </html>
            """, rows.toString());
            ctx.html(html);
        } catch (Exception e) {
            ctx.html("<h1>Error Loading Progression</h1><p>Error: " + e.getMessage() + "</p><p>" + e.getClass().getName() + "</p><a href='/'>Back to Home</a>");
            e.printStackTrace();
        }
    }

    public void showEvolutionPage(Context ctx) {
        String id = ctx.pathParam("id");
        IMonster monster = dataStore.getMonster(id);
        if (monster == null) {
            ctx.html("<h1>Monster Not Found</h1>");
            return;
        }
        
        boolean canEvolve = dataStore.canEvolve(id);
        String nextEvo = dataStore.getEvolutionManager().getNextEvolution(monster);
        int evoLevel = dataStore.getEvolutionManager().getEvolutionLevel(monster);
        
        String content;
        if (canEvolve && nextEvo != null) {
            // Simulate evolution to get actual evolved stats
            IMonster evolved = dataStore.getEvolutionManager().evolve(monster);
            content = String.format("""
                <h2>%s is ready to evolve!</h2>
                <div class="evolution-preview">
                    <div class="monster-before">
                        <h3>Before</h3>
                        <h4>%s</h4>
                        <p>Level %d</p>
                        <div class="stats">
                            <div>HP: %d</div><div>ATK: %d</div>
                            <div>DEF: %d</div><div>SPD: %d</div>
                        </div>
                    </div>
                    <div class="arrow">--></div>
                    <div class="monster-after">
                        <h3>After</h3>
                        <h4>%s</h4>
                        <p>Level %d</p>
                        <div class="stats">
                            <div>HP: %d</div><div>ATK: %d</div>
                            <div>DEF: %d</div><div>SPD: %d</div>
                        </div>
                    </div>
                </div>
                <form method="POST" action="/monster/%s/evolve">
                    <button type="submit">Evolve Now!</button>
                </form>
                """,
                monster.getName(),
                monster.getName(), monster.getLevel(),
                monster.getMaxHp(), monster.getAttack(), monster.getDefense(), monster.getSpeed(),
                evolved.getName(), evolved.getLevel(),
                evolved.getMaxHp(), evolved.getAttack(), evolved.getDefense(), evolved.getSpeed(),
                id
            );
        } else {
            content = String.format("""
                <h2>%s cannot evolve yet</h2>
                <p>Current Level: %d</p>
                <p>%s</p>
                """,
                monster.getName(), monster.getLevel(),
                nextEvo != null ? "Evolves at level " + evoLevel : "This is the final evolution!"
            );
        }
        
        ctx.html(String.format("""
            <!DOCTYPE html><html><head><title>Evolution</title>
            <style>
                body { font-family: Arial; max-width: 900px; margin: 50px auto; padding: 20px; background: #f5f5f5; text-align: center; }
                .evolution-preview { display: flex; justify-content: center; align-items: center; gap: 30px; margin: 30px 0; }
                .monster-before, .monster-after { background: white; padding: 30px; border-radius: 15px; box-shadow: 0 5px 15px rgba(0,0,0,0.1); flex: 1; }
                .arrow { font-size: 48px; font-weight: bold; color: #667eea; }
                .stats { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin-top: 15px; }
                .stats div { background: #f8f9fa; padding: 10px; border-radius: 5px; }
                button { padding: 20px 60px; background: #28a745; color: white; border: none; border-radius: 50px; font-size: 20px; font-weight: bold; cursor: pointer; }
                a { display: inline-block; margin: 20px 10px; padding: 10px 20px; background: #007bff; color: white; text-decoration: none; border-radius: 5px; }
            </style></head><body>
            %s
            <a href="/progression">Back to Progression</a>
            <a href="/">Home</a>
            </body></html>
            """, content));
    }

    public void performEvolution(Context ctx) {
        String id = ctx.pathParam("id");
        try {
            IMonster evolved = dataStore.evolveMonster(id);
            ctx.html(String.format("""
                <!DOCTYPE html><html><head><title>Evolution Success!</title>
                <style>
                    body { font-family: Arial; max-width: 600px; margin: 100px auto; text-align: center; background: linear-gradient(135deg, #667eea, #764ba2); color: white; padding: 50px; }
                    h1 { font-size: 48px; margin: 20px 0; }
                    a { display: inline-block; margin: 20px 10px; padding: 15px 30px; background: white; color: #667eea; text-decoration: none; border-radius: 10px; font-weight: bold; }
                </style></head><body>
                <h1>Evolution Complete!</h1>
                <h2>Your monster evolved into %s!</h2>
                <a href="/monster/%s">View Monster</a>
                <a href="/progression">Progression Dashboard</a>
                </body></html>
                """, evolved.getName(), id));
        } catch (Exception e) {
            ctx.html("<h1>Error:</h1><p>" + e.getMessage() + "</p><a href='/progression'>Back</a>");
        }
    }
}
