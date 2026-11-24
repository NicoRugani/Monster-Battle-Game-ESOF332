package web.checkpoint3;

import io.javalin.Javalin;
import model.*;
import util.checkpoints.Checkpoint3SampleData;

/**
 * Main web application for Checkpoint 3.
 * Tests Evolution and Leveling system implementations.
 * Runs on port 7003.
 */
public class Checkpoint3App {
    private final Checkpoint3DataStore dataStore;
    private final Checkpoint3HomeController homeController;
    private final Checkpoint3MonsterController monsterController;
    private final Checkpoint3BattleController battleController;
    private final Checkpoint3EvolutionController evolutionController;

    public Checkpoint3App() {
        // Initialize factory, managers, and data store
        IMonsterFactory monsterFactory = new MonsterFactory();
        IBattleManager battleManager = new BattleManager();
        IEvolutionManager evolutionManager = new EvolutionManager(monsterFactory);
        this.dataStore = new Checkpoint3DataStore(monsterFactory, battleManager, evolutionManager);

        // Initialize controllers
        this.homeController = new Checkpoint3HomeController(dataStore);
        this.monsterController = new Checkpoint3MonsterController(dataStore);
        this.battleController = new Checkpoint3BattleController(dataStore);
        this.evolutionController = new Checkpoint3EvolutionController(dataStore);

        // Generate sample data
        Checkpoint3SampleData sampleData = new Checkpoint3SampleData(dataStore);
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

        // Battle routes (from Checkpoint 2)
        app.get("/battle/new", battleController::showBattleSetup);
        app.post("/battle/start", battleController::startBattle);
        app.get("/battle/{battleId}", battleController::viewBattle);
        app.post("/battle/{battleId}/attack", battleController::executeAttack);

        // Evolution routes (new for Checkpoint 3)
        app.get("/progression", evolutionController::showProgression);
        app.get("/monster/{id}/evolution", evolutionController::showEvolutionPage);
        app.post("/monster/{id}/evolve", evolutionController::performEvolution);

        // Utility routes
        app.post("/clear", ctx -> {
            dataStore.clearAll();
            // Regenerate sample data
            Checkpoint3SampleData sampleData = new Checkpoint3SampleData(dataStore);
            sampleData.generateSampleData();
            ctx.redirect("/");
        });

        app.start(port);
    }

    public static void main(String[] args) {
        Checkpoint3App app = new Checkpoint3App();
        app.start(7003);
        System.out.println("Checkpoint 3 App (Evolution & Leveling) started at http://localhost:7003");
        System.out.println("Test your EvolutionManager and leveling system!");
    }
}
