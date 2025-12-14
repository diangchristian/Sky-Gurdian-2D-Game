package entity;

import main.GamePanel;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class FastEnemy extends Enemy {

    private static BufferedImage image;

    private double dx;
    private double dy;

    public FastEnemy(double x, double y) {
        this.x = x;
        this.y = y;

        width = 70;
        height = 70;
        health = 5;

        dx = Math.random() < 0.5 ? -3.5 : 3.5;
        dy = 4.5;

        loadImage();
    }

    private void loadImage() {
        if (image == null) {
            try {
                image = ImageIO.read(
                        getClass().getResourceAsStream("/enemies/fast-enemy.png")
                );
            } catch (IOException | NullPointerException e) {
                image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            }
        }
    }

    @Override
    public void update() {

        // Strafing movement
        x += dx;
        y += dy;

        // Bounce off walls
        if (x <= 0 || x + width >= GamePanel.WIDTH) {
            dx *= -1;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.drawImage(image, (int) x, (int) y, width, height, null);
    }
}
