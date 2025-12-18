package main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseHandler extends MouseAdapter {

    private final GamePanel gp;

    public boolean leftPressed;
    public int mouseX, mouseY;

    public MouseHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void mouseMoved(MouseEvent e) {

        if (gp.ui == null || gp.ui.startButton == null) return;

        int mx = gp.screenToGameX(e.getX());
        int my = gp.screenToGameY(e.getY());

        gp.ui.startButton.hovered =
                gp.ui.startButton.contains(mx, my);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) return;

        leftPressed = true;

        mouseX = gp.screenToGameX(e.getX());
        mouseY = gp.screenToGameY(e.getY());

        switch (gp.gameState) {

            case MENU -> {
                if (gp.ui.startButton.contains(mouseX, mouseY)) {
                    gp.music.fadeTo(GamePanel.MENU_MUSIC, 800);
                    gp.ui.startFadeToGame();
                }
            }

            case PLAYING -> {
                if (gp.ui.pauseButton.contains(mouseX, mouseY)) {
                    gp.gameState = GameState.PAUSED;
                }
            }

            case PAUSED -> {
                if (gp.ui.resumeButton.contains(mouseX, mouseY)) {
                    gp.gameState = GameState.PLAYING;
                } else if (gp.ui.exitButton.contains(mouseX, mouseY)) {
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
