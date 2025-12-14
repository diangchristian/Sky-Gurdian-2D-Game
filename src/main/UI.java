package main;

import entity.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;


public class UI {
    private BufferedImage menuBg;
    private BufferedImage gameOverBg;
    private BufferedImage loadingBg;


    public BufferedImage pauseImg, resumeImg, exitImg;
    public UIButton pauseButton, resumeButton, exitButton;

    private GamePanel gp;
    private GameWorld gw;
    private Font orbitronRegular;
    private Font orbitronBold;

    private Font titleFont;
    private Font normalFont;
    private Font buttonFont;
    // PAUSE MENU
    public boolean isPaused = false;

    public UIButton startButton;
    private float titleScale = 1.0f;
    private float scaleDir = 0.005f;

    // Fade transition
    private boolean isTransitioning = false;
    private boolean fadeOut = true;
    private int fadeAlpha = 0;
    private final int FADE_SPEED = 10;
    private String getReadyText = "";

    public UI(GamePanel gp, GameWorld gw) {
        this.gp = gp;
        this.gw = gw;

        loadImages();
        loadFonts();
        loadButtonImages();   // <<< ADD THIS

        titleFont = orbitronBold;
        normalFont = orbitronRegular;
        buttonFont = orbitronBold.deriveFont(28f);

        startButton = new UIButton(
                gp.WIDTH / 2 - 120,
                gp.HEIGHT - 100,
                250,
                50,
                "START GAME",
                buttonFont
        );
    }


    private void loadImages() {
        try {
            menuBg = ImageIO.read(getClass().getResourceAsStream("/background/mainbackground.png"));
//            gameOverBg = ImageIO.read(getClass().getResourceAsStream("/background/gameover.png"));
//            loadingBg = ImageIO.read(getClass().getResourceAsStream("/background/loading.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFonts() {
        try {
            orbitronRegular = Font.createFont(
                    Font.TRUETYPE_FONT,
                    getClass().getResourceAsStream("/fonts/Orbitron-Regular.ttf")
            ).deriveFont(22f);

            orbitronBold = Font.createFont(
                    Font.TRUETYPE_FONT,
                    getClass().getResourceAsStream("/fonts/Orbitron-Bold.ttf")
            ).deriveFont(Font.BOLD, 48f);

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(orbitronRegular);
            ge.registerFont(orbitronBold);

        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            // fallback
            orbitronRegular = new Font("Arial", Font.BOLD, 22);
            orbitronBold = new Font("Arial", Font.BOLD, 48);
        }
    }


    private void loadButtonImages() {
        try {
            pauseImg = ImageIO.read(getClass().getResourceAsStream("/ui/pause.png"));
            resumeImg = ImageIO.read(getClass().getResourceAsStream("/ui/resume.png"));
            exitImg = ImageIO.read(getClass().getResourceAsStream("/ui/exit.png"));
        } catch (IOException e) {
            e.printStackTrace();
            pauseImg = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
            resumeImg = new BufferedImage(120, 50, BufferedImage.TYPE_INT_ARGB);
            exitImg = new BufferedImage(120, 50, BufferedImage.TYPE_INT_ARGB);
        }

        // Button positions
        pauseButton = new UIButton(700, 20, 50, 50, "", null);  // top-right corner
        resumeButton = new UIButton(gp.WIDTH / 2 - 60, gp.HEIGHT / 2 - 50, 100, 100, "", null);
        exitButton = new UIButton(gp.WIDTH / 2 - 60, gp.HEIGHT / 2 + 50, 100, 100, "", null);
    }



    public void draw(Graphics2D g2) {
        switch (gp.gameState) {
            case LOADING -> drawLoading(g2);
            case MENU -> drawMenu(g2);
            case PLAYING ->
                    {
                        drawHUD(g2);
                        g2.drawImage(pauseImg, pauseButton.x, pauseButton.y, pauseButton.width, pauseButton.height, null);
                    }
            case GAME_OVER -> drawGameOver(g2);
            case PAUSED -> drawPauseOverlay(g2);

        }

        // ALWAYS draw GET READY on top
                drawGetReady(g2);

        // Fade should be absolute top
                drawFade(g2);


    }

    /* ================= SCREENS ================= */

    private void drawLoading(Graphics2D g2) {
        drawCenteredText(g2, "Loading...", titleFont, Color.WHITE, gp.HEIGHT / 2);
    }

    private void drawMenu(Graphics2D g2) {
        g2.drawImage(menuBg, 0, 0, gp.WIDTH, gp.HEIGHT, null);

        drawCenteredTextBottom(
                g2,
                "Sky Gurdian",
                titleFont,
                Color.WHITE,
                new Color(0, 0, 0, 0),
                140   // padding from bottom
        );

        startButton = new UIButton(
                gp.WIDTH / 2 - 120,
                gp.HEIGHT - 100,
                250,
                50,
                "START GAME",
                buttonFont
        );


        startButton.draw(g2);

    }

    private void drawGetReady(Graphics2D g2) {
        if (getReadyText.isEmpty()) return;

        g2.setFont(titleFont.deriveFont(40f));
        FontMetrics fm = g2.getFontMetrics();

        int x = (gp.WIDTH - fm.stringWidth(getReadyText)) / 2;
        int y = gp.HEIGHT / 2;

        g2.setColor(new Color(0, 0, 0, 180));
        g2.drawString(getReadyText, x + 3, y + 3);

        g2.setColor(Color.WHITE);
        g2.drawString(getReadyText, x, y);
    }

    public void drawPauseOverlay(Graphics2D g2) {
        Composite original = g2.getComposite();

        // semi-transparent background
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.WIDTH, gp.HEIGHT);
        g2.setComposite(original);

        // Center PAUSED text
        drawCenteredText(g2, "PAUSED", titleFont, Color.WHITE, gp.HEIGHT / 2 - 100);

        // Draw resume and exit images
        g2.drawImage(resumeImg, resumeButton.x, resumeButton.y, resumeButton.width, resumeButton.height, null);
        g2.drawImage(exitImg, exitButton.x, exitButton.y, exitButton.width, exitButton.height, null);

//        // Optional hover effect
//        Point mouse = gp.getMousePosition();
//        if (mouse != null) {
//            if (resumeButton.contains(mouse.x, mouse.y)) drawHoverBorder(g2, resumeButton);
//            if (exitButton.contains(mouse.x, mouse.y)) drawHoverBorder(g2, exitButton);
//        }
    }

//    private void drawHoverBorder(Graphics2D g2, UIButton btn) {
//        g2.setColor(Color.YELLOW);
//        g2.setStroke(new BasicStroke(3));
//        g2.drawOval(btn.x, btn.y, btn.width - 20, btn.height - 20);
//    }


