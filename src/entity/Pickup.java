package entity;

import java.awt.*;
import main.GamePanel;

public abstract class Pickup {

    protected int x, y;
    protected int width = 32, height = 32;
    protected int speed = 2;


    public void update() {
        y += speed;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.GREEN); // placeholder
        g2.fillRect(x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean isOffScreen() {
        return y > GamePanel.HEIGHT;
    }

    public abstract void applyEffect(Player player);

    public abstract String getEffectText();

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

}
