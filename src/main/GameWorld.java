    package main;

    import entity.*;
    import java.awt.Graphics2D;
    import java.util.ArrayList;
    import java.util.Random;
    import entity.BulletSpeedPickup;

    public class GameWorld {

        private final GamePanel gp;
        private final Player player;

        private final ArrayList<Bullet> bullets = new ArrayList<>();
        private final ArrayList<Enemy> enemies = new ArrayList<>();
        private final ArrayList<Explosion> explosions = new ArrayList<>();
        private final ArrayList<Pickup> pickups = new ArrayList<>();
        private final ArrayList<PickupText> pickupTexts = new ArrayList<>();

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
        private int scoreForNextBoss = 300;

        // Game start sequence
        private StartPhase startPhase = StartPhase.TUTORIAL;
        private int tutorialTimer = 240; // 4 seconds
        private int getReadyTimer = 60;   // 1 second

        public UI ui;
        public int score = 0;
        public int earnedScore = 0;

        public GameWorld(GamePanel gp, Player player) {
            this.gp = gp;
            this.player = player;
            ui = new UI(gp, this);
        }

        // ========================= UPDATE =========================
        public void update() {
            ui.update();
            ui.updateBossPulse(bossActive);
            // -------- GAME START FLOW --------
            if (startPhase != StartPhase.DONE) {

                // ----- TUTORIAL FIRST -----
                if (startPhase == StartPhase.TUTORIAL) {
                    ui.setTutorialText(
                            "Move: WASD / Arrow Keys\n" +
                                    "Shoot: SPACE\n" +
                                    "Avoid enemies and survive!"
                    );

                    tutorialTimer--;
                    if (tutorialTimer <= 0) {
                        startPhase = StartPhase.GET_READY;
                        ui.clearTutorial();
                    }
                    return;
                }



                // ----- GET READY SECOND -----
                if (startPhase == StartPhase.GET_READY) {
                    ui.setGetReadyText("GET READY");

                    getReadyTimer--;
                    if (getReadyTimer <= 0) {
                        ui.setGetReadyText("");
                        startPhase = StartPhase.DONE;

                        spawningEnabled = true;
                        spawnTimer = getSpawnDelay();
                        gp.music.switchMusic(gp.GAME_MUSIC);
                    }
                    return;
                }
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

            pickupTexts.removeIf(t -> {
                t.update();
                return t.isFinished();
            });
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

            for (int i = 0; i < pickupTexts.size(); i++) {
                pickupTexts.get(i).draw(g2);
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
                difficultyLevel += 1;
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

                int fastChance   = Math.min(10 + difficultyLevel * 5, 70);
                int sniperChance = Math.min(5 + difficultyLevel * 3, 25);

                int roll = random.nextInt(100);


                if (roll < sniperChance) {
                    enemies.add(new SniperEnemy(x, -50, gp, player));

                } else if (roll < sniperChance + fastChance) {
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
                    difficultyLevel,   // ✅ PASS CURRENT DIFFICULTY
                    gp
            ));

            bossActive = true;
            spawningEnabled = false;
        }


        private void spawnBossIfNeeded() {
            if (score >= scoreForNextBoss && !bossActive && !bossPreSpawn) {
                scoreForNextBoss += 400 + difficultyLevel * 2;
                bullets.clear();
                enemies.clear();
                bossPreSpawn = true;
                bossPreSpawnTimer = 90;
            }
        }

        private void onBossDefeated() {
            bossActive = false;
            ui.updateBossPulse(false); // reset pulse
            spawningEnabled = false;
            spawnDelayTimer = postBossDelay;
            spawnTimer = getSpawnDelay();

            // 🎁 GUARANTEED BOSS DROP
            pickups.add(new HealthPickup(
                    GamePanel.WIDTH / 2 - 16,
                    GamePanel.HEIGHT / 2,
                    gp
            ));

            pickups.add(new BulletSpeedPickup(
                    GamePanel.WIDTH / 2 - 70,
                    GamePanel.HEIGHT / 2
            ));

            gp.music.fadeTo(gp.GAME_MUSIC, 1000);
        }

        private void updatePickups() {
            pickups.removeIf(p -> {
                p.update();

                if (p.getBounds().intersects(player.getBounds())) {
                    p.applyEffect(player);
                    gp.playSE(9); // pickup sound

                    pickupTexts.add(new PickupText(
                            p.getEffectText(),
                            p.getX() + p.getWidth() / 2 - 16,
                            p.getY(),
                            ui.orbitronRegular.deriveFont(22f)
                    ));

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
            handleSniperBullets();
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


                            explosions.add(new Explosion(
                                    e.x, e.y, e.width, e.height,
                                    ExplosionAssets.normalExplosion
                            ));


                            if (e instanceof SniperEnemy sniper) {
                                sniper.getBullets().clear();
                            }


                            if (e instanceof BossEnemy) {
                                onBossDefeated();
                            } else {

                                int roll = random.nextInt(100);

                                if (roll < 6) {
                                    pickups.add(new ShieldPickup((int) e.x, (int) e.y));
                                } else if (roll < 10) {
                                    pickups.add(new BulletSpeedPickup((int) e.x, (int) e.y));
                                } else if (roll < 18) {
                                    pickups.add(new HealthPickup((int) e.x, (int) e.y, gp));
                                }
                            }


                            if (e instanceof FastEnemy) {
                                score += 20;
                                earnedScore = 20;
                            } else if (e instanceof SniperEnemy) {
                                score += 25;
                                earnedScore = 25;
                            } else if (e instanceof BasicEnemy) {
                                score += 10;
                                earnedScore = 10;
                            } else if (e instanceof BossEnemy){
                                score += 70;
                                earnedScore = 70;
                            }

                            if (earnedScore > 0) {
                                pickupTexts.add(new PickupText(
                                        "+" + earnedScore,
                                        (int) e.x + e.width / 2,
                                        (int) e.y,
                                        ui.orbitronBold.deriveFont( 20f)
                                ));
                            }

                            enemies.remove(j);
                            gp.playSE(2);
                        }
                        break;
                    }
                }
            }
        }


        private void handleSniperBullets() {
            for (Enemy e : enemies) {
                if (e instanceof SniperEnemy sniper) {
                    for (int i = sniper.getBullets().size() - 1; i >= 0; i--) {
                        EnemyBullet b = sniper.getBullets().get(i);

                        if (b.getBounds().intersects(player.getBounds())) {
                            player.takeDamage(11); // sniper hits harder
                            sniper.getBullets().remove(i);

                            gp.playSE(4); // hit sound (optional)

                            if (player.isDead()) {
                                gp.gameOver();
                                return;
                            }
                        }
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
                    player.takeDamage(6);
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

            startPhase = StartPhase.TUTORIAL;
            tutorialTimer = 240;
            getReadyTimer = 60;

            player.setDefaultValues();

            gp.music.switchMusic(gp.GAME_MUSIC);
        }

        // ========================= START PHASE ENUM =========================
        private enum StartPhase {
            TUTORIAL, GET_READY, DONE
        }
    }
