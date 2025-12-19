package entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import main.GamePanel;

import javax.imageio.ImageIO;

public class ShieldPickup extends Pickup {
    private BufferedImage image;
    public ShieldPickup(int x, int y) {
        this.x = x;
        this.y = y;

        try {
            image = ImageIO.read(getClass().getResource("/shield.png")); // put your image in resources/images/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, width, height, null);
    }

    public String getEffectText() {
        return "+SHIELD";
    }
    @Override
    public void applyEffect(Player player) {
        player.shieldActive = true;
        player.shieldHP = player.maxShieldHP;
    }
}
