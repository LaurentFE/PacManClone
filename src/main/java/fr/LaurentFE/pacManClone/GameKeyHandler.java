package fr.LaurentFE.pacManClone;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameKeyHandler implements KeyListener {

    private boolean upPressed;
    private boolean rightPressed;
    private boolean downPressed;
    private boolean leftPressed;

    public GameKeyHandler() {
        super();
        upPressed = false;
        rightPressed = false;
        downPressed = false;
        leftPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_UP ||
                keyCode == KeyEvent.VK_Z) {
            upPressed = true;
        }
        if (keyCode == KeyEvent.VK_RIGHT ||
                keyCode == KeyEvent.VK_D) {
            rightPressed = true;
        }
        if (keyCode == KeyEvent.VK_DOWN ||
                keyCode == KeyEvent.VK_S) {
            downPressed = true;
        }
        if (keyCode == KeyEvent.VK_LEFT ||
                keyCode == KeyEvent.VK_Q) {
            leftPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_UP ||
                keyCode == KeyEvent.VK_Z) {
            upPressed = false;
        }
        if (keyCode == KeyEvent.VK_RIGHT ||
                keyCode == KeyEvent.VK_D) {
            rightPressed = false;
        }
        if (keyCode == KeyEvent.VK_DOWN ||
                keyCode == KeyEvent.VK_S) {
            downPressed = false;
        }
        if (keyCode == KeyEvent.VK_LEFT ||
                keyCode == KeyEvent.VK_Q) {
            leftPressed = false;
        }
    }


    public boolean isUpPressed() {
        return upPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }
}
