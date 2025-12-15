package main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ExplosionAssets {
    public static BufferedImage[] normalExplosion;
    public static BufferedImage[] bossExplosion;

    public static void load() {
        normalExplosion = loadExplosion("/explosions/basic/", 11, 32, 32);
    }

    private static BufferedImage[] loadExplosion(String path, int count, int fallbackWidth, int fallbackHeight) {
        BufferedImage[] images = new BufferedImage[count];
        for (int i = 0; i < count; i++) {
            try {
                images[i] = ImageIO.read(ExplosionAssets.class.getResourceAsStream(path + i  + ".png"));
            } catch (IOException | NullPointerException e) {
                // Fallback blank image if file not found
                images[i] = new BufferedImage(fallbackWidth, fallbackHeight, BufferedImage.TYPE_INT_ARGB);
            }
        }
        return images;
    }
}
