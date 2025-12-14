package main;

import javax.swing.*;
import java.awt.*;

public class ScrollingBackground {

    private final Image image;
    private int y1 = 0;
    private int y2;
    private final int speed;

    public ScrollingBackground(String path, int height, int speed) {
        this.image = new ImageIcon(getClass().getResource(path)).getImage();
        this.y2 = -height;
        this.speed = speed;
    }

    public void update(int height) {
        y1 += speed;
        y2 += speed;

        if (y1 >= height) y1 = -height;
        if (y2 >= height) y2 = -height;
    }

    public void draw(Graphics2D g2, int width, int height) {
        g2.drawImage(image, 0, y1, width, height, null);
        g2.drawImage(image, 0, y2, width, height, null);
    }
}
