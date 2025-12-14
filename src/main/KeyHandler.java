package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

   public boolean
            upPressed,
            downPressed,
            leftPressed,
            rightPressed,
            arrowUpPressed,
            arrowDownPressed,
            arrowLeftPressed,
            arrowRightPressed,
            spaceBarPressed;

    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode(); // returns the interger keyCode that is pressed

        if(code == KeyEvent.VK_W){
            upPressed = true;
        }

        if(code == KeyEvent.VK_S){
            downPressed = true;
        }

        if(code == KeyEvent.VK_A){
            leftPressed = true;
        }

        if(code == KeyEvent.VK_D){
            rightPressed = true;
        }

        if(code == KeyEvent.VK_UP){
            arrowUpPressed = true;
        }

        if(code == KeyEvent.VK_DOWN){
            arrowDownPressed = true;
        }

        if(code == KeyEvent.VK_LEFT){
            arrowLeftPressed = true;
        }

        if(code == KeyEvent.VK_RIGHT){
            arrowRightPressed = true;
        }

        if(code == KeyEvent.VK_SPACE){
            spaceBarPressed = true;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if(code == KeyEvent.VK_W){
            upPressed = false;
        }

        if(code == KeyEvent.VK_S){
            downPressed = false;
        }

        if(code == KeyEvent.VK_A){
            leftPressed = false;
        }

        if(code == KeyEvent.VK_D){
            rightPressed = false;
        }

        if(code == KeyEvent.VK_UP){
            arrowUpPressed = false;
        }

        if(code == KeyEvent.VK_DOWN){
            arrowDownPressed = false;
        }

        if(code == KeyEvent.VK_LEFT){
            arrowLeftPressed = false;
        }

        if(code == KeyEvent.VK_RIGHT){
            arrowRightPressed = false;
        }

        if(code == KeyEvent.VK_SPACE){
            spaceBarPressed = false;
        }
    }

}
