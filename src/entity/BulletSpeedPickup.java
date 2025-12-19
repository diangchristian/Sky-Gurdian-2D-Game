package entity;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class BulletSpeedPickup extends Pickup {

    private final double boost = 1.5;
    private BufferedImage image;
    public BulletSpeedPickup(int x, int y) {
        this.x = x;
        this.y = y;

        try {
            image = ImageIO.read(getClass().getResource("/bulletspeed.png")); // put your image in resources/images/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, width, height, null);
    }

    @Override
    public String getEffectText() {
        return "+SPD";
    }

    // 👇 ADD IT HERE
    @Override
    public void applyEffect(Player player) {
        player.bulletSpeed = (int) Math.min(
                player.bulletSpeed + boost,
                player.maxBulletSpeed
        );

        System.out.println("Bullet speed increased: " + player.bulletSpeed);
    }
}
