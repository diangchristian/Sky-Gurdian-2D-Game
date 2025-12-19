package main;

import entity.Player;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class UI {
    private BufferedImage menuBg;

    public BufferedImage pauseImg, resumeImg, exitImg;
    public UIButton pauseButton, resumeButton, exitButton;

    private GamePanel gp;
    private GameWorld gw;
    public Font orbitronRegular;
    public Font orbitronBold;

    private float glowAlpha = 0.6f;
    private float glowDir = 0.01f;
    private float bossPulseAlpha = 0f;
    private boolean bossPulseIncreasing = true;

    private Font titleFont;
    private Font normalFont;
    private Font buttonFont;

    // PAUSE MENU
    public boolean isPaused = false;

    // MENU
    public UIButton startButton;
    private float titleScale = 1.0f;
    private float scaleDir = 0.005f;
    int titleY = 140;
    // Fade transition
    private boolean isTransitioning = false;
    private boolean fadeOut = true;
    private int fadeAlpha = 0;
    private final int FADE_SPEED = 10;

//    private Color aboutBgColor = new Color(20, 30, 40, 160);

    // TUTORIAL

    // TUTORIAL PANEL
    private String tutorialText = "";
    private String[] tutorialLines = null;
    private int tutorialTimer = 0;
    private final int TUTORIAL_PADDING = 20;
    private Font tutorialFont;
    private Color tutorialBgColor = new Color(0, 0, 0, 180); // semi-transparent black

    // ABOUT GAME
    private String aboutText = "Sky Guardian is an arcade-style shooting game.\n" +
            "Survive waves of enemies and defeat bosses.\n" +
            "Collect pickups to upgrade your plane and stay alive!\n" +
            "Use WASD / Arrow Keys to move, SPACE to shoot.";
    private String[] aboutLines = null;
    private final int ABOUT_PADDING = 20;
    private Color aboutBgColor = new Color(0, 0, 0, 180); // semi-transparent black


    // GET READY & Tutorial
    private String getReadyText = "";

    public UI(GamePanel gp, GameWorld gw) {
        this.gp = gp;
        this.gw = gw;

        loadImages();
        loadFonts();
        loadButtonImages();

        titleFont = orbitronBold;
        normalFont = orbitronRegular;
        buttonFont = orbitronBold.deriveFont(28f);
        prepareAboutText();

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
            tutorialFont = orbitronRegular.deriveFont(22f);


        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
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
            resumeImg = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
            exitImg = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        }

        pauseButton = new UIButton(700, 20, 50, 50, "", null);
        resumeButton = new UIButton(gp.WIDTH / 2 - 50, gp.HEIGHT / 2 - 50, 100, 100, "", null);
        exitButton = new UIButton(gp.WIDTH / 2 - 50, gp.HEIGHT / 2 + 50, 100, 100, "", null);
    }

    private void prepareAboutText() {
        aboutLines = aboutText.split("\n");
    }

    // -------------------- DRAW --------------------
    public void draw(Graphics2D g2) {
        switch (gp.gameState) {
            case LOADING -> drawLoading(g2);
            case MENU -> drawMenu(g2);
            case PLAYING -> {
                drawHUD(g2);
                g2.drawImage(pauseImg, pauseButton.x, pauseButton.y, pauseButton.width, pauseButton.height, null);
            }
            case GAME_OVER -> drawGameOver(g2);
            case PAUSED -> drawPauseOverlay(g2);
        }
        drawTutorial(g2);
        drawGetReady(g2);
        drawFade(g2);
        drawBossPulseTop(g2);

//        drawBossPulseCorners(g2);
    }


    private void drawLoading(Graphics2D g2) {
        drawCenteredText(g2, "Loading...", titleFont, Color.WHITE, gp.HEIGHT / 2);
    }

    private void drawMenu(Graphics2D g2) {
        // Draw background
        g2.drawImage(menuBg, 0, 0, gp.WIDTH, gp.HEIGHT, null);

        // ---------------- Title ----------------
        int titleY = 140;
        g2.setFont(titleFont);

        FontMetrics fm = g2.getFontMetrics();
        int titleX = (gp.WIDTH - fm.stringWidth("Sky Guardian")) / 2;

// Glow
        g2.setColor(new Color(100, 200, 255, (int)(255 * glowAlpha)));
        g2.drawString("Sky Guardian", titleX + 2, titleY + 2);

// Main text
        g2.setColor(Color.WHITE);
        g2.drawString("Sky Guardian", titleX, titleY);


        // ---------------- About Panel ----------------
        drawAboutPanel(g2, titleY + 60); // start a bit below the title

        // ---------------- Start Button ----------------
        startButton.y = gp.HEIGHT - 100; // fixed position from bottom
        startButton.draw(g2);
        drawVignette(g2);
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

    public void drawTutorial(Graphics2D g2) {
        if (tutorialLines == null || tutorialLines.length == 0) return;

        g2.setFont(tutorialFont);

        // calculate panel size
        int panelWidth = 0;
        int lineHeight = g2.getFontMetrics().getHeight();
        for (String line : tutorialLines) {
            panelWidth = Math.max(panelWidth, g2.getFontMetrics().stringWidth(line));
        }
        panelWidth += TUTORIAL_PADDING * 2;
        int panelHeight = lineHeight * tutorialLines.length + TUTORIAL_PADDING * 2;

        int panelX = (gp.WIDTH - panelWidth) / 2;
        int panelY =  (gp.HEIGHT - panelHeight) / 2 - 200; // place near top

        // draw semi-transparent background
        g2.setColor(tutorialBgColor);
        g2.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);

        // draw each line
        g2.setColor(Color.WHITE);
        int textY = panelY + TUTORIAL_PADDING + lineHeight - 4;
        for (String line : tutorialLines) {
            g2.drawString(line, panelX + TUTORIAL_PADDING, textY);
            textY += lineHeight;
        }
    }




    private void drawAboutPanel(Graphics2D g2, int panelStartY) {
        if (aboutLines == null || aboutLines.length == 0) return;

        g2.setFont(tutorialFont);

        // calculate panel width
        int panelWidth = 0;
        int lineHeight = g2.getFontMetrics().getHeight();
        for (String line : aboutLines) {
            panelWidth = Math.max(panelWidth, g2.getFontMetrics().stringWidth(line));
        }
        panelWidth += ABOUT_PADDING * 2;
        int panelHeight = lineHeight * aboutLines.length + ABOUT_PADDING * 2;

        int panelX = (gp.WIDTH - panelWidth) / 2;
        int panelY = panelStartY;

        // draw semi-transparent background
        g2.setColor(aboutBgColor);
        g2.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);

        // draw each line
        g2.setColor(Color.WHITE);
        int textY = panelY + ABOUT_PADDING + lineHeight - 4;
        for (String line : aboutLines) {
            g2.drawString(line, panelX + ABOUT_PADDING, textY);
            textY += lineHeight;
        }
    }





    public void drawPauseOverlay(Graphics2D g2) {
        Composite original = g2.getComposite();

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.WIDTH, gp.HEIGHT);
        g2.setComposite(original);

        drawCenteredText(g2, "PAUSED", titleFont, Color.WHITE, gp.HEIGHT / 2 - 100);

        g2.drawImage(resumeImg, resumeButton.x, resumeButton.y, resumeButton.width, resumeButton.height, null);
        g2.drawImage(exitImg, exitButton.x, exitButton.y, exitButton.width, exitButton.height, null);
    }

    public void drawGameOver(Graphics2D g2) {
        Composite original = g2.getComposite();

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.WIDTH, gp.HEIGHT);
        g2.setComposite(original);

        drawCenteredText(g2, "GAME OVER", titleFont, Color.RED, gp.HEIGHT / 2 - 40);
        drawCenteredText(g2, "Score: " + gw.score, normalFont, Color.WHITE, gp.HEIGHT / 2 + 10);
        drawCenteredText(g2, "Press SPACE to Restart", normalFont, Color.LIGHT_GRAY, gp.HEIGHT / 2 + 50);
    }

    private void drawHUD(Graphics2D g2) {
        Player player = gp.player;

        // Score
        g2.setFont(normalFont);
        g2.setColor(Color.WHITE);
        g2.drawString("Score: " + gw.score, 20, 40);

        // Health bar
        g2.setColor(Color.GRAY);
        g2.fillRect(20, 60, 200, 20);

        double healthPercent = player.health / 100.0;

        if (healthPercent > 0.6) g2.setColor(Color.GREEN);
        else if (healthPercent > 0.3) g2.setColor(Color.ORANGE);
        else g2.setColor(Color.RED);

        int healthWidth = (int) (200 * healthPercent);
        g2.fillRect(20, 60, healthWidth, 20);

        g2.setColor(Color.WHITE);
        g2.drawRect(20, 60, 200, 20);
    }

    public void setGetReadyText(String text) {
        getReadyText = text;
    }

    public void setTutorialText(String text) {
        if (text == null || text.isEmpty()) return;

        tutorialText = text;
        tutorialLines = tutorialText.split("\n"); // split lines
    }

    private float bossPulseTime = 0f;

    public void updateBossPulse(boolean bossActive) {
        if (!bossActive) {
            bossPulseAlpha = 0f;
            bossPulseTime = 0f;
            return;
        }

        bossPulseTime += 0.05f; // adjust speed if needed
        bossPulseAlpha = 0.05f + 0.2f * (float)(Math.sin(bossPulseTime) * 0.5 + 0.5);
        // alpha smoothly oscillates between 0.05 and 0.25
    }


    private void drawBossPulseTop(Graphics2D g2) {
        if (bossPulseAlpha <= 0) return;

        int width = gp.WIDTH;
        int height = gp.HEIGHT;

        // Center slightly above screen for top glow
        Point2D center = new Point2D.Float(width / 2f, -height * 0.25f);

        // Radius large enough to cover top of screen and fade naturally
        float radius = width * 0.9f;

        // Smooth gradient: red at center, transparent at edges
        float[] dist = {0f, 1f};
        Color[] colors = {
                new Color(255, 0, 0, (int)(bossPulseAlpha * 255)),
                new Color(255, 0, 0, 0)
        };

        RadialGradientPaint gradient = new RadialGradientPaint(center, radius, dist, colors,
                MultipleGradientPaint.CycleMethod.NO_CYCLE);

        Paint old = g2.getPaint();
        g2.setPaint(gradient);

        // Fill the whole screen; gradient will fade naturally
        g2.fillRect(0, 0, width, height);

        g2.setPaint(old);
    }







    // -------------------- UPDATE --------------------
    public void update() {

        glowAlpha += glowDir;
        if (glowAlpha > 0.9f || glowAlpha < 0.4f) glowDir *= -1;



        if (gp.gameState == GameState.MENU) {
            titleScale += scaleDir;
            if (titleScale > 1.05f || titleScale < 0.95f) scaleDir *= -1;
        }

        if (tutorialTimer > 0) {
            tutorialTimer--;
            if (tutorialTimer <= 0) clearTutorial();
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

    private void drawVignette(Graphics2D g2) {
        RadialGradientPaint vignette = new RadialGradientPaint(
                new Point(gp.WIDTH / 2, gp.HEIGHT / 2),
                gp.WIDTH,
                new float[]{0.6f, 1f},
                new Color[]{
                        new Color(0, 0, 0, 0),
                        new Color(0, 0, 0, 180)
                }
        );

        Paint old = g2.getPaint();
        g2.setPaint(vignette);
        g2.fillRect(0, 0, gp.WIDTH, gp.HEIGHT);
        g2.setPaint(old);
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

    public void drawCenteredText(Graphics2D g2, String text, Font font, Color color, int y) {
        g2.setFont(font);
        g2.setColor(color);
        int textWidth = g2.getFontMetrics().stringWidth(text);
        int x = (gp.WIDTH - textWidth) / 2;
        g2.drawString(text, x, y);
    }

    private void drawCenteredTextBottom(Graphics2D g2, String text, Font font, Color textColor, Color shadowColor, int bottomPadding) {
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        int x = (gp.WIDTH - fm.stringWidth(text)) / 2;
        int y = gp.HEIGHT - bottomPadding;
        g2.setColor(shadowColor);
        g2.drawString(text, x + 3, y + 3);
        g2.setColor(textColor);
        g2.drawString(text, x, y);
    }

    // ================= TUTORIAL =================
    public void clearTutorial() {
        tutorialText = "";
        tutorialLines = null;
        tutorialTimer = 0;
    }

}
