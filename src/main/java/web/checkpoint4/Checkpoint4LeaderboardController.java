package web.checkpoint4;

import io.javalin.http.Context;
import model.*;

import java.util.List;

/**
 * Controller for leaderboard operations.
 */
public class Checkpoint4LeaderboardController {
    private final Checkpoint4DataStore dataStore;

    public Checkpoint4LeaderboardController(Checkpoint4DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void showLeaderboard(Context ctx) {
        List<IPlayer> allPlayers = dataStore.getLeaderboard().getAllPlayers();

        StringBuilder playersHtml = new StringBuilder();
        int rank = 1;
        for (IPlayer player : allPlayers) {
            String rankClass = rank <= 3 ? "top-rank" : "";
            String medalEmoji = switch (rank) {
                case 1 -> "🥇 ";
                case 2 -> "🥈 ";
                case 3 -> "🥉 ";
                default -> "";
            };

            playersHtml.append(String.format("""
                <tr class="%s">
                    <td>%s#%d</td>
                    <td><a href="/player/%s">%s</a></td>
                    <td>%d</td>
                    <td>%d</td>
                    <td>%d</td>
                    <td>%.1f%%</td>
                    <td>%d</td>
                </tr>
                """,
                rankClass, medalEmoji, rank, player.getId(), player.getUsername(),
                player.getScore(), player.getWins(), player.getLosses(),
                player.getWinRate(), player.getMonsters().size()
            ));
            rank++;
        }

        String emptyMessage = allPlayers.isEmpty() ?
            "<tr><td colspan='7' style='text-align: center;'>No players yet. <a href='/player/new'>Be the first!</a></td></tr>" :
            "";

        String html = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Leaderboard</title>
                <style>
                    body { font-family: Arial, sans-serif; max-width: 1100px; margin: 50px auto; padding: 20px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); }
                    h1 { color: #333; text-align: center; }
                    table { width: 100%%; border-collapse: collapse; background: white; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    th { background: #007bff; color: white; padding: 15px; text-align: left; }
                    td { padding: 12px; border-bottom: 1px solid #ddd; }
                    tr:hover { background: #f8f9fa; }
                    .top-rank { background: #fff3cd; }
                    .top-rank:hover { background: #ffeaa7; }
                    a { color: #007bff; text-decoration: none; }
                    a:hover { text-decoration: underline; }
                    .actions { text-align: center; margin: 20px 0; }
                    .actions a { display: inline-block; padding: 10px 20px; margin: 5px; background: #667eea; color: white; text-decoration: none; border-radius: 5px; }
                    .actions a:hover { background: #764ba2; }
                    .back{display: inline-block; margin-top: 30px;padding: 12px 24px; background: rgba(255, 255, 255, 0.15);color: #ffffff;text-decoration: none;font-size: 16px; font-weight: bold; border-radius: 30px; border: 2px solid rgba(255, 255, 255, 0.3); backdrop-filter: blur(5px); transition: all 0.3s ease; }
                    .back:hover{ background: #ffffff; color: #764ba2; transform: translateY(-3px); box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3); text-decoration: none; }
                    .stats { text-align: center; margin: 20px 0; color: #666; }
                    fieldset{background: white;
                    padding: 40px;
                    border-radius: 20px;
                    box-shadow: 0 10px 40px rgba(0,0,0,0.3);
                    color: #333; }
                </style>
            </head>
            <body>

                <fieldset>
                    <h1>Leaderboard</h1>

                    <div class="stats">
                        <p>Total Players: %d</p>
                    </div>

                    <table>
                        <thead>
                            <tr>
                                <th>Rank</th>
                                <th>Player</th>
                                <th>Score</th>
                                <th>Wins</th>
                                <th>Losses</th>
                                <th>Win Rate</th>
                                <th>Monsters</th>
                            </tr>
                        </thead>
                        <tbody>
                            %s%s
                        </tbody>
                    </table>

                    <div class="actions">
                        <a href="/player/new">Create New Player</a>
                    </div>
                </fieldset>
                    <a href="/" class="back">&larr; Back to Home</a>
               
       
            </body>
            </html>
            """,
            dataStore.getLeaderboard().getTotalPlayers(),
            playersHtml.toString(),
            emptyMessage
        );

        ctx.html(html);
    }
}
