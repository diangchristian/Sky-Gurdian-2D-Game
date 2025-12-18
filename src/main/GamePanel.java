package main;

import entity.Player;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {

    // 🎮 Virtual game resolution (DO NOT CHANGE)
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

    // 💥 Explosion assets (RESTORED)
    public ExplosionAssets explosionAssets = new ExplosionAssets();

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

        // 💥 LOAD EXPLOSIONS
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
                    music.switchMusic(MENU_MUSIC);
                }
            }

            case MENU -> {
                // Menu logic if needed
            }

            case PLAYING -> {
                bg.update(HEIGHT);
                world.update();
            }

            case GAME_OVER -> {
                if (keyH.spaceBarPressed) {
                    resetGame();
                    keyH.spaceBarPressed = false;
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

        // Enable smooth scaling
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Calculate scale
        double scaleX = getWidth() / (double) WIDTH;
        double scaleY = getHeight() / (double) HEIGHT;
        double scale = Math.min(scaleX, scaleY); // keep aspect ratio

        // Center the game
        int xOffset = (int) ((getWidth() - WIDTH * scale) / 2);
        int yOffset = (int) ((getHeight() - HEIGHT * scale) / 2);

        g2.translate(xOffset, yOffset);
        g2.scale(scale, scale);

        // 🎮 Draw everything using original coordinates
        if (gameState == GameState.PLAYING || gameState == GameState.PAUSED) {
            bg.draw(g2, WIDTH, HEIGHT);
            world.draw(g2);
        }

        ui.draw(g2);

        if (player.isDead()) {
            ui.drawGameOver(g2);
        }

        g2.dispose();
    }


    public double getScale() {
        double scaleX = getWidth() / (double) WIDTH;
        double scaleY = getHeight() / (double) HEIGHT;
        return Math.min(scaleX, scaleY);
    }

    public int getMouseX(int rawX) {
        return (int) (rawX / getScale());
    }

    public int getMouseY(int rawY) {
        return (int) (rawY / getScale());
    }


    public int getOffsetX() {
        return (int) ((getWidth() - WIDTH * getScale()) / 2);
    }

    public int getOffsetY() {
        return (int) ((getHeight() - HEIGHT * getScale()) / 2);
    }

    public int screenToGameX(int screenX) {
        return (int) ((screenX - getOffsetX()) / getScale());
    }

    public int screenToGameY(int screenY) {
        return (int) ((screenY - getOffsetY()) / getScale());
    }


}
