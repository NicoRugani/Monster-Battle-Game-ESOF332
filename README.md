# Monster Battle Game

A Java turn-based monster battle game built as a semester-long project for **ESOF 322 (Software Engineering)** at Montana State University. Players collect monsters, battle opponents, level up their team through evolution, and compete on a global leaderboard.

This repo is my personal copy of the team project, pulled over to showcase the work — see **My Contribution** below for what I specifically built.

## Overview

The project was developed iteratively across four checkpoints, each building on the last:

- **Checkpoint 1 — Monster System:** Monster stats (HP, Attack, Defense, Speed) and elemental types (Fire, Water, Grass, Electric, Normal).
- **Checkpoint 2 — Battle System:** Turn-based combat with type effectiveness, damage calculation, and critical hits.
- **Checkpoint 3 — Evolution & Leveling:** Experience points, stat growth on level-up, and multi-stage monster evolution chains.
- **Checkpoint 4 — Players & Leaderboard:** Player accounts, win/loss tracking, score calculation, and global rankings, exposed through a REST web layer.

## Tech Stack

- **Java 21**
- **Maven** — build and dependency management
- **Javalin** — lightweight REST web framework
- **JUnit 5** — unit testing
- **GitHub Actions** — CI autograding on every push

## Architecture

The model layer is fully interface-driven (`IMonster`, `IBattle`, `IBattleManager`, `IPlayer`, `IEvolutionManager`, `IMonsterFactory`, `ILeaderboard`, `IAttackResult`), separating game logic from the web layer that exposes it.

## My Contribution

This was a 4-person team project (Git feature branches, pull-request review, CI-based autograding). My work focused on:

- **Model layer:** `Monster`, `AttackResult` (getters, messages, `toString()`), `EvolutionManager` (level retention and stat scaling on evolution), and `MonsterFactory` with its `IMonsterFactory` interface (factory-pattern monster creation).
- **Web layer (Checkpoint 4):** The REST controllers connecting the game logic to the web app — `Checkpoint4BattleController`, `Checkpoint4PlayerController`, `Checkpoint4MonsterController`, and `Checkpoint4LeaderboardController` — powering the battle, player, and leaderboard endpoints.

## Building and Running

```bash
# Compile
mvn clean compile

# Run all tests
mvn test

# Run a specific checkpoint's tests
mvn test -Dtest=MonsterSystemTest      # Checkpoint 1
mvn test -Dtest=CombatSystemTest       # Checkpoint 2
mvn test -Dtest=EvolutionSystemTest    # Checkpoint 3
mvn test -Dtest=LeaderboardSystemTest  # Checkpoint 4

# Run a checkpoint's web app
mvn compile exec:java -Dexec.mainClass="web.checkpoint4.Checkpoint4App"  # localhost:7004
```
