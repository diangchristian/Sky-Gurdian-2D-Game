package main;

import entity.*;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

public class GameWorld {

    private final GamePanel gp;
    private final Player player;

    private final ArrayList<Bullet> bullets = new ArrayList<>();
    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final ArrayList<Explosion> explosions = new ArrayList<>();
    private final ArrayList<Pickup> pickups = new ArrayList<>();
    private final Random random = new Random();
    public int currentMusic = -1;

    // Shooting
    private int shootCooldown = 0;
    private final int FIRE_RATE = 10;

    // Enemy spawning
    private boolean spawningEnabled = false;
    private int spawnDelayTimer = 120;
    private int spawnTimer = 90;
    private final int baseSpawnDelay = 90;
    private final int minSpawnDelay = 20;

    // Difficulty
    private int difficultyLevel = 1;
    private int scoreForNextDifficulty = 100;

    // Boss control
    private boolean bossActive = false;
    private boolean bossPreSpawn = false;
    private int bossPreSpawnTimer = 90;
    private final int postBossDelay = 120;
    private int scoreForNextBoss = 150;

    // Game start
    private boolean firstTimeStart = true;
    private int startReadyTimer = 90;

    public UI ui;
    public int score = 0;

    public GameWorld(GamePanel gp, Player player) {
        this.gp = gp;
        this.player = player;
        ui = new UI(gp, this);
    }

    // ========================= UPDATE =========================
    public void update() {

        // -------- GAME START (GET READY) --------
        if (firstTimeStart) {
            ui.setGetReadyText("GET READY");
            startReadyTimer--;

            if (startReadyTimer <= 0) {
                firstTimeStart = false;
                ui.setGetReadyText("");
                spawningEnabled = true;
                spawnTimer = getSpawnDelay();

                gp.music.switchMusic(gp.GAME_MUSIC);
            }
            return;
        }

        // -------- BOSS PRE-SPAWN --------
        if (bossPreSpawn) {
            bossPreSpawnTimer--;
            ui.setGetReadyText("GET READY");

            if (bossPreSpawnTimer == 60) {
                gp.music.fadeTo(gp.BOSS_MUSIC, 1000);

            }

            if (bossPreSpawnTimer <= 0) {
                spawnBossNow();
                bossPreSpawn = false;
                ui.setGetReadyText("");
            }
        }

        // -------- SPAWN DELAY AFTER BOSS --------
        if (!spawningEnabled && !bossActive && !bossPreSpawn) {
            spawnDelayTimer--;
            if (spawnDelayTimer <= 0) {
                spawningEnabled = true;
                spawnTimer = getSpawnDelay();
            }
        }

        // -------- PLAYER & BULLETS --------
        player.update();
        handleShooting();
        updateBullets();

        // -------- ENEMY SPAWN --------
        if (spawningEnabled && !bossActive && !bossPreSpawn) {
            spawnEnemies();
        }

        // -------- UPDATE ENEMIES --------
        updateEnemies();

        // -------- COLLISIONS --------
        checkCollisions();

        // -------- BOSS SPAWN CHECK --------
        spawnBossIfNeeded();

        updatePickups();

        // -------- EXPLOSIONS --------
        explosions.removeIf(exp -> {
            exp.update();
            return exp.isFinished();
        });
    }

