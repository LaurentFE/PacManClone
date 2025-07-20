package fr.LaurentFE.pacManClone;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class GamePanel extends JPanel implements Runnable {

    private final GameKeyHandler gameKeyHandler;
    private Thread gameThread;


    private final int tileSize;
    private final GameMap gameMap;
    private final PacMan pacMan;
    private final Set<Ghost> ghosts;

    public GamePanel(GameMap gameMap) {
        tileSize = 32;
        int moveSpeed = tileSize/8;
        Orientation defaultOrientation = Orientation.RIGHT;
        pacMan = new PacMan(
                new Point(tileSize*13,tileSize*26),
                tileSize,
                tileSize,
                defaultOrientation,
                moveSpeed);
        Ghost blinky = new Ghost(
                new Point(tileSize*12, tileSize*16),
                tileSize,
                tileSize,
                defaultOrientation,
                moveSpeed,
                Color.RED);
        Ghost pinky = new Ghost(
                new Point(tileSize*13, tileSize*16),
                tileSize,
                tileSize,
                defaultOrientation,
                moveSpeed,
                Color.PINK);
        Ghost inky = new Ghost(
                new Point(tileSize*14, tileSize*16),
                tileSize,
                tileSize,
                defaultOrientation,
                moveSpeed,
                Color.BLUE);
        Ghost clyde = new Ghost(
                new Point(tileSize*15, tileSize*16),
                tileSize,
                tileSize,
                defaultOrientation,
                moveSpeed,
                Color.ORANGE);
        Ghost[] ghostsArray = new Ghost[]{blinky, pinky, inky, clyde};
        ghosts = new HashSet<>(Arrays.asList(ghostsArray));
        this.gameMap = gameMap;
        setPreferredSize(new Dimension(gameMap.getMapWidthTile()*tileSize, gameMap.getMapHeightTile()*tileSize));
        setBackground(Color.BLACK);
        setDoubleBuffered(true); // Render is made on a second panel, then copied to the main widow => smoother rendering
        gameKeyHandler = new GameKeyHandler(defaultOrientation);
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
        for(Ghost ghost: ghosts) {
            drawGhost(g2d, ghost);
        }
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

    private void drawGhost(Graphics2D g2d, Ghost ghost) {
        g2d.setColor(ghost.getColor());
        g2d.fillArc(ghost.getPosition().x + ghost.getSize()/16,
                ghost.getPosition().y + ghost.getSize()/16,
                ghost.getSize() -  ghost.getSize()/8,
                3*ghost.getSize()/4,
                0,
                180);
        g2d.fillRect(ghost.getPosition().x + ghost.getSize()/16,
                ghost.getPosition().y + 3*ghost.getSize()/8,
                14*ghost.getSize()/16,
                7*ghost.getSize()/16);
        drawGhostSkirt(g2d, ghost);
        drawGhostEyes(g2d, ghost);
    }

    private void drawGhostSkirt(Graphics2D g2d, Ghost ghost) {
        int[] x1 = new int[]{ghost.getPosition().x + ghost.getSize()/16,
                ghost.getPosition().x + ghost.getSize()/16,
                ghost.getPosition().x + 3*ghost.getSize()/16};
        int[] x2 = new int[]{ghost.getPosition().x + 4*ghost.getSize()/16,
                ghost.getPosition().x + 6*ghost.getSize()/16,
                ghost.getPosition().x + 6*ghost.getSize()/16};
        int[] x3 = new int[]{ghost.getPosition().x + 10*ghost.getSize()/16,
                ghost.getPosition().x + 10*ghost.getSize()/16,
                ghost.getPosition().x + 12*ghost.getSize()/16};
        int[] x4 = new int[]{ghost.getPosition().x + 13*ghost.getSize()/16,
                ghost.getPosition().x + 15*ghost.getSize()/16,
                ghost.getPosition().x + 15*ghost.getSize()/16};
        int[] y1 = new int[]{ghost.getPosition().y + 13*ghost.getSize()/16,
                ghost.getPosition().y + 15*ghost.getSize()/16,
                ghost.getPosition().y + 13*ghost.getSize()/16};
        int[] y2 = new int[]{ghost.getPosition().y + 13*ghost.getSize()/16,
                ghost.getPosition().y + 13*ghost.getSize()/16,
                ghost.getPosition().y + 15*ghost.getSize()/16};
        g2d.fillPolygon(x1, y1, 3);
        g2d.fillPolygon(x2, y2, 3);
        g2d.fillRect(x2[2], y1[0], ghost.getSize()/16, 2*ghost.getSize()/16);
        g2d.fillRect(x2[2] + 3*ghost.getSize()/16, y1[0], ghost.getSize()/16, 2*ghost.getSize()/16);
        g2d.fillPolygon(x3, y1, 3);
        g2d.fillPolygon(x4, y2, 3);
    }

    private void drawGhostEyes(Graphics2D g2d, Ghost ghost) {
        g2d.setColor(Color.WHITE);
        int eyeHeight = 6*ghost.getSize()/16;
        int eyeWidth = 4*ghost.getSize()/16;
        int pupilSize = 2*ghost.getSize()/16;
        Point leftEyePosition = new Point(ghost.getPosition().x + 4*ghost.getSize()/16, ghost.getPosition().y + 4*ghost.getSize()/16);
        Point rightEyeOffset = new Point(ghost.getSize()/16 + eyeWidth, 0);
        Point leftPupilPosition = new Point(ghost.getPosition().x + 6*ghost.getSize()/16, ghost.getPosition().y + 6*ghost.getSize()/16);
        Point[] orientationOffsets = getEyeAndPupilOrientationOffset(ghost.getOrientation(), ghost.getSize());

        leftEyePosition.x += orientationOffsets[0].x;
        leftEyePosition.y += orientationOffsets[0].y;
        leftPupilPosition.x += orientationOffsets[1].x;
        leftPupilPosition.y += orientationOffsets[1].y;

        g2d.fillArc(leftEyePosition.x,
                leftEyePosition.y,
                eyeWidth, eyeHeight, 0, 360);
        g2d.fillArc(leftEyePosition.x + rightEyeOffset.x,
                leftEyePosition.y + rightEyeOffset.y,
                eyeWidth, eyeHeight, 0, 360);
        g2d.setColor(Color.BLUE);
        g2d.fillArc(leftPupilPosition.x,
                leftPupilPosition.y,
                pupilSize, pupilSize, 0, 360);
        g2d.fillArc(leftPupilPosition.x + rightEyeOffset.x,
                leftPupilPosition.y + rightEyeOffset.y,
                pupilSize, pupilSize, 0, 360);

    }

    private Point[] getEyeAndPupilOrientationOffset(Orientation orientation, int ghostSize) {
        Point eyeOrientationOffset;
        Point pupilOrientationOffset;
        if (orientation == Orientation.UP) {
            eyeOrientationOffset = new Point(-ghostSize/16, -ghostSize/8);
            pupilOrientationOffset = new Point(-ghostSize/8, -ghostSize/4);
        } else if (orientation == Orientation.LEFT) {
            eyeOrientationOffset = new Point(-ghostSize/8, 0);
            pupilOrientationOffset = new Point(-ghostSize/4, 0);
        } else if (orientation == Orientation.DOWN) {
            eyeOrientationOffset = new Point(-ghostSize/16, ghostSize/16);
            pupilOrientationOffset = new Point(-2*ghostSize/16, 3*ghostSize/16);
        } else {
            eyeOrientationOffset = new Point(0,0);
            pupilOrientationOffset = new Point(0, 0);
        }
        return new Point[]{eyeOrientationOffset, pupilOrientationOffset};
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

    private Rectangle getNextPathTileForOrientation() {
        Point directionModifier;
        Orientation orientation = gameKeyHandler.getNextOrientation();
        if (orientation == Orientation.UP) {
            directionModifier = new Point(0, -1);
        } else if (orientation == Orientation.LEFT) {
            directionModifier = new Point(-1, 0);
        } else if (orientation == Orientation.DOWN) {
            directionModifier = new Point(0, 1);
        } else {
            directionModifier = new Point(1, 0);
        }
        Point tileAPosition = new Point(
                pacMan.getPosition().x / tileSize + directionModifier.x,
                pacMan.getPosition().y / tileSize + directionModifier.y);
        Point tileBPosition = new Point(
                pacMan.getPosition().x / tileSize + directionModifier.x,
                pacMan.getPosition().y / tileSize + directionModifier.y);
        Point tileCPosition = new Point(
                pacMan.getPosition().x / tileSize + directionModifier.x,
                pacMan.getPosition().y / tileSize + directionModifier.y);

        if (orientation == Orientation.UP || orientation == Orientation.DOWN) {
            tileAPosition.x -= 1;
            tileCPosition.x += 1;
        } else {
            tileAPosition.y -= 1;
            tileCPosition.y += 1;
        }

        if (gameMap.getTile(tileAPosition) == TileType.PATH) {
            return new Rectangle(
                    tileAPosition.x * tileSize,
                    tileAPosition.y * tileSize,
                    tileSize,
                    tileSize
            );
        } else if (gameMap.getTile(tileBPosition) == TileType.PATH) {
            return new Rectangle(
                    tileBPosition.x * tileSize,
                    tileBPosition.y * tileSize,
                    tileSize,
                    tileSize
            );
        } else if (gameMap.getTile(tileCPosition) == TileType.PATH) {
            return new Rectangle(
                    tileCPosition.x * tileSize,
                    tileCPosition.y * tileSize,
                    tileSize,
                    tileSize
            );
        } else {
            return new Rectangle();
        }
    }

    private boolean canPacManGetIntoPath() {
        Rectangle pathTile = getNextPathTileForOrientation();
        if (pathTile.equals(new Rectangle()))
            return false;

        if (gameKeyHandler.getNextOrientation() == Orientation.UP
                || gameKeyHandler.getNextOrientation() == Orientation.DOWN) {
            if (pathTile.x - pacMan.getHitBox().x < pacMan.getMoveSpeed()
                    && pathTile.x - pacMan.getHitBox().x > -pacMan.getMoveSpeed()) {
                pacMan.setX(pathTile.x);
                return true;
            }
        } else {
            if (pathTile.y - pacMan.getHitBox().y < pacMan.getMoveSpeed()
                    && pathTile.y - pacMan.getHitBox().y > -pacMan.getMoveSpeed()) {
                pacMan.setY(pathTile.y);
                return true;
            }
        }
        return false;
    }

    private boolean tryToChangePacManDirection() {
        if (gameKeyHandler.getNextOrientation() == pacMan.getOrientation())
            return false;

        if (canPacManGetIntoPath()) {
            pacMan.setOrientation(gameKeyHandler.getNextOrientation());
            updatePacManPosition();
            return true;
        }
        return false;
    }

    private void updatePacMan() {
        if (!tryToChangePacManDirection()) {
            updatePacManPosition();
        }
    }

    private void updatePacManPosition() {
        pacMan.move();
        Point upperLeftTile = new Point(
                (pacMan.getHitBox().x / tileSize),
                (pacMan.getHitBox().y / tileSize));
        TileType upperLeftTileType = gameMap.getTile(upperLeftTile);
        Point upperRightTile = new Point(
                ((pacMan.getHitBox().x + pacMan.getHitBox().width-1) / tileSize),
                (pacMan.getHitBox().y / tileSize));
        TileType upperRightTileType = gameMap.getTile(upperRightTile);
        Point lowerLeftTile = new Point(
                (pacMan.getHitBox().x / tileSize),
                ((pacMan.getHitBox().y + pacMan.getHitBox().height-1) / tileSize));
        TileType lowerLeftTileType = gameMap.getTile(lowerLeftTile);
        Point lowerRightTile = new Point(
                ((pacMan.getHitBox().x + pacMan.getHitBox().width-1) / tileSize),
                ((pacMan.getHitBox().y + pacMan.getHitBox().height-1) / tileSize));
        TileType lowerRightTileType = gameMap.getTile(lowerRightTile);

        if (upperLeftTileType != TileType.PATH) {
            pacMan.bumpOutOfCollision(upperLeftTile);
        } else if (upperRightTileType != TileType.PATH) {
            pacMan.bumpOutOfCollision(upperRightTile);
        } else if (lowerLeftTileType != TileType.PATH) {
            pacMan.bumpOutOfCollision(lowerLeftTile);
        } else if (lowerRightTileType != TileType.PATH) {
            pacMan.bumpOutOfCollision(lowerRightTile);
        } else {
            pacMan.animateMouth();
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
