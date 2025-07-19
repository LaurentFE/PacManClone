package fr.LaurentFE.pacManClone;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GamePanel extends JPanel implements Runnable {

    private final GameKeyHandler gameKeyHandler;
    private Thread gameThread;


    private final int tileSize;
    private final GameMap gameMap;
    private final PacMan pacMan;

    public GamePanel(GameMap gameMap) {
        tileSize = 32;
        int moveSpeed = tileSize/8;
        pacMan = new PacMan(
                new Point(tileSize*13,tileSize*26),
                tileSize,
                tileSize,
                Orientation.RIGHT,
                moveSpeed);
        this.gameMap = gameMap;
        setPreferredSize(new Dimension(gameMap.getMapWidthTile()*tileSize, gameMap.getMapHeightTile()*tileSize));
        setBackground(Color.BLACK);
        setDoubleBuffered(true); // Render is made on a second panel, then copied to the main widow => smoother rendering
        gameKeyHandler = new GameKeyHandler();
        addKeyListener(gameKeyHandler);
        setFocusable(true);
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

        drawMap(g2d);
        drawPacMan(g2d);
    }

    private void drawMap(Graphics2D g2d) {
        for (int y = 0; y < gameMap.getMapHeightTile(); y++) {
            for (int x = 0; x < gameMap.getMapWidthTile(); x++) {
                TileType currentTile = gameMap.getTile(new Point(x,y));
                if (currentTile == TileType.PATH
                        || currentTile == TileType.OUTOFBOUNDS) {
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(x*tileSize,
                            y*tileSize,
                            tileSize,
                            tileSize);
                } else if (currentTile == TileType.DOOR) {
                    g2d.setColor(Color.PINK);
                    g2d.fillRect(x*tileSize,
                            y*tileSize + tileSize/2 + tileSize/4,
                            tileSize,
                            tileSize/8);
                } else if (currentTile != TileType.UNDEFINED) {
                    drawWallShape(x,y,g2d);
                }
            }
        }
    }

    private void drawDoubleHorizontalWall(boolean pathAbove, Graphics2D g2d, int x, int y) {
        int y1, y2;
        if (pathAbove) {
            y1 = y*tileSize + tileSize/2 + 2;
            y2 = y*tileSize + tileSize - 1;
        } else {
            y1 = y*tileSize;
            y2 = y*tileSize + tileSize/2 - 2;
        }
        g2d.drawLine(x*tileSize,y1,(x+1)*tileSize,y1);
        g2d.drawLine(x*tileSize,y2,(x+1)*tileSize,y2);
    }

    private void drawDoubleVerticalWall(boolean pathLeft, Graphics2D g2d, int x, int y) {
        int x1, x2;
        if(pathLeft) {
            x1 = x*tileSize + tileSize/2 + 2;
            x2 = x*tileSize + tileSize - 1;
        } else {
            x1 = x*tileSize;
            x2 = x*tileSize + tileSize/2 - 2;
        }
        g2d.drawLine(x1,y*tileSize,x1,(y+1)*tileSize);
        g2d.drawLine(x2,y*tileSize,x2,(y+1)*tileSize);
    }

    private void drawDoubleOuterDownRightCorner(boolean pathAbove, Graphics2D g2d, int x, int y) {
        int x1, x2;
        int y1, y2;
        if(pathAbove) {
            x1 = x*tileSize + tileSize/2 + 2;
            y1 = y*tileSize + tileSize/2 + 2;
            g2d.drawArc(x1,
                    y1,
                    tileSize,
                    tileSize,
                    90,
                    90);
        } else {
            x1 = x*tileSize;
            x2 = x*tileSize + tileSize/2 - 2;
            y1 = y*tileSize;
            y2 = y*tileSize + tileSize/2 - 2;
            g2d.drawArc(x1,
                    y1,
                    tileSize*2,
                    tileSize*2,
                    90,
                    90);
            g2d.drawArc(x2,
                    y2,
                    tileSize + 2,
                    tileSize + 2,
                    90,
                    90);
        }
    }

    private void drawDoubleOuterDownLeftCorner(boolean pathAbove, Graphics2D g2d, int x, int y) {
        int x1, x2;
        int y1, y2;
        if(pathAbove) {
            x1 = x*tileSize - tileSize/2 - 2;
            y1 = y*tileSize + tileSize/2 + 2;
            g2d.drawArc(x1,
                    y1,
                    tileSize,
                    tileSize,
                    0,
                    90);
        } else {
            x1 = x*tileSize - tileSize;
            x2 = x*tileSize - tileSize/2;
            y1 = y*tileSize;
            y2 = y*tileSize + tileSize/2 - 2;
            g2d.drawArc(x1,
                    y1,
                    tileSize*2,
                    tileSize*2,
                    0,
                    90);
            g2d.drawArc(x2,
                    y2,
                    tileSize + 2,
                    tileSize + 2,
                    0,
                    90);
        }
    }

    private void drawDoubleOuterUpLeftCorner(boolean pathAbove, Graphics2D g2d, int x, int y) {
        int x1, x2;
        int y1, y2;
        if(pathAbove) {
            x1 = x*tileSize - tileSize/2 - 2;
            y1 = y*tileSize - tileSize/2 - 2;
            g2d.drawArc(x1,
                    y1,
                    tileSize,
                    tileSize,
                    0,
                    -90);
        } else {
            x1 = x*tileSize - tileSize;
            x2 = x*tileSize - tileSize/2;
            y1 = y*tileSize - tileSize - 1;
            y2 = y*tileSize - tileSize/2;
            g2d.drawArc(x1,
                    y1,
                    tileSize*2,
                    tileSize*2,
                    0,
                    -90);
            g2d.drawArc(x2,
                    y2,
                    tileSize + 2,
                    tileSize + 2,
                    0,
                    -90);
        }
    }

    private void drawDoubleOuterUpRightCorner(boolean pathBelow, Graphics2D g2d, int x, int y) {
        int x1, x2;
        int y1, y2;
        if(pathBelow) {
            x1 = x*tileSize + tileSize/2 + 2;
            y1 = y*tileSize - tileSize/2 - 2;
            g2d.drawArc(x1,
                    y1,
                    tileSize,
                    tileSize,
                    -90,
                    -90);
        } else {
            x1 = x*tileSize;
            x2 = x*tileSize + tileSize/2 - 2;
            y1 = y*tileSize - tileSize - 1;
            y2 = y*tileSize - tileSize/2 - 1;
            g2d.drawArc(x1,
                    y1,
                    tileSize*2,
                    tileSize*2,
                    -90,
                    -90);
            g2d.drawArc(x2,
                    y2,
                    tileSize + 2,
                    tileSize + 2,
                    -90,
                    -90);
        }
    }

    private void drawDoubleInnerDownRightCorner(boolean pathAbove, Graphics2D g2d, int x, int y) {
        int x1 = x*tileSize;
        int x2 = x*tileSize + tileSize/2 - 2;
        int y1 = y*tileSize;
        int y2 = y*tileSize + tileSize/2 - 2;
        if(pathAbove) {
            g2d.drawLine(x1, y1, x1 + tileSize, y1);
        } else {
            g2d.drawLine(x1, y1, x1, y1 + tileSize);
        }
        g2d.drawArc(x2,
                y2,
                tileSize + 2,
                tileSize + 2,
                90,
                90);
    }

    private void drawDoubleInnerDownLeftCorner(boolean outOfBoundsAbove, Graphics2D g2d, int x, int y) {
        int x1 = x*tileSize + tileSize - 1;
        int x2 = x*tileSize - tileSize/2;
        int y1 = y*tileSize;
        int y2 = y*tileSize + tileSize/2 - 2;
        if(outOfBoundsAbove) {
            g2d.drawLine(x1 - tileSize + 1, y1, x1, y1);
        } else {
            g2d.drawLine(x1, y1, x1, y1 + tileSize);
        }
        g2d.drawArc(x2,
                y2,
                tileSize + 2,
                tileSize + 2,
                0,
                90);
    }

    private void drawDoubleInnerUpLeftCorner(boolean outOfBoundsAbove, Graphics2D g2d, int x, int y) {
        int x1 = x*tileSize + tileSize - 1;
        int x2 = x*tileSize - tileSize/2;
        int y1 = y*tileSize;
        int y2 = y*tileSize - tileSize/2;
        if(outOfBoundsAbove) {
            g2d.drawLine(x1, y1, x1 + tileSize, y1);
        } else {
            g2d.drawLine(x1, y1, x1, y1 + tileSize);
        }
        g2d.drawArc(x2,
                y2,
                tileSize + 2,
                tileSize + 2,
                0,
                -90);
    }

    private void drawDoubleInnerUpRightCorner(boolean outOfBoundsAbove, Graphics2D g2d, int x, int y) {
        int x1 = x*tileSize;
        int x2 = x*tileSize + tileSize/2 - 2;
        int y1 = y*tileSize;
        int y2 = y*tileSize - tileSize/2 - 1;
        if(outOfBoundsAbove) {
            g2d.drawLine(x1, y1, x1 + tileSize, y1);
        } else {
            g2d.drawLine(x1, y1, x1, y1 + tileSize);
        }
        g2d.drawArc(x2,
                y2,
                tileSize + 2,
                tileSize + 2,
                -90,
                -90);
    }

    private void drawSimpleHorizontalWall(boolean pathAbove, Graphics2D g2d, int x, int y) {
        int y1;
        if(pathAbove) {
            y1 = y*tileSize + tileSize/2 + 2;
        } else {
            y1 = y*tileSize + tileSize/2 - 2;
        }
        g2d.drawLine(x*tileSize,y1,(x+1)*tileSize,y1);
    }

    private void drawSimpleVerticalWall(boolean pathLeft, Graphics2D g2d, int x, int y) {
        int x1;
        if(pathLeft) {
            x1 = x*tileSize + tileSize/2 + 2;
        } else {
            x1 = x*tileSize + tileSize/2 - 2;
        }
        g2d.drawLine(x1,y*tileSize,x1,(y+1)*tileSize);
    }

    private void drawWallShape(int x, int y, Graphics2D g2d) {
        Map<String, TileType> neighbours = new HashMap<>();
        neighbours.put("above",gameMap.getTile(new Point(x, y-1)));
        neighbours.put("left",gameMap.getTile(new Point(x-1, y)));
        neighbours.put("below",gameMap.getTile(new Point(x, y+1)));

        g2d.setColor(Color.BLUE);
        switch(gameMap.getTile(new Point(x,y))){
            case DOUBLEHORIZONTALWALL ->
                drawDoubleHorizontalWall(
                        neighbours.get("above")==TileType.PATH,
                        g2d,
                        x,
                        y);
            case DOUBLEVERTICALWALL ->
                drawDoubleVerticalWall(
                        neighbours.get("left")==TileType.PATH,
                        g2d,
                        x,
                        y);
            case DOUBLEOUTERDOWNRIGHTCORNER ->
                drawDoubleOuterDownRightCorner(
                        neighbours.get("above")==TileType.PATH,
                        g2d,
                        x,
                        y);
            case DOUBLEOUTERDOWNLEFTCORNER ->
                drawDoubleOuterDownLeftCorner(
                        neighbours.get("above")==TileType.PATH,
                        g2d,
                        x,
                        y);
            case DOUBLEOUTERUPLEFTCORNER ->
                drawDoubleOuterUpLeftCorner(
                        neighbours.get("below")==TileType.PATH,
                        g2d,
                        x,
                        y);
            case DOUBLEOUTERUPRIGHTCORNER ->
                drawDoubleOuterUpRightCorner(
                        neighbours.get("below")==TileType.PATH,
                        g2d,
                        x,
                        y);
            case DOUBLEINNERDOWNRIGHTCORNER ->
                drawDoubleInnerDownRightCorner(
                        neighbours.get("above")==TileType.OUTOFBOUNDS,
                        g2d,
                        x,
                        y);
            case DOUBLEINNERDOWNLEFTCORNER ->
                drawDoubleInnerDownLeftCorner(
                        neighbours.get("above")==TileType.OUTOFBOUNDS,
                        g2d,
                        x,
                        y);
            case DOUBLEINNERUPLEFTCORNER ->
                drawDoubleInnerUpLeftCorner(
                        neighbours.get("above")==TileType.OUTOFBOUNDS,
                        g2d,
                        x,
                        y);
            case DOUBLEINNERUPRIGHTCORNER ->
                drawDoubleInnerUpRightCorner(
                        neighbours.get("above")==TileType.OUTOFBOUNDS,
                        g2d,
                        x,
                        y);
            case SIMPLEHORIZONTALWALL ->
                drawSimpleHorizontalWall(
                        neighbours.get("above")==TileType.PATH,
                        g2d,
                        x,
                        y);
            case SIMPLEVERTICALWALL ->
                drawSimpleVerticalWall(
                        neighbours.get("left")==TileType.PATH,
                        g2d,
                        x,
                        y);
            case SIMPLEOUTERDOWNRIGHTCORNER ->
                g2d.drawArc(x*tileSize + tileSize/2 + 2,
                        y*tileSize + tileSize/2 + 2,
                        tileSize-2,
                        tileSize-2,
                        180,
                        -90);
            case SIMPLEOUTERDOWNLEFTCORNER ->
                g2d.drawArc(x*tileSize - tileSize/2,
                        y*tileSize + tileSize/2 + 2,
                        tileSize-2,
                        tileSize-2,
                        0,
                        90);
            case SIMPLEOUTERUPLEFTCORNER ->
            g2d.drawArc(x*tileSize - tileSize/2,
                    y*tileSize - tileSize/2,
                    tileSize-2,
                    tileSize-2,
                    0,
                    -90);
            case SIMPLEOUTERUPRIGHTCORNER ->
                g2d.drawArc(x*tileSize + tileSize/2 + 2,
                        y*tileSize - tileSize/2,
                        tileSize-2,
                        tileSize-2,
                        180,
                        90);
            case SIMPLEINNERDOWNRIGHTCORNER ->
                g2d.drawArc(x*tileSize + tileSize/2 - 2,
                        y*tileSize + tileSize/2 - 2,
                        tileSize+2,
                        tileSize+2,
                        180,
                        -90);
            case SIMPLEINNERDOWNLEFTCORNER ->
                g2d.drawArc(x*tileSize - tileSize/2,
                        y*tileSize + tileSize/2 - 2,
                        tileSize+2,
                        tileSize+2,
                        0,
                        90);
            case SIMPLEINNERUPLEFTCORNER ->
                g2d.drawArc(x*tileSize - tileSize/2,
                        y*tileSize - tileSize/2,
                        tileSize+2,
                        tileSize+2,
                        0,
                        -90);
            case SIMPLEINNERUPRIGHTCORNER ->
                g2d.drawArc(x*tileSize + tileSize/2 - 2,
                        y*tileSize - tileSize/2,
                        tileSize+2,
                        tileSize+2,
                        180,
                        90);
        }
    }

    private void drawPacMan(Graphics2D g2d) {
        int mouthStartAngle;
        g2d.setColor(Color.YELLOW);
        if(pacMan.getOrientation() == Orientation.UP) {
            mouthStartAngle = 90;
        } else if (pacMan.getOrientation() == Orientation.RIGHT) {
            mouthStartAngle = 0;
        } else if (pacMan.getOrientation() == Orientation.DOWN) {
            mouthStartAngle = -90;
        } else {
            mouthStartAngle = 180;
        }
        g2d.fillArc(pacMan.getPosition().x,
                pacMan.getPosition().y,
                pacMan.getSize(),
                pacMan.getSize(),
                mouthStartAngle + pacMan.getCurrentMouthAngle()/2,
                360 - pacMan.getCurrentMouthAngle());
    }

    private void updatePacMan() {
        if (gameKeyHandler.isUpPressed()) {
            pacMan.changeOrientation(Orientation.UP);
            updatePacManPosition();
        } else if (gameKeyHandler.isRightPressed()) {
            pacMan.changeOrientation(Orientation.RIGHT);
            updatePacManPosition();
        } else if (gameKeyHandler.isDownPressed()) {
            pacMan.changeOrientation(Orientation.DOWN);
            updatePacManPosition();
        } else if (gameKeyHandler.isLeftPressed()) {
            pacMan.changeOrientation(Orientation.LEFT);
            updatePacManPosition();
        }
        pacMan.animateMouth();
    }

    private void updatePacManPosition() {
        pacMan.move();
        Point upperLeftTile = new Point(
                (pacMan.getHitBox().x / tileSize),
                (pacMan.getHitBox().y / tileSize));
        TileType upperLeftTileType = gameMap.getTile(
                new Point(
                        pacMan.getHitBox().x / tileSize,
                        pacMan.getHitBox().y / tileSize)
        );
        Point upperRightTile = new Point(
                ((pacMan.getHitBox().x + pacMan.getHitBox().width-1) / tileSize),
                (pacMan.getHitBox().y / tileSize));
        TileType upperRightTileType = gameMap.getTile(
                new Point(
                        (pacMan.getHitBox().x + pacMan.getHitBox().width-1)/ tileSize,
                        pacMan.getHitBox().y / tileSize)
        );
        Point lowerLeftTile = new Point(
                (pacMan.getHitBox().x / tileSize),
                ((pacMan.getHitBox().y + pacMan.getHitBox().height-1) / tileSize));
        TileType lowerLeftTileType = gameMap.getTile(
                new Point(
                        pacMan.getHitBox().x / tileSize,
                        (pacMan.getHitBox().y + pacMan.getHitBox().height-1) / tileSize)
        );
        Point lowerRightTile = new Point(
                ((pacMan.getHitBox().x + pacMan.getHitBox().width-1) / tileSize),
                ((pacMan.getHitBox().y + pacMan.getHitBox().height-1) / tileSize));
        TileType lowerRightTileType = gameMap.getTile(
                new Point(
                        (pacMan.getHitBox().x + pacMan.getHitBox().width-1) / tileSize,
                        (pacMan.getHitBox().y + pacMan.getHitBox().height-1) / tileSize)
        );

        if (upperLeftTileType != TileType.PATH) {
            pacMan.bumpOutOfCollision(upperLeftTile);
        } else if (upperRightTileType != TileType.PATH) {
            pacMan.bumpOutOfCollision(upperRightTile);
        } else if (lowerLeftTileType != TileType.PATH) {
            pacMan.bumpOutOfCollision(lowerLeftTile);
        } else if (lowerRightTileType != TileType.PATH) {
            pacMan.bumpOutOfCollision(lowerRightTile);
        }
    }

    public void update() {
        updatePacMan();
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