    // ========================= DRAW =========================
    public void draw(Graphics2D g2) {
        player.draw(g2);

        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).draw(g2);
        }

        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).draw(g2);
        }

        for (int i = 0; i < explosions.size(); i++) {
            explosions.get(i).draw(g2);
        }

        for (int i = 0; i < pickups.size(); i++) {
            pickups.get(i).draw(g2);
        }


    }

    // ========================= SHOOTING =========================
    private void handleShooting() {
        boolean bossReady = true;
        for (Enemy e : enemies) {
            if (e instanceof BossEnemy boss) {
                bossReady = boss.isReady();
                break;
            }
        }

        if (gp.keyH.spaceBarPressed && shootCooldown == 0 && bossReady) {
            bullets.add(player.shoot());
            gp.playSE(1);
            shootCooldown = FIRE_RATE;
        }

        if (shootCooldown > 0) shootCooldown--;
    }

    // ========================= DIFFICULTY =========================
    private void updateDifficulty() {
        if (score >= scoreForNextDifficulty) {
            difficultyLevel++;
            scoreForNextDifficulty += difficultyLevel * 250;
        }
    }

    private int getMaxEnemies() {
        return 5 + difficultyLevel * 2;
    }

    private int getSpawnDelay() {
        int delay = baseSpawnDelay - difficultyLevel * 5;
        return Math.max(delay, minSpawnDelay);
    }

    // ========================= BULLETS =========================
    private void updateBullets() {
        bullets.removeIf(b -> {
            b.update();
            return b.y + b.height < 0;
        });
    }

    // ========================= ENEMIES =========================
    private void spawnEnemies() {
        if (enemies.size() >= getMaxEnemies()) return;

        updateDifficulty();

        if (--spawnTimer <= 0) {
            int x = random.nextInt(GamePanel.WIDTH - 50);
            int fastChance = Math.min(10 + difficultyLevel * 5, 70);

            if (random.nextInt(100) < fastChance) {
                enemies.add(new FastEnemy(x, -50));
            } else {
                enemies.add(new BasicEnemy(x, -50));
            }

            spawnTimer = getSpawnDelay();
        }
    }

    private void updateEnemies() {
        enemies.removeIf(e -> {
            e.update();
            return e.y > GamePanel.HEIGHT;
        });
    }

    // ========================= BOSS =========================
    private void spawnBossNow() {
        enemies.add(new BossEnemy(
                GamePanel.WIDTH / 2 - 100,
                -120,
                random.nextInt(4),
                gp
        ));
        bossActive = true;
        spawningEnabled = false;
    }

    private void spawnBossIfNeeded() {
        if (score >= scoreForNextBoss && !bossActive && !bossPreSpawn) {
            scoreForNextBoss += 200 + difficultyLevel * 2;
            bullets.clear();
            enemies.clear();
            bossPreSpawn = true;
            bossPreSpawnTimer = 90;
        }
    }

    private void onBossDefeated() {
        bossActive = false;
        spawningEnabled = false;
        spawnDelayTimer = postBossDelay;
        spawnTimer = getSpawnDelay();

        // 🎁 GUARANTEED BOSS DROP
        pickups.add(new HealthPickup(
                GamePanel.WIDTH / 2 - 16,
                GamePanel.HEIGHT / 2,
                gp
        ));

        gp.music.fadeTo(gp.GAME_MUSIC, 1000);

    }

    private void updatePickups() {
        pickups.removeIf(p -> {
            p.update();

            if (p.getBounds().intersects(player.getBounds())) {
                p.applyEffect(player);
                gp.playSE(9); // pickup sound
                return true;
            }

            return p.isOffScreen();
        });
    }



    // ========================= COLLISIONS =========================
    private void checkCollisions() {
        handleBulletEnemyCollision();
        handlePlayerEnemyCollision();
        handleBossBullets();
    }

    private void handleBulletEnemyCollision() {
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            for (int j = enemies.size() - 1; j >= 0; j--) {
                Enemy e = enemies.get(j);
                if (b.getBounds().intersects(e.getBounds())) {
                    bullets.remove(i);
                    e.takeDamage(1);
                    gp.playSE(4);

                    if (e.isDead()) {
                        if (e instanceof BossEnemy) {
                            explosions.add(new Explosion(e.x, e.y, e.width, e.height, ExplosionAssets.normalExplosion));
                            onBossDefeated();
                        } else {
                            explosions.add(new Explosion(e.x, e.y, e.width, e.height, ExplosionAssets.normalExplosion));
                        }

                        if (!(e instanceof BossEnemy)) {
                            int dropChance = 15; // %
                            if (random.nextInt(100) < dropChance) {
                                pickups.add(new HealthPickup((int) e.x, (int)e.y, gp));
                            }
                        }

                        enemies.remove(j);
                        score += 10;
                        gp.playSE(2);
                    }
                    break;
                }
            }
        }
    }

    private void handlePlayerEnemyCollision() {
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy e = enemies.get(i);
            if (player.getBounds().intersects(e.getBounds())) {
                enemies.remove(i);
                explosions.add(new Explosion(e.x, e.y, e.width, e.height, ExplosionAssets.normalExplosion));
                gp.playSE(5);
                player.takeDamage(10);
                if (player.isDead()) {
                    gp.gameOver();
                    return;
                }
            }
        }
    }

    private void handleBossBullets() {
        for (Enemy e : enemies) {
            if (e instanceof BossEnemy boss) {
                for (int i = 0; i < boss.getBullets().size(); i++) {
                    BossBullet b = boss.getBullets().get(i);
                    if (b.getBounds().intersects(player.getBounds())) {
                        player.takeDamage(10);
                        boss.getBullets().remove(i--);
                        if (player.isDead()) {
                            gp.gameOver();
                            return;
                        }
                    }
                }
            }
        }
    }

    // ========================= RESET =========================
    public void reset() {
        bullets.clear();
        enemies.clear();
        explosions.clear();
        pickups.clear();


        score = 0;
        difficultyLevel = 1;
        scoreForNextDifficulty = 100;
        scoreForNextBoss = 150;

        bossActive = false;
        bossPreSpawn = false;
        spawningEnabled = false;

        spawnTimer = baseSpawnDelay;
        spawnDelayTimer = 120;

        firstTimeStart = true;
        startReadyTimer = 90;

        player.setDefaultValues();

        gp.music.switchMusic(gp.GAME_MUSIC);

    }
}
