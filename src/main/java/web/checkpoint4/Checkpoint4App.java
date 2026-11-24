package web.checkpoint4;

import io.javalin.Javalin;
import model.*;
import util.checkpoints.Checkpoint4SampleData;

/**
 * Main web application for Checkpoint 4.
 * Tests Player and Leaderboard implementations.
 * Runs on port 7004.
 */
public class Checkpoint4App {
    private final Checkpoint4DataStore dataStore;
    private final Checkpoint4HomeController homeController;
    private final Checkpoint4PlayerController playerController;
    private final Checkpoint4MonsterController monsterController;
    private final Checkpoint4BattleController battleController;
    private final Checkpoint4LeaderboardController leaderboardController;

    public Checkpoint4App() {
        // Initialize factory, managers, and data store
        IMonsterFactory monsterFactory = new MonsterFactory();
        IBattleManager battleManager = new BattleManager();
        IEvolutionManager evolutionManager = new EvolutionManager(monsterFactory);
        ILeaderboard leaderboard = new Leaderboard();
        this.dataStore = new Checkpoint4DataStore(monsterFactory, battleManager, evolutionManager, leaderboard);

        // Initialize controllers
        this.homeController = new Checkpoint4HomeController(dataStore);
        this.playerController = new Checkpoint4PlayerController(dataStore);
        this.monsterController = new Checkpoint4MonsterController(dataStore);
        this.battleController = new Checkpoint4BattleController(dataStore);
        this.leaderboardController = new Checkpoint4LeaderboardController(dataStore);

        // Generate sample data
        Checkpoint4SampleData sampleData = new Checkpoint4SampleData(dataStore);
        sampleData.generateSampleData();
    }

    public void start(int port) {
        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "/public";
            });
        });

        // Routes
        app.get("/", homeController::showHome);

        // Player routes
        app.get("/player/list", playerController::listPlayers);
        app.get("/player/new", playerController::showNewPlayerForm);
        app.post("/player/create", playerController::createPlayer);
        app.get("/player/{playerId}", playerController::showPlayerDashboard);
        app.post("/player/{playerId}/add-monster", playerController::addMonster);

        // Monster routes
        app.get("/monster/list", monsterController::listMonsters);
        app.get("/monster/{monsterId}/evolve", monsterController::evolveMonster);

        // Battle routes
        app.get("/battle/new", battleController::showNewBattleForm);
        app.post("/battle/start", battleController::startBattle);
        app.get("/battle/{battleId}", battleController::showBattle);
        app.post("/battle/{battleId}/attack", battleController::attack);
        app.post("/battle/{battleId}/runaway", battleController::runaway);

        // Leaderboard routes
        app.get("/leaderboard", leaderboardController::showLeaderboard);

        // Utility routes
        app.post("/clear", ctx -> {
            dataStore.clearAll();
            // Regenerate sample data
            Checkpoint4SampleData sampleData = new Checkpoint4SampleData(dataStore);
            sampleData.generateSampleData();
            ctx.redirect("/");
        });

        app.start(port);
    }

    public static void main(String[] args) {
        Checkpoint4App app = new Checkpoint4App();
        app.start(7004);
        System.out.println("Checkpoint 4 App (Players & Leaderboard) started at http://localhost:7004");
        System.out.println("Test your Player and Leaderboard implementations!");
    }
}
