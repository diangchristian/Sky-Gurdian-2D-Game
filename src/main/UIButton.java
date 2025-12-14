package main;

import java.awt.*;

public class UIButton {

    public int x, y, width, height;
    public String text;
    public boolean hovered = false;

    private Font font;

    public UIButton(int x, int y, int width, int height, String text, Font font) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.font = font;
    }

    public void draw(Graphics2D g2) {
        // Background
        if (hovered) {
            g2.setColor(new Color(40, 180, 255));
        } else {
            g2.setColor(new Color(20, 120, 200));
        }
        g2.fillRoundRect(x, y, width, height, 25, 25);

        // Border
        g2.setStroke(new BasicStroke(3));
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(x, y, width, height, 25, 25);

        // Text
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();

        int textX = x + (width - fm.stringWidth(text)) / 2;
        int textY = y + (height + fm.getAscent()) / 2 - 4;

        g2.drawString(text, textX, textY);
    }

    public boolean contains(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }


}
