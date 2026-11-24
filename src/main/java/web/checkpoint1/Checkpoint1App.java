package web.checkpoint1;

import io.javalin.Javalin;
import model.IMonsterFactory;
import model.MonsterFactory;
import util.checkpoints.Checkpoint1SampleData;

/**
 * Main web application for Checkpoint 1.
 * Tests Monster and MonsterFactory implementations.
 * Runs on port 7001.
 */
public class Checkpoint1App {
    private final Checkpoint1DataStore dataStore;
    private final Checkpoint1HomeController homeController;
    private final Checkpoint1MonsterController monsterController;

    public Checkpoint1App() {
        // Initialize factory and data store
        IMonsterFactory monsterFactory = new MonsterFactory();
        this.dataStore = new Checkpoint1DataStore(monsterFactory);

        // Initialize controllers
        this.homeController = new Checkpoint1HomeController(dataStore);
        this.monsterController = new Checkpoint1MonsterController(dataStore);

        // Generate sample data
        Checkpoint1SampleData sampleData = new Checkpoint1SampleData(dataStore);
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

        // Monster routes
        app.get("/monster/create", monsterController::showCreateForm);
        app.post("/monster/create-custom", monsterController::createCustomMonster);
        app.post("/monster/create-starter", monsterController::createStarterMonster);
        app.get("/monster/all", monsterController::listAllMonsters);
        app.get("/monster/{id}", monsterController::viewMonsterDetail);
        app.get("/encyclopedia", monsterController::showEncyclopedia);

        // Utility routes
        app.post("/clear", ctx -> {
            dataStore.clearAll();
            // Regenerate sample data
            Checkpoint1SampleData sampleData = new Checkpoint1SampleData(dataStore);
            sampleData.generateSampleData();
            ctx.redirect("/");
        });

        app.start(port);
    }

    public static void main(String[] args) {
        Checkpoint1App app = new Checkpoint1App();
        app.start(7001);
        System.out.println("Checkpoint 1 App (Monster System) started at http://localhost:7001");
        System.out.println("Test your Monster and MonsterFactory implementations!");
    }
}
