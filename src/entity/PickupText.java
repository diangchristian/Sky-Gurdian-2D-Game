package entity;

import java.awt.*;

public class PickupText {
    private String text;
    private int x, y;
    private Font font;
    private int timer = 60; // frames until it disappears
    private float alpha = 1f; // transparency

    public PickupText(String text, int x, int y, Font font) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.font = font;
    }

    public void update() {
        y -= 1; // move upward
        timer--;
        alpha = Math.max(0, timer / 60f); // fade out
    }

    public void draw(Graphics2D g2) {
        g2.setFont(font);
        g2.setColor(new Color(1f, 1f, 1f, alpha)); // white with alpha
        g2.drawString(text, x, y);
    }

    public boolean isFinished() {
        return timer <= 0;
    }
}
