package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public abstract class Enemy {

    public double x, y;
    public int width, height;
    public int speed;
    public int health;

    public abstract void update();
    public abstract void draw(Graphics2D g2);

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    public void takeDamage(int damage) {
        health -= damage;
    }

    public boolean isDead() {
        return health <= 0;
    }
}
