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

    private GamePanel gp;
    private final ArrayList<BossBullet> bullets = new ArrayList<>();
    private int shootCooldown = 0;
    private int patternIndex = 0;
    private boolean ready = false;
    public int variant;
    private int difficultyLevel;  // new

    private final int BASE_HEALTH = 50;
    private final int FIRE_RATE = 60;

    // Constructor with difficulty
    public BossEnemy(double x, double y, int variant, int difficultyLevel, GamePanel gp) {
        this.x = x;
        this.y = y;
        this.variant = variant;
        this.difficultyLevel = difficultyLevel;
        this.gp = gp;

        width = 200;
        height = 200;
        speed = 1;

        this.health = BASE_HEALTH + (difficultyLevel - 1) * 20;

        loadImages();
        loadBulletImages();
    }


    // Old constructor defaults to difficulty 1
    // Old constructor defaults to difficulty 1
    public BossEnemy(double x, double y, int variant, GamePanel gp) {
        this(x, y, variant, 1, gp);  // pass gp to the main constructor
    }

    public BossEnemy(double x, double y, GamePanel gp) {
        this(x, y, 0, 1, gp);        // default variant 0, difficulty 1
    }


    private void loadImages() {
        if (images == null) {
            images = new BufferedImage[5];
            try {
                images[0] = ImageIO.read(getClass().getResourceAsStream("/enemies/boss.png"));
                images[1] = ImageIO.read(getClass().getResourceAsStream("/enemies/boss2.png"));
                images[2] = ImageIO.read(getClass().getResourceAsStream("/enemies/boss3.png"));
                images[3] = ImageIO.read(getClass().getResourceAsStream("/enemies/boss4.png"));
                images[4] = ImageIO.read(getClass().getResourceAsStream("/enemies/archie.png"));
            } catch (IOException | NullPointerException e) {
                for (int i = 0; i < images.length; i++)
                    images[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            }
        }
    }

    private void loadBulletImages() {
        if (bulletImages == null) {
            bulletImages = new BufferedImage[5];
            try {
                bulletImages[0] = ImageIO.read(getClass().getResourceAsStream("/bullets/boss1.png"));
                bulletImages[1] = ImageIO.read(getClass().getResourceAsStream("/bullets/boss2.png"));
                bulletImages[2] = ImageIO.read(getClass().getResourceAsStream("/bullets/boss3.png"));
                bulletImages[3] = ImageIO.read(getClass().getResourceAsStream("/bullets/boss4.png"));
                bulletImages[4] = ImageIO.read(getClass().getResourceAsStream("/bullets/archie.png"));
            } catch (IOException | NullPointerException e) {
                for (int i = 0; i < bulletImages.length; i++)
                    bulletImages[i] = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            }
        }
    }

    @Override
    public void update() {
        if (y < 50) {
            y += speed;
        } else {
            ready = true;
        }

        if (ready) handleShooting();
        updateBullets();
    }

    private void handleShooting() {
        if (shootCooldown <= 0) {
            int maxPattern = 3;

            // Increase available patterns with difficulty
            if (difficultyLevel >= 2) maxPattern = 4;
            if (difficultyLevel >= 3) maxPattern = 5;
            if (difficultyLevel >= 4) maxPattern = 6;

            switch (patternIndex % maxPattern) {
                case 0 -> shootStraight();
                case 1 -> shootSpread();
                case 2 -> shootCircle();
                case 3 -> shootDiagonalCross();
                case 4 -> shootFanWave();
                case 5 -> shootRandomRapid();
            }

            // Play laser sound here
            if (gp != null) {
                gp.playSE(3); // replace 3 with your laser sound index
            }

            patternIndex++;
            shootCooldown = FIRE_RATE - (difficultyLevel * 5); // faster shooting at higher difficulty
            if (shootCooldown < 15) shootCooldown = 15;
        }
        shootCooldown--;
    }


    // ---------------- SHOOTING PATTERNS ----------------
    private void shootStraight() {
        double spawnX = x + width / 2.0 - 8;
        double spawnY = y + height;
        bullets.add(new BossBullet(spawnX, spawnY, 0, 1, bulletImages[variant]));
    }

    private void shootSpread() {
        int bulletCount = 7;
        double angleStart = -60;
        double angleStep = 120.0 / (bulletCount - 1);

        double spawnX = x + width / 2.0 - 8;      // center of boss
        double spawnY = y + 60;                   // 30 pixels from top (adjust to boss nose)

        for (int i = 0; i < bulletCount; i++) {
            double angle = Math.toRadians(angleStart + angleStep * i);
            double dx = Math.sin(angle);
            double dy = Math.cos(angle);
            bullets.add(new BossBullet(spawnX, spawnY, dx, dy, bulletImages[variant]));
        }
    }


    private void shootCircle() {
        int bulletCount = 16;
        for (int i = 0; i < bulletCount; i++) {
            double angle = (2 * Math.PI / bulletCount) * i;
            double dx = Math.cos(angle);
            double dy = Math.sin(angle);
            bullets.add(new BossBullet(x + width / 2.0 - 8, y + height / 2.0, dx, dy, bulletImages[variant]));
        }
    }

    private void shootDiagonalCross() {
        double[][] directions = { {1,1}, {-1,1}, {1,-1}, {-1,-1} };
        for (double[] dir : directions) {
            bullets.add(new BossBullet(x + width / 2.0 - 8, y + height / 2.0, dir[0], dir[1], bulletImages[variant]));
        }
    }

    private void shootFanWave() {
        int bulletCount = 5;
        double angleStart = -45;
        double angleStep = 22.5;
        for (int i = 0; i < bulletCount; i++) {
            double angle = Math.toRadians(angleStart + i * angleStep);
            double dx = Math.sin(angle);
            double dy = Math.cos(angle);
            bullets.add(new BossBullet(x + width / 2.0 - 8, y + height / 2.0, dx, dy, bulletImages[variant]));
        }
    }

    private void shootRandomRapid() {
        for (int i = 0; i < 10; i++) {
            double dx = Math.random() * 2 - 1;
            double dy = Math.random() * 1 + 0.5;
            bullets.add(new BossBullet(x + width / 2.0 - 8, y + height / 2.0, dx, dy, bulletImages[variant]));
        }
    }

    private void updateBullets() {
        bullets.removeIf(b -> {
            b.update();
            return b.outOfBounds();
        });
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.drawImage(images[variant], (int) x, (int) y, width, height, null);

        // Health bar
        int barWidth = width;
        int barHeight = 15;
        int barX = (int) x;
        int barY = (int) y - barHeight - 5;

        g2.setColor(Color.GRAY);
        g2.fillRect(barX, barY, barWidth, barHeight);

        g2.setColor(Color.RED);
        int healthWidth = (int) (barWidth * ((double) health / (BASE_HEALTH + (difficultyLevel-1)*20)));
        g2.fillRect(barX, barY, healthWidth, barHeight);

        g2.setColor(Color.WHITE);
        g2.drawRect(barX, barY, barWidth, barHeight);

        bullets.forEach(b -> b.draw(g2));
    }

    public ArrayList<BossBullet> getBullets() { return bullets; }

    public boolean isReady() { return ready; }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height - 50);
    }
}
