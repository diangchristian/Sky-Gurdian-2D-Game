package entity;

import main.GamePanel;
import main.KeyHandler;

import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Player {
    public int x, y;
    public int speed;
    public final int width = 80;
    public final int height = 80;
    public int health = 100; // remove final so it can decrease

    private BufferedImage plane;

    GamePanel gp;
    KeyHandler keyH;

    public Player(GamePanel gp, KeyHandler keyH){
        this.gp = gp;
        this.keyH = keyH;
        this.setDefaultValues();
        this.getPlayerImage();
    }

    public void setDefaultValues() {
        x = gp.WIDTH / 2 - width / 2;
        y = gp.HEIGHT / 2 - height / 2;
        speed = 5;
    }

    public void getPlayerImage(){
        try{
            plane = ImageIO.read(getClass().getResourceAsStream("/mainplane.png"));
        }catch(IOException e){
            e.printStackTrace();
            plane = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = plane.createGraphics();
            g2.setColor(Color.BLUE);
            g2.fillRect(0, 0, width, height);
            g2.dispose();
        }
    }

    public void update() {
        if (keyH.upPressed)    { y -= speed; }
        if (keyH.downPressed)  { y += speed; }
        if (keyH.leftPressed)  { x -= speed; }
        if (keyH.rightPressed) { x += speed; }

        // Boundaries check
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > gp.WIDTH - width) x = gp.WIDTH - width;
        if (y > gp.HEIGHT - height) y = gp.HEIGHT - height;
    }

    public Bullet shoot() {
        double startX = x + width / 2.0 - 22; // center bullet
        double startY = y;                    // top of plane
        return new Bullet(startX, startY);
    }

    public void takeDamage(int dmg) {
        health -= dmg;
    }

    public void heal(int amount) {
        health = Math.min(health + amount, 100);
    }

    public boolean isDead() {
        return health <= 0;
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(plane, x, y, width + 20, height + 20, null);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
