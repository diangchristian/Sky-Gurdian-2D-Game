package main;

import java.awt.*;
import java.awt.event.MouseEvent;

public class GameButton {

    private int x, y, width, height;
    private String text;

    private boolean hovered, pressed, enabled = true;

    private Runnable onClick;

    // Button colors
    private Color normalColor = new Color(40, 120, 220);
    private Color hoverColor  = new Color(70, 160, 255);
    private Color pressColor  = new Color(20, 90, 180);
    private Color disabledColor = new Color(120, 120, 120);

    public GameButton(String text, int x, int y, int width, int height, Runnable onClick) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.onClick = onClick;
    }

    // DRAW
    public void draw(Graphics2D g) {
        Color bg = disabledColor;
        if (enabled) {
            bg = normalColor;
            if (pressed) bg = pressColor;
            else if (hovered) bg = hoverColor;
        }

        g.setColor(bg);
        g.fillRoundRect(x, y, width, height, 20, 20);

        g.setColor(Color.BLACK);
        g.drawRoundRect(x, y, width, height, 20, 20);

        g.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g.getFontMetrics();
        int tx = x + (width - fm.stringWidth(text)) / 2;
        int ty = y + (height + fm.getAscent()) / 2 - 4;

        g.setColor(Color.WHITE);
        g.drawString(text, tx, ty);
    }

    // INPUT
    public void mouseMoved(MouseEvent e) {
        hovered = enabled && contains(e.getX(), e.getY());
    }

    public void mousePressed(MouseEvent e) {
        if (enabled && contains(e.getX(), e.getY())) {
            pressed = true;
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (enabled && pressed && contains(e.getX(), e.getY())) {
            onClick.run(); // EXECUTE ACTION
        }
        pressed = false;
    }

    private boolean contains(int mx, int my) {
        return mx >= x && mx <= x + width &&
                my >= y && my <= y + height;
    }

    // SETTERS
    public void setText(String text) {
        this.text = text;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        hovered = pressed = false;
    }
}
