package entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Explosion {
    private double x, y;
    private int width, height;
    private int frame = 0;
    private BufferedImage[] images;

    public Explosion(double x, double y, int width, int height, BufferedImage[] frames) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.images = frames;
    }

    public void update() {
        frame++;
    }

    public boolean isFinished() {
        return images == null || frame >= images.length;
    }

    public void draw(Graphics2D g2) {
        if (images != null && frame < images.length) {
            g2.drawImage(images[frame], (int)x, (int)y, width, height, null);
        }
    }

}
