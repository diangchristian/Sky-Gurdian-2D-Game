package entity;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class EnemyBullet {

    public double x, y;
    private final double dx, dy;
    private final int speed = 6;
    private final int size = 42;
    private static BufferedImage image;

    public EnemyBullet(double x, double y, double dx, double dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;

        try {
            image = ImageIO.read(getClass().getResource("/bullets/sniperbullet.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        x += dx * speed;
        y += dy * speed;
    }

    public void draw(Graphics2D g2) {

        g2.drawImage(image, (int)x, (int)y, size, size, null);


    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, size, size);
    }

    public boolean outOfBounds() {
        return y > 800 || x < -20 || x > 820;
    }
}
