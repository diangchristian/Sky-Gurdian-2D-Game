package entity;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;

public class SniperEnemy extends Enemy {

    private static BufferedImage image;
    private final ArrayList<EnemyBullet> bullets = new ArrayList<>();

    private final GamePanel gp;
    private final Player player;

    private int shootCooldown = 0;
    private final int FIRE_RATE = 120; // slow fire (2 seconds at 60 FPS)

    private boolean stopped = false;
    private int stopY;

    public SniperEnemy(double x, double y, GamePanel gp, Player player) {
        this.x = x;
        this.y = y;
        this.gp = gp;
        this.player = player;

        width = 64;
        height = 64;
        speed = 1;
        health = 8;

        stopY = 120 + (int)(Math.random() * 120);

        loadImage();
    }

    private void loadImage() {
        if (image == null) {
            try {
                image = ImageIO.read(
                        getClass().getResourceAsStream("/enemies/sniperenemy.png")
                );
            } catch (IOException | NullPointerException e) {
                image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            }
        }
    }

    @Override
    public void update() {

        // Move down until stop position
        if (!stopped) {
            y += speed;
            if (y >= stopY) {
                stopped = true;
            }
        } else {
            handleShooting();
        }

        updateBullets();
    }

    private void handleShooting() {
        if (shootCooldown > 0) {
            shootCooldown--;
            return;
        }

        shootAtPlayer();
        gp.playSE(3); // enemy shot sound
        shootCooldown = FIRE_RATE;
    }

    private void shootAtPlayer() {
        double startX = x + width / 2.0;
        double startY = y + height;

        double targetX = player.x + player.width / 2.0;
        double targetY = player.y + player.height / 2.0;

        double dx = targetX - startX;
        double dy = targetY - startY;

        double length = Math.sqrt(dx * dx + dy * dy);
        dx /= length;
        dy /= length;

        bullets.add(new EnemyBullet(startX, startY, dx, dy));
    }

    private void updateBullets() {
        bullets.removeIf(b -> {
            b.update();
            return b.outOfBounds();
        });
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.drawImage(image, (int)x, (int)y, width, height, null);
        bullets.forEach(b -> b.draw(g2));
    }

    public ArrayList<EnemyBullet> getBullets() {
        return bullets;
    }
}
