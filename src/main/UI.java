package main;

import entity.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class UI {
    private BufferedImage menuBg;

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

    // MENU
    public UIButton startButton;
    private float titleScale = 1.0f;
    private float scaleDir = 0.005f;

    // Fade transition
    private boolean isTransitioning = false;
    private boolean fadeOut = true;
    private int fadeAlpha = 0;
    private final int FADE_SPEED = 10;

    // TUTORIAL

    // TUTORIAL PANEL
    private String tutorialText = "";
    private String[] tutorialLines = null;
    private int tutorialTimer = 0;
    private final int TUTORIAL_PADDING = 20;
    private Font tutorialFont;
    private Color tutorialBgColor = new Color(0, 0, 0, 180); // semi-transparent black


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
    }


    private void drawLoading(Graphics2D g2) {
        drawCenteredText(g2, "Loading...", titleFont, Color.WHITE, gp.HEIGHT / 2);
    }

    private void drawMenu(Graphics2D g2) {
        g2.drawImage(menuBg, 0, 0, gp.WIDTH, gp.HEIGHT, null);

        // Game title
        drawCenteredTextBottom(
                g2,
                "Sky Guardian",
                titleFont,
                Color.WHITE,
                new Color(0, 0, 0, 0),
                140
        );


        // Start button
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


    // -------------------- UPDATE --------------------
    public void update() {
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