    public void drawGameOver(Graphics2D g2) {
        // Save original composite
        Composite original = g2.getComposite();

        // -------- Draw semi-transparent overlay --------
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f)); // 60% opacity
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.WIDTH, gp.HEIGHT);

        // -------- Restore full opacity for text --------
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        // Draw GAME OVER text
        drawCenteredText(g2, "GAME OVER", titleFont, Color.RED, gp.HEIGHT / 2 - 40);
        drawCenteredText(g2, "Score: " + gw.score, normalFont, Color.WHITE, gp.HEIGHT / 2 + 10);
        drawCenteredText(g2, "Press SPACE to Restart", normalFont, Color.LIGHT_GRAY, gp.HEIGHT / 2 + 50);

        // -------- Restore original composite --------
        g2.setComposite(original);
    }



    /* ================= HUD ================= */

    private void drawHUD(Graphics2D g2) {
        Player player = gp.player;

        // SCORE
        g2.setFont(normalFont);
        g2.setColor(Color.WHITE);
        g2.drawString("Score: " + gw.score, 20, 40);

        // HEALTH BAR BACKGROUND
        g2.setColor(Color.GRAY);
        g2.fillRect(20, 60, 200, 20);

        // HEALTH PERCENTAGE
        double healthPercent = player.health / 100.0;

        // CHANGE COLOR BASED ON HEALTH
        if (healthPercent > 0.6) {
            g2.setColor(Color.GREEN);      // High health
        } else if (healthPercent > 0.3) {
            g2.setColor(Color.ORANGE);     // Mid health
        } else {
            g2.setColor(Color.RED);        // Low health
        }

        // HEALTH BAR FILL
        int healthWidth = (int) (200 * healthPercent);
        g2.fillRect(20, 60, healthWidth, 20);

        // BORDER
        g2.setColor(Color.WHITE);
        g2.drawRect(20, 60, 200, 20);
    }



    public void setGetReadyText(String text) {
        getReadyText = text;
    }



    /* ================= UTIL ================= */

    public void drawCenteredText(Graphics2D g2, String text, Font font, Color color, int y) {
        g2.setFont(font);
        g2.setColor(color);
        int textWidth = g2.getFontMetrics().stringWidth(text);
        int x = (gp.WIDTH - textWidth) / 2;
        g2.drawString(text, x, y);
    }

    public void startFadeToGame() {
        isTransitioning = true;
        fadeOut = true;
        fadeAlpha = 0;
    }

    private void drawFade(Graphics2D g2) {
        if (!isTransitioning) return;

        g2.setColor(new Color(0, 0, 0, fadeAlpha));
        g2.fillRect(0, 0, gp.WIDTH, gp.HEIGHT);
    }

    public void update() {
        if (gp.gameState == GameState.MENU) {
            // Animate title scale for a pulse/breathing effect
            titleScale += scaleDir;
            if (titleScale > 1.05f || titleScale < 0.95f) {
                scaleDir *= -1; // Reverse direction
            }
        }

        if (isTransitioning) {
            if (fadeOut) {
                fadeAlpha += FADE_SPEED;
                if (fadeAlpha >= 255) {
                    fadeAlpha = 255;
                    fadeOut = false;
                    gp.gameState = GameState.PLAYING;
                }
            } else {
                fadeAlpha -= FADE_SPEED;
                if (fadeAlpha <= 0) {
                    fadeAlpha = 0;
                    isTransitioning = false;
                }
            }
        }


    }

    private void drawCenteredTextBottom(
            Graphics2D g2,
            String text,
            Font font,
            Color textColor,
            Color shadowColor,
            int bottomPadding
    ) {
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();

        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();

        int x = (gp.WIDTH - textWidth) / 2;

        // IMPORTANT PART 👇
        int y = gp.HEIGHT - bottomPadding;

        // Shadow
        g2.setColor(shadowColor);
        g2.drawString(text, x + 3, y + 3);

        // Main text
        g2.setColor(textColor);
        g2.drawString(text, x, y);
    }


}
