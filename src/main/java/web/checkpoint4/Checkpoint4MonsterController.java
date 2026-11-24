package web.checkpoint4;

import io.javalin.http.Context;
import model.*;

import java.util.List;

/**
 * Controller for monster-related operations.
 */
public class Checkpoint4MonsterController {
    private final Checkpoint4DataStore dataStore;

    public Checkpoint4MonsterController(Checkpoint4DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void listMonsters(Context ctx) {
        // Define evolution chains in order
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

            // Type header
            monstersHtml.append(String.format("""
                <div class="evolution-chain">
                    <h2 class="type-header %s-type">%s Type Evolution</h2>
                    <div class="evolution-row">
                """, typeName.toLowerCase(), typeName));

            // Add each monster in the chain
            for (int i = 1; i < chain.length; i++) {
                String monsterName = chain[i];
                IMonster monster = dataStore.getMonsterFactory().getMonsterByName(monsterName);

                if (monster != null) {
                    int stage = dataStore.getEvolutionManager().getEvolutionStage(monster);
                    String nextEvolution = dataStore.getEvolutionManager().getNextEvolution(monster);
                    int evolutionLevel = dataStore.getEvolutionManager().getEvolutionLevel(monster);

                    String evolutionInfo = nextEvolution != null ?
                        String.format("→ Evolves at Lv.%d", evolutionLevel) :
                        "Final Form";

                    String arrowHtml = (i < chain.length - 1) ? "<div class='evolution-arrow'>→</div>" : "";

                    monstersHtml.append(String.format("""
                        <div class="monster-card">
                            <div class="stage-badge">Stage %d</div>
                            <img src="/images/%s.png" alt="%s" class="monster-image"
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
                            <p class="evolution-info">%s</p>
                        </div>
                        %s
                        """,
                        stage,
                        monster.getName().toLowerCase(), monster.getName(),
                        monster.getType().toString().toLowerCase(),
                        monster.getName(),
                        monster.getType(),
                        monster.getMaxHp(), monster.getAttack(),
                        monster.getDefense(), monster.getSpeed(),
                        evolutionInfo,
                        arrowHtml
                    ));
                }
            }

            monstersHtml.append("</div></div>"); // Close evolution-row and evolution-chain
        }

        String html = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Monster Encyclopedia</title>
                <style>
                    body { font-family: Arial, sans-serif; max-width: 1200px; margin: 50px auto; padding: 20px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); }
                    h1 { color: #ffffff; text-align: center; margin-bottom: 10px; }
                    .subtitle { text-align: center; color: #f0f4f8; margin-bottom: 30px; }
                    .evolution-chain { background: white; margin: 30px 0; padding: 20px; border-radius: 10px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
                    .type-header { margin: 0 0 20px 0; padding: 10px; border-radius: 5px; color: white; text-align: center; }
                    .fire-type { background: linear-gradient(135deg, #f12711, #f5af19); }
                    .water-type { background: linear-gradient(135deg, #0575e6, #00f2fe); }
                    .grass-type { background: linear-gradient(135deg, #56ab2f, #a8e063); }
                    .electric-type { background: linear-gradient(135deg, #f2c94c, #f2994a); }
                    .normal-type { background: linear-gradient(135deg, #868f96, #596164); }
                    .evolution-row { display: flex; justify-content: center; align-items: center; gap: 20px; flex-wrap: wrap; }
                    .monster-card { background: #f9f9f9; border: 3px solid #ddd; border-radius: 10px; padding: 15px; width: 200px; text-align: center; position: relative; }
                    .monster-card h3 { color: #333; margin: 10px 0 5px 0; font-size: 18px; }
                    .monster-type { color: #666; font-size: 14px; margin: 5px 0; }
                    .monster-image { width: 150px; height: 150px; object-fit: contain; margin: 10px auto; display: block; border-radius: 5px; }
                    .monster-placeholder-small { width: 150px; height: 150px; background: #f9f9f9;; border-radius: 5px; margin: 10px auto; display: flex; align-items: center; justify-content: center; font-size: 48px; }
                    .monster-placeholder-small[data-type="fire"]::after { content: '🔥'; }
                    .monster-placeholder-small[data-type="water"]::after { content: '💧'; }
                    .monster-placeholder-small[data-type="grass"]::after { content: '🌿'; }
                    .monster-placeholder-small[data-type="electric"]::after { content: '⚡'; }
                    .monster-placeholder-small[data-type="normal"]::after { content: '⭐'; }
                    .stage-badge { position: absolute; top: 10px; right: 10px; background: #007bff; color: white; padding: 3px 8px; border-radius: 12px; font-size: 12px; font-weight: bold; }
                    .stats { background: white; padding: 10px; border-radius: 5px; margin: 10px 0; text-align: left; }
                    .stat { display: flex; justify-content: space-between; margin: 3px 0; font-size: 14px; }
                    .evolution-info { color: #28a745; font-weight: bold; margin: 10px 0 5px 0; font-size: 13px; }
                    .evolution-arrow { font-size: 48px; color: #007bff; font-weight: bold; }
                    .back{display: inline-block; margin-top: 30px;padding: 12px 24px; background: rgba(255, 255, 255, 0.15);color: #ffffff;text-decoration: none;font-size: 16px; font-weight: bold; border-radius: 30px; border: 2px solid rgba(255, 255, 255, 0.3); backdrop-filter: blur(5px); transition: all 0.3s ease;}
                    .back:hover{ background: #ffffff; color: #764ba2; transform: translateY(-3px); box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3); text-decoration: none; }
                </style>
            </head>
            <body>
                <h1>Monster Encyclopedia</h1>
                <p class="subtitle">Complete evolution chains from starter to final form</p>

                %s

                <div style="text-align: center;">
                    <a href="/" class="back">&larr; Back to Home</a>
                </div>
            </body>
            </html>
            """,
            monstersHtml.toString()
        );

        ctx.html(html);
    }

    public void evolveMonster(Context ctx) {
        String monsterId = ctx.pathParam("monsterId");
        // This would require storing monster instances somewhere
        // For now, just redirect back
        ctx.redirect("/monster/list");
    }
}
