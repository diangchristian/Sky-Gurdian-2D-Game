package entity;

import main.GamePanel;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class BasicEnemy extends Enemy {

    private static BufferedImage image;

    private double angle;
    private double hoverSpeed = 2.0;
    private double enterSpeed = 2.5;
    private double targetY;

    public BasicEnemy(double x, double y) {
        this.x = x;
        this.y = y;

        width = 48;
        height = 48;
        health = 3;

        angle = Math.random() * Math.PI * 2;
        targetY = 80 + Math.random() * 200;

        loadImage();
    }

    private void loadImage() {
        if (image == null) {
            try {
                image = ImageIO.read(
                        getClass().getResourceAsStream("/enemies/enemy1.png")
                );
            } catch (IOException | NullPointerException e) {
                image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            }
        }
    }

    @Override
    public void update() {

        // Fly down until target position
        if (y < targetY) {
            y += enterSpeed;
        } else {
            // Hover left-right like a drone
            angle += 0.05;
            x += Math.sin(angle) * hoverSpeed;
        }

        // Keep inside screen
        if (x < 0) x = 0;
        if (x + width > GamePanel.WIDTH) {
            x = GamePanel.WIDTH - width;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.drawImage(image, (int) x, (int) y, width, height, null);
    }
}
