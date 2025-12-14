package entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Bullet {

    public double x, y;
    public int width = 48, height = 48;
    public double dx = 0, dy = -1;
    public double speed = 15;
    protected BufferedImage image;

    // Constructor with optional image
    public Bullet(double x, double y, double dx, double dy, BufferedImage img) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;

        if (img != null) {
            this.image = img;
            this.width = img.getWidth();
            this.height = img.getHeight();
        } else {
            loadDefaultImage();
        }
    }

    public Bullet(double x, double y) { // default player bullet
        this(x, y, 0, -1, null);
    }

    private void loadDefaultImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/bullets/bullet.png"));
        } catch (IOException | NullPointerException e) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            System.err.println("Failed to load default bullet image!");
        }
    }

    public void update() {
        x += dx * speed;
        y += dy * speed;
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(image, (int)x, (int)y, width, height, null);
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }

    public boolean outOfBounds(int screenWidth, int screenHeight) {
        return x < -width || x > screenWidth || y < -height || y > screenHeight;
    }
}
