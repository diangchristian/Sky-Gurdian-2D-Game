package main;

import entity.Player;
import entity.Bullet;
import entity.Enemy;
import entity.BasicEnemy;
import entity.FastEnemy;
import entity.BossEnemy;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 700;

    private Thread gameThread;
    private final int FPS = 60;

    public GameState gameState = GameState.LOADING;

    public KeyHandler keyH = new KeyHandler();
    public MouseHandler mouseH = new MouseHandler(this);

    public final Player player = new Player(this, keyH);
    private final GameWorld world = new GameWorld(this, player);
    private final ScrollingBackground bg =
            new ScrollingBackground("/background/sky.png", HEIGHT, 4);

    public ExplosionAssets explosionAssets; // just a reference if needed

    public UI ui = world.ui;
    private int loadingTimer = 120;
    public Sound music = new Sound();
    public Sound se = new Sound();

    public static final int MENU_MUSIC = 0;
    public static final int GAME_MUSIC = 6;
    public static final int BOSS_MUSIC = 7;
    public static final int GAMEOVER_MUSIC = 8;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        addKeyListener(keyH);
        addMouseListener(mouseH);
        addMouseMotionListener(mouseH);
        explosionAssets.load();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        long drawInterval = 1_000_000_000 / FPS;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            long now = System.nanoTime();
            if (now - lastTime >= drawInterval) {
                update();
                repaint();
                lastTime = now;
            }
        }
    }

    private void update() {
        ui.update();

        switch (gameState) {
            case LOADING -> {
                if (--loadingTimer <= 0) {
                    gameState = GameState.MENU;

                    // 🔊 START MENU MUSIC HERE (ONCE)
                    music.switchMusic(MENU_MUSIC);
                }
            }

            case MENU -> {
                // nothing needed here
            }

            case PLAYING -> {
                // when starting the game


                bg.update(HEIGHT);
                world.update();
            }

            case GAME_OVER -> {
                // Check if SPACE is pressed to restart
                if (keyH.spaceBarPressed) {
                    resetGame();      // your existing reset method
                    keyH.spaceBarPressed = false; // prevent immediate re-trigger
                }
            }
        }
    }



    public void gameOver() {
        gameState = GameState.GAME_OVER;

        music.switchMusic(GAMEOVER_MUSIC);
    }

    public void resetGame() {
        world.reset();
        loadingTimer = 120;
        player.health = 100;
        gameState = GameState.PLAYING;
    }

    public void playSE(int i) {
        se.play(i);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Draw background and world for PLAYING and PAUSED states
        if (gameState == GameState.PLAYING || gameState == GameState.PAUSED) {
            bg.draw(g2, WIDTH, HEIGHT);  // draw background
            world.draw(g2);              // draw player, enemies, bullets
        }

        // Draw UI overlays (HUD, pause button, etc.)
        ui.draw(g2);

        // Draw GAME OVER overlay if player is dead
        if (player.isDead()) {
            ui.drawGameOver(g2);
        }

        g2.dispose();
    }



}
