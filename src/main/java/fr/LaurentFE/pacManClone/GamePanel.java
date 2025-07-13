package fr.LaurentFE.pacManClone;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {

    private GameKeyHandler gameKeyHandler;
    private Thread gameThread;


    private PacMan pacMan;

    public GamePanel() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        setDoubleBuffered(true); // Render is made on a second panel, then copied to the main widow => smoother rendering
        gameKeyHandler = new GameKeyHandler();
        addKeyListener(gameKeyHandler);
        setFocusable(true);

        pacMan = new PacMan(100, 100, Orientation.RIGHT);
    }

    public void startGameThread() {
        if (gameThread != null) {
            return;
        }
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void stopGameThread() {
        gameThread=null;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        drawPacMan(g2d);
    }

    private void drawPacMan(Graphics2D g2d) {
        int mouthStartAngle;
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(pacMan.getX(), pacMan.getY(), 150,150);
        g2d.setColor(Color.BLACK);
        if(pacMan.getOrientation() == Orientation.UP) {
            mouthStartAngle = 90;
        } else if (pacMan.getOrientation() == Orientation.RIGHT) {
            mouthStartAngle = 0;
        } else if (pacMan.getOrientation() == Orientation.DOWN) {
            mouthStartAngle = -90;
        } else {
            mouthStartAngle = 180;
        }
        g2d.fillArc(pacMan.getX(),
                pacMan.getY(),
                150,
                150,
                mouthStartAngle - pacMan.getCurrentMouthAngle()/2,
                pacMan.getCurrentMouthAngle());
    }

    public void update() {
        // PacMan can't go diagonally.
        if (gameKeyHandler.isUpPressed()) {
            pacMan.setOrientation(Orientation.UP);
            pacMan.offsetY(-3);
        } else if (gameKeyHandler.isRightPressed()) {
            pacMan.setOrientation(Orientation.RIGHT);
            pacMan.offsetX(3);
        } else if (gameKeyHandler.isDownPressed()) {
            pacMan.setOrientation(Orientation.DOWN);
            pacMan.offsetY(3);
        } else if (gameKeyHandler.isLeftPressed()) {
            pacMan.setOrientation(Orientation.LEFT);
            pacMan.offsetX(-3);
        }
        pacMan.animateMouth();
    }

    @Override
    public void run() {
        final double FPS = 60;
        double drawIntervalNanoSec = 1_000_000_000/FPS;
        long lastUpdateTimeNanoSec = System.nanoTime();
        long currentTimeNanoSec;

        while (gameThread != null) {
            currentTimeNanoSec = System.nanoTime();

            if(currentTimeNanoSec >= lastUpdateTimeNanoSec + drawIntervalNanoSec) {
                update();
                repaint();
                lastUpdateTimeNanoSec = currentTimeNanoSec;
            }
        }
    }
}
