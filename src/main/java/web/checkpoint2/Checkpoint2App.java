package web.checkpoint2;

import io.javalin.Javalin;
import model.*;
import util.checkpoints.Checkpoint2SampleData;

/**
 * Main web application for Checkpoint 2.
 * Tests Battle and BattleManager implementations.
 * Runs on port 7002.
 */
public class Checkpoint2App {
    private final Checkpoint2DataStore dataStore;
    private final Checkpoint2HomeController homeController;
    private final Checkpoint2MonsterController monsterController;
    private final Checkpoint2BattleController battleController;

    public Checkpoint2App() {
        // Initialize factory,  managers, and data store
        IMonsterFactory monsterFactory = new MonsterFactory();
        IBattleManager battleManager = new BattleManager();
        this.dataStore = new Checkpoint2DataStore(monsterFactory, battleManager);

        // Initialize controllers
        this.homeController = new Checkpoint2HomeController(dataStore);
        this.monsterController = new Checkpoint2MonsterController(dataStore);
        this.battleController = new Checkpoint2BattleController(dataStore);

        // Generate sample data
        Checkpoint2SampleData sampleData = new Checkpoint2SampleData(dataStore);
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

        // Home route
        app.get("/", homeController::showHome);

        // Monster routes (from Checkpoint 1)
        app.get("/monster/create", monsterController::showCreateForm);
        app.post("/monster/create-custom", monsterController::createCustomMonster);
        app.post("/monster/create-starter", monsterController::createStarterMonster);
        app.get("/monster/all", monsterController::listAllMonsters);
        app.get("/monster/{id}", monsterController::viewMonsterDetail);
        app.get("/encyclopedia", monsterController::showEncyclopedia);

        // Battle routes (new for Checkpoint 2)
        app.get("/battle/new", battleController::showBattleSetup);
        app.post("/battle/start", battleController::startBattle);
        app.get("/battle/{battleId}", battleController::viewBattle);
        app.post("/battle/{battleId}/attack", battleController::executeAttack);

        // Utility routes
        app.post("/clear", ctx -> {
            dataStore.clearAll();
            // Regenerate sample data
            Checkpoint2SampleData sampleData = new Checkpoint2SampleData(dataStore);
            sampleData.generateSampleData();
            ctx.redirect("/");
        });

        app.start(port);
    }

    public static void main(String[] args) {
        Checkpoint2App app = new Checkpoint2App();
        app.start(7002);
        System.out.println("Checkpoint 2 App (Combat System) started at http://localhost:7002");
        System.out.println("Test your Battle and BattleManager implementations!");
    }
}
