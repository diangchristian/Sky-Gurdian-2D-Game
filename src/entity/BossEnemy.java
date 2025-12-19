package entity;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class BossEnemy extends Enemy {

    private static BufferedImage[] images;
    private static BufferedImage[] bulletImages;
    // Shooting control
    private int attackCooldown = 0;
    private int attackDelay = 0;
    private int currentPattern = 0;

    private static final String[] BOSS_NAMES = {
            "XAR’VOTH THE CONQUEROR",
            "ZYLRAK THE VOID REAVER",
            "KRA’NEX THE STAR DEVOURER",
            "OMNIX-7, PLANET BREAKER",
            "ARCHIE PRIME"
    };

    private final GamePanel gp;
    private final ArrayList<BossBullet> bullets = new ArrayList<>();

    private int shootCooldown = 0;
    private int patternIndex = 0;
    private boolean ready = false;

    private final int difficultyLevel;
    public int variant;

    private final int BASE_HEALTH = 60;
    private int maxHealth;

    private int phase = 1;
    private String bossName;

    // ================= CONSTRUCTOR =================
    public BossEnemy(double x, double y, int variant, int difficultyLevel, GamePanel gp) {
        this.x = x;
        this.y = y;
        this.variant = variant;
        this.difficultyLevel = difficultyLevel;
        this.gp = gp;

        width = 200;
        height = 200;
        speed = 1 + difficultyLevel / 3;

        maxHealth = BASE_HEALTH + (difficultyLevel - 1) * 20;
        health = maxHealth;

        bossName = BOSS_NAMES[variant % BOSS_NAMES.length];

        loadImages();
        loadBulletImages();
    }

    // ================= UPDATE =================
    @Override
    public void update() {

        if (y < 50) {
            y += speed;
            return;
        } else {
            ready = true;
        }

        updatePhase();
        handleShooting();
        updateBullets();
    }

    // ================= PHASE =================
    private void updatePhase() {
        double hpPercent = (double) health / maxHealth;
        if (hpPercent > 0.7) phase = 1;
        else if (hpPercent > 0.4) phase = 2;
        else phase = 3;
    }

    // ================= SHOOTING =================
    private void handleShooting() {

        // Waiting after attack
        if (attackDelay > 0) {
            attackDelay--;
            return;
        }

        // Cooldown before next pattern
        if (attackCooldown > 0) {
            attackCooldown--;
            return;
        }

        // Decide allowed patterns based on difficulty
        int maxPattern = getMaxPatternForDifficulty();

        switch (currentPattern % maxPattern) {
            case 0 -> shootStraight();
            case 1 -> shootSpread();
            case 2 -> shootFanWave();
            case 3 -> shootDiagonalCross();
            case 4 -> shootCircle();
            case 5 -> shootRandomRapid();
        }

        gp.playSE(3);

        currentPattern++;

        // Cooldown BEFORE next pattern
        attackCooldown = getAttackCooldown();

        // Delay AFTER pattern (rest time)
        attackDelay = getAttackDelay();
    }

    private int getMaxPatternForDifficulty() {
        if (difficultyLevel <= 1) return 2; // straight, spread
        if (difficultyLevel == 2) return 3; // + fan
        if (difficultyLevel == 3) return 4; // + diagonal
        if (difficultyLevel == 4) return 5; // + circle
        return 6; // full chaos (late game)
    }

    private int getAttackCooldown() {
        // Time before next attack pattern
        int base = 120;
        base -= difficultyLevel * 15;
        return Math.max(base, 45);
    }

    private int getAttackDelay() {
        // Pause AFTER attack pattern
        int delay = 60;
        delay -= difficultyLevel * 5;
        return Math.max(delay, 20);
    }

    // ================= PATTERNS =================
    private void phaseOnePattern() {
        if (patternIndex++ % 2 == 0) shootStraight();
        else shootSpread();
    }

    private void phaseTwoPattern() {
        int p = patternIndex++ % 3;
        if (p == 0) shootSpread();
        else if (p == 1) shootFanWave();
        else shootDiagonalCross();
    }

    private void phaseThreePattern() {
        int p = patternIndex++ % 4;
        if (p == 0) shootCircle();
        else if (p == 1) shootFanWave();
        else if (p == 2) shootDiagonalCross();
        else shootRandomRapid();
    }

    // ================= ATTACKS =================
    private void shootStraight() {
        bullets.add(new BossBullet(x + width / 2.0 - 8, y + height, 0, 1, bulletImages[variant]));
    }

    private void shootSpread() {
        int count = 5 + difficultyLevel;
        double start = -50;
        double step = 100.0 / (count - 1);

        for (int i = 0; i < count; i++) {
            double a = Math.toRadians(start + step * i);
            bullets.add(new BossBullet(
                    x + width / 2.0 - 8,
                    y + height / 2.0,
                    Math.sin(a),
                    Math.cos(a),
                    bulletImages[variant]
            ));
        }
    }

    private void shootFanWave() {
        for (int i = -2; i <= 2; i++) {
            bullets.add(new BossBullet(
                    x + width / 2.0 - 8,
                    y + height / 2.0,
                    i * 0.4,
                    1,
                    bulletImages[variant]
            ));
        }
    }

    private void shootCircle() {
        int count = 12 + difficultyLevel * 2;
        for (int i = 0; i < count; i++) {
            double a = 2 * Math.PI * i / count;
            bullets.add(new BossBullet(
                    x + width / 2.0 - 8,
                    y + height / 2.0,
                    Math.cos(a),
                    Math.sin(a),
                    bulletImages[variant]
            ));
        }
    }

    private void shootDiagonalCross() {
        double[][] d = {{1,1},{-1,1},{1,-1},{-1,-1}};
        for (double[] dir : d) {
            bullets.add(new BossBullet(
                    x + width / 2.0 - 8,
                    y + height / 2.0,
                    dir[0], dir[1],
                    bulletImages[variant]
            ));
        }
    }

    private void shootRandomRapid() {
        for (int i = 0; i < 8 + difficultyLevel; i++) {
            bullets.add(new BossBullet(
                    x + width / 2.0 - 8,
                    y + height / 2.0,
                    Math.random() * 2 - 1,
                    Math.random() + 0.5,
                    bulletImages[variant]
            ));
        }
    }

    // ================= BULLETS =================
    private void updateBullets() {
        bullets.removeIf(b -> {
            b.update();
            return b.outOfBounds();
        });
    }

    // ================= DRAW =================
    @Override
    public void draw(Graphics2D g2) {
        g2.drawImage(images[variant], (int) x, (int) y, width, height, null);

        int barWidth = width;
        int barHeight = 14;
        int barX = (int) x;
        int barY = (int) y - barHeight - 10;

        // Name
        g2.setFont(gp.ui.orbitronBold.deriveFont(Font.BOLD, 14f));
        FontMetrics fm = g2.getFontMetrics();
        int nameX = barX + (barWidth - fm.stringWidth(bossName)) / 2;
        g2.setColor(Color.RED);
        g2.drawString(bossName, nameX, barY - 5);

        // Health bar
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(barX, barY, barWidth, barHeight);

        g2.setColor(Color.RED);
        g2.fillRect(barX, barY,
                (int) (barWidth * (double) health / maxHealth),
                barHeight
        );

        g2.setColor(Color.WHITE);
        g2.drawRect(barX, barY, barWidth, barHeight);

        bullets.forEach(b -> b.draw(g2));
    }

    public ArrayList<BossBullet> getBullets() { return bullets; }
    public boolean isReady() { return ready; }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height - 40);
    }

    // ================= ASSETS =================
    private void loadImages() {
        if (images != null) return;
        images = new BufferedImage[5];
        try {
            images[0] = ImageIO.read(getClass().getResource("/enemies/boss.png"));
            images[1] = ImageIO.read(getClass().getResource("/enemies/boss2.png"));
            images[2] = ImageIO.read(getClass().getResource("/enemies/boss3.png"));
            images[3] = ImageIO.read(getClass().getResource("/enemies/boss4.png"));
            images[4] = ImageIO.read(getClass().getResource("/enemies/archie.png"));
        } catch (Exception e) {
            for (int i = 0; i < images.length; i++)
                images[i] = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        }
    }

    private void loadBulletImages() {
        if (bulletImages != null) return;
        bulletImages = new BufferedImage[5];
        try {
            bulletImages[0] = ImageIO.read(getClass().getResource("/bullets/boss1.png"));
            bulletImages[1] = ImageIO.read(getClass().getResource("/bullets/boss2.png"));
            bulletImages[2] = ImageIO.read(getClass().getResource("/bullets/boss3.png"));
            bulletImages[3] = ImageIO.read(getClass().getResource("/bullets/boss4.png"));
            bulletImages[4] = ImageIO.read(getClass().getResource("/bullets/archie.png"));
        } catch (Exception e) {
            for (int i = 0; i < bulletImages.length; i++)
                bulletImages[i] = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        }
    }
}
