package main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseHandler extends MouseAdapter {

    private GamePanel gp;

    public boolean leftPressed;
    public int mouseX, mouseY;

    public MouseHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void mouseMoved(MouseEvent e) {

        if (gp.ui == null) return;
        if (gp.ui.startButton == null) return;

        int mx = e.getX();
        int my = e.getY();

        gp.ui.startButton.hovered =
                gp.ui.startButton.contains(mx, my);
    }


    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) return;

        leftPressed = true;
        mouseX = e.getX();
        mouseY = e.getY();

        switch (gp.gameState) {

            case MENU -> {
                if (gp.ui.startButton.contains(mouseX, mouseY)) {
                    gp.music.fadeTo(0, 800);      // stop menu music
                    gp.ui.startFadeToGame();       // fade transition to PLAYING
                }
            }

            case PLAYING -> {
                // Pause button click
                if (gp.ui.pauseButton.contains(mouseX, mouseY)) {
                    gp.ui.isPaused = true;
                    gp.gameState = GameState.PAUSED;
                }
            }

            case PAUSED -> {
                // Resume button click
                if (gp.ui.resumeButton.contains(mouseX, mouseY)) {
                    gp.ui.isPaused = false;
                    gp.gameState = GameState.PLAYING;
                }
                // Exit button click
                else if (gp.ui.exitButton.contains(mouseX, mouseY)) {
                    System.exit(0);
                }
            }
        }
    }


    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            leftPressed = false;
        }
    }
}
