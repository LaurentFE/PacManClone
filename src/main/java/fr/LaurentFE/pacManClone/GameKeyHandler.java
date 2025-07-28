package fr.LaurentFE.pacManClone;

import fr.LaurentFE.pacManClone.entities.Orientation;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameKeyHandler implements KeyListener {

    private Orientation nextOrientation;

    public GameKeyHandler() {
        super();
        nextOrientation = null;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_UP
                || keyCode == KeyEvent.VK_Z) {
            nextOrientation = Orientation.UP;
        }
        if (keyCode == KeyEvent.VK_RIGHT
                || keyCode == KeyEvent.VK_D) {
            nextOrientation = Orientation.RIGHT;
        }
        if (keyCode == KeyEvent.VK_DOWN
                || keyCode == KeyEvent.VK_S) {
            nextOrientation = Orientation.DOWN;
        }
        if (keyCode == KeyEvent.VK_LEFT
                || keyCode == KeyEvent.VK_Q) {
            nextOrientation = Orientation.LEFT;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public Orientation getNextOrientation() {
        return nextOrientation;
    }

    public void resetInput() {
        nextOrientation = null;
    }
}
