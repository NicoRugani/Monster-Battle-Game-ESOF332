package model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of IPlayer interface.
 * Students will complete this implementation for Checkpoint 4.
 */
public class Player implements IPlayer {
    private final String id;
    private final String username;
    private final List<IMonster> monsters;
    private int wins;
    private int losses;

    public Player(String username) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.monsters = new ArrayList<>();
        this.wins = 0;
        this.losses = 0;
    }

    public Player(String id, String username, List<IMonster> monsters, int wins, int losses) {
        this.id = id;
        this.username = username;
        this.monsters = new ArrayList<>(monsters);
        this.wins = wins;
        this.losses = losses;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public List<IMonster> getMonsters() {
        return new ArrayList<>(this.monsters);

    }

    @Override
    public void addMonster(IMonster monster) {
        this.monsters.add(monster);
    }

    @Override
    public int getWins() {
        return this.wins;
    }

    @Override
    public int getLosses() {
        return this.losses;
    }

    @Override
    public void recordWin() {
        this.wins++;
    }

    @Override
    public void recordLoss() {
        this.losses++;
    }

    @Override
    public int getScore() {
        return this.wins - this.losses;
    }

    @Override
    public double getWinRate() {
        int totalBattles = this.wins + this.losses;

        if (totalBattles == 0) {
            return 0.0;
        }

        return (this.wins * 100.0) / totalBattles;
    }
}
