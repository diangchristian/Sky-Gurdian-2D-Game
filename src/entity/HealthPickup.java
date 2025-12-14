package entity;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class HealthPickup extends Pickup {

    private final int healAmount = 20;
    private BufferedImage image;

    public HealthPickup(int x, int y, GamePanel gp) {
        this.x = x;
        this.y = y;
        this.width = 32;  // or your desired size
        this.height = 32;

        try {
            image = ImageIO.read(getClass().getResource("/health.png")); // put your image in resources/images/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        if (image != null) {
            g2.drawImage(image, x, y, width, height, null);
        } else {
            // fallback if image not loaded
            g2.setColor(Color.PINK);
            g2.fillOval(x, y, width, height);
        }
    }

    @Override
    public void applyEffect(Player player) {
        player.heal(healAmount); // ensure your Player class has a heal method
    }
}
