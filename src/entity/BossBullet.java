package entity;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BossBullet extends Bullet {

    private double speed = 5;
    private static final int BULLET_SIZE = 30; // same as player bullet

    public BossBullet(double x, double y, double dx, double dy, BufferedImage image) {
        super(x, y, dx, dy, image);

        // Fixed size for consistency with player bullets
        this.width = BULLET_SIZE;
        this.height = BULLET_SIZE;

        // Normalize direction
        double len = Math.sqrt(dx * dx + dy * dy);
        if (len != 0) {
            this.dx = dx / len;
            this.dy = dy / len;
        }
    }

    @Override
    public void update() {
        x += dx * speed;
        y += dy * speed;
    }

    @Override
    public void draw(Graphics2D g2) {
        if (image != null) {
            g2.drawImage(image, (int) x, (int) y, width, height, null);
        } else {
            g2.setColor(Color.RED);
            g2.fillOval((int) x, (int) y, width, height);
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    public boolean outOfBounds() {
        return x < -width || x > 800 || y < -height || y > 800;
    }
}
