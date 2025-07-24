package fr.LaurentFE.pacManClone;

import fr.LaurentFE.pacManClone.ghost.Ghost;
import fr.LaurentFE.pacManClone.ghost.GhostState;
import fr.LaurentFE.pacManClone.ghost.personality.Blinky;
import fr.LaurentFE.pacManClone.ghost.personality.Clyde;
import fr.LaurentFE.pacManClone.ghost.personality.Inky;
import fr.LaurentFE.pacManClone.ghost.personality.Pinky;

import javax.swing.*;
import java.awt.*;
import java.util.*;


public class GamePanel extends JPanel implements Runnable {

    private final GameKeyHandler gameKeyHandler;
    private Thread gameThread;

    public static final int TILE_SIZE = 32;
    private static final Orientation DEFAULT_ORIENTATION = Orientation.RIGHT;
    private static final int MOVE_SPEED = TILE_SIZE /8;
    public static final PacMan PAC_MAN = new PacMan(
            new Point(TILE_SIZE *13, TILE_SIZE *26),
            DEFAULT_ORIENTATION,
            MOVE_SPEED);
    public static final Ghost BLINKY = new Ghost(
            new Point(TILE_SIZE *9, TILE_SIZE *14),
            DEFAULT_ORIENTATION,
            MOVE_SPEED,
            Color.RED,
            new Blinky(),
            new Point(0,0));
    public static final Ghost PINKY = new Ghost(
            new Point(TILE_SIZE *18, TILE_SIZE *14),
            DEFAULT_ORIENTATION,
            MOVE_SPEED,
            Color.PINK,
            new Pinky(),
            new Point(GameMap.getInstance().getMapWidthTile() - 1, 0));
    public static final Ghost INKY = new Ghost(
            new Point(TILE_SIZE *12, TILE_SIZE *14),
            DEFAULT_ORIENTATION,
            MOVE_SPEED,
            Color.CYAN,
            new Inky(),
            new Point(GameMap.getInstance().getMapWidthTile() - 1, GameMap.getInstance().getMapHeightTile() - 1));
    public static final Ghost CLYDE = new Ghost(
            new Point(TILE_SIZE *15, TILE_SIZE *14),
            DEFAULT_ORIENTATION,
            MOVE_SPEED,
            Color.ORANGE,
            new Clyde(),
            new Point(0, GameMap.getInstance().getMapHeightTile() - 1));

    private final GameMap gameMap;

    public GamePanel() {
        this.gameMap = GameMap.getInstance();
        setPreferredSize(new Dimension(gameMap.getMapWidthTile()* TILE_SIZE, gameMap.getMapHeightTile()* TILE_SIZE));
        setBackground(Color.BLACK);
        setDoubleBuffered(true); // Render is made on a second panel, then copied to the main widow => smoother rendering
        gameKeyHandler = new GameKeyHandler(DEFAULT_ORIENTATION);
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
        drawGhost(g2d, BLINKY);
        drawGhost(g2d, INKY);
        drawGhost(g2d, PINKY);
        drawGhost(g2d, CLYDE);

    }

    private void drawMap(Graphics2D g2d) {
        for (int y = 0; y < gameMap.getMapHeightTile(); y++) {
            for (int x = 0; x < gameMap.getMapWidthTile(); x++) {
                TileType currentTile = gameMap.getTile(new Point(x,y));
                if (currentTile == TileType.PATH
                        || currentTile == TileType.OUTOFBOUNDS) {
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(x* TILE_SIZE,
                            y* TILE_SIZE,
                            TILE_SIZE,
                            TILE_SIZE);
                } else if (currentTile == TileType.DOOR) {
                    g2d.setColor(Color.PINK);
                    g2d.fillRect(x* TILE_SIZE,
                            y* TILE_SIZE + TILE_SIZE /2 + TILE_SIZE /4,
                            TILE_SIZE,
                            TILE_SIZE /8);
                } else if (currentTile != TileType.UNDEFINED) {
                    drawWallShape(x,y,g2d);
                }
            }
        }
    }

    private void drawDoubleHorizontalWall(boolean pathAbove, Graphics2D g2d, int x, int y) {
        int y1, y2;
        if (pathAbove) {
            y1 = y* TILE_SIZE + TILE_SIZE /2 + 2;
            y2 = y* TILE_SIZE + TILE_SIZE - 1;
        } else {
            y1 = y* TILE_SIZE;
            y2 = y* TILE_SIZE + TILE_SIZE /2 - 2;
        }
        g2d.drawLine(x* TILE_SIZE,y1,(x+1)* TILE_SIZE,y1);
        g2d.drawLine(x* TILE_SIZE,y2,(x+1)* TILE_SIZE,y2);
    }

    private void drawDoubleVerticalWall(boolean pathLeft, Graphics2D g2d, int x, int y) {
        int x1, x2;
        if(pathLeft) {
            x1 = x* TILE_SIZE + TILE_SIZE /2 + 2;
            x2 = x* TILE_SIZE + TILE_SIZE - 1;
        } else {
            x1 = x* TILE_SIZE;
            x2 = x* TILE_SIZE + TILE_SIZE /2 - 2;
        }
        g2d.drawLine(x1,y* TILE_SIZE,x1,(y+1)* TILE_SIZE);
        g2d.drawLine(x2,y* TILE_SIZE,x2,(y+1)* TILE_SIZE);
    }

    private void drawDoubleOuterDownRightCorner(boolean pathAbove, Graphics2D g2d, int x, int y) {
        int x1, x2;
        int y1, y2;
        if(pathAbove) {
            x1 = x* TILE_SIZE + TILE_SIZE /2 + 2;
            y1 = y* TILE_SIZE + TILE_SIZE /2 + 2;
            g2d.drawArc(x1,
                    y1,
                    TILE_SIZE,
                    TILE_SIZE,
                    90,
                    90);
        } else {
            x1 = x* TILE_SIZE;
            x2 = x* TILE_SIZE + TILE_SIZE /2 - 2;
            y1 = y* TILE_SIZE;
            y2 = y* TILE_SIZE + TILE_SIZE /2 - 2;
            g2d.drawArc(x1,
                    y1,
                    TILE_SIZE *2,
                    TILE_SIZE *2,
                    90,
                    90);
            g2d.drawArc(x2,
                    y2,
                    TILE_SIZE + 2,
                    TILE_SIZE + 2,
                    90,
                    90);
        }
    }

    private void drawDoubleOuterDownLeftCorner(boolean pathAbove, Graphics2D g2d, int x, int y) {
        int x1, x2;
        int y1, y2;
        if(pathAbove) {
            x1 = x* TILE_SIZE - TILE_SIZE /2 - 2;
            y1 = y* TILE_SIZE + TILE_SIZE /2 + 2;
            g2d.drawArc(x1,
                    y1,
                    TILE_SIZE,
                    TILE_SIZE,
                    0,
                    90);
        } else {
            x1 = x* TILE_SIZE - TILE_SIZE;
            x2 = x* TILE_SIZE - TILE_SIZE /2;
            y1 = y* TILE_SIZE;
            y2 = y* TILE_SIZE + TILE_SIZE /2 - 2;
            g2d.drawArc(x1,
                    y1,
                    TILE_SIZE *2,
                    TILE_SIZE *2,
                    0,
                    90);
            g2d.drawArc(x2,
                    y2,
                    TILE_SIZE + 2,
                    TILE_SIZE + 2,
                    0,
                    90);
        }
    }

    private void drawDoubleOuterUpLeftCorner(boolean pathAbove, Graphics2D g2d, int x, int y) {
        int x1, x2;
        int y1, y2;
        if(pathAbove) {
            x1 = x* TILE_SIZE - TILE_SIZE /2 - 2;
            y1 = y* TILE_SIZE - TILE_SIZE /2 - 2;
            g2d.drawArc(x1,
                    y1,
                    TILE_SIZE,
                    TILE_SIZE,
                    0,
                    -90);
        } else {
            x1 = x* TILE_SIZE - TILE_SIZE;
            x2 = x* TILE_SIZE - TILE_SIZE /2;
            y1 = y* TILE_SIZE - TILE_SIZE - 1;
            y2 = y* TILE_SIZE - TILE_SIZE /2;
            g2d.drawArc(x1,
                    y1,
                    TILE_SIZE *2,
                    TILE_SIZE *2,
                    0,
                    -90);
            g2d.drawArc(x2,
                    y2,
                    TILE_SIZE + 2,
                    TILE_SIZE + 2,
                    0,
                    -90);
        }
    }

    private void drawDoubleOuterUpRightCorner(boolean pathBelow, Graphics2D g2d, int x, int y) {
        int x1, x2;
        int y1, y2;
        if(pathBelow) {
            x1 = x* TILE_SIZE + TILE_SIZE /2 + 2;
            y1 = y* TILE_SIZE - TILE_SIZE /2 - 2;
            g2d.drawArc(x1,
                    y1,
                    TILE_SIZE,
                    TILE_SIZE,
                    -90,
                    -90);
        } else {
            x1 = x* TILE_SIZE;
            x2 = x* TILE_SIZE + TILE_SIZE /2 - 2;
            y1 = y* TILE_SIZE - TILE_SIZE - 1;
            y2 = y* TILE_SIZE - TILE_SIZE /2 - 1;
            g2d.drawArc(x1,
                    y1,
                    TILE_SIZE *2,
                    TILE_SIZE *2,
                    -90,
                    -90);
            g2d.drawArc(x2,
                    y2,
                    TILE_SIZE + 2,
                    TILE_SIZE + 2,
                    -90,
                    -90);
        }
    }

    private void drawDoubleInnerDownRightCorner(boolean pathAbove, Graphics2D g2d, int x, int y) {
        int x1 = x* TILE_SIZE;
        int x2 = x* TILE_SIZE + TILE_SIZE /2 - 2;
        int y1 = y* TILE_SIZE;
        int y2 = y* TILE_SIZE + TILE_SIZE /2 - 2;
        if(pathAbove) {
            g2d.drawLine(x1, y1, x1 + TILE_SIZE, y1);
        } else {
            g2d.drawLine(x1, y1, x1, y1 + TILE_SIZE);
        }
        g2d.drawArc(x2,
                y2,
                TILE_SIZE + 2,
                TILE_SIZE + 2,
                90,
                90);
    }

    private void drawDoubleInnerDownLeftCorner(boolean outOfBoundsAbove, Graphics2D g2d, int x, int y) {
        int x1 = x* TILE_SIZE + TILE_SIZE - 1;
        int x2 = x* TILE_SIZE - TILE_SIZE /2;
        int y1 = y* TILE_SIZE;
        int y2 = y* TILE_SIZE + TILE_SIZE /2 - 2;
        if(outOfBoundsAbove) {
            g2d.drawLine(x1 - TILE_SIZE + 1, y1, x1, y1);
        } else {
            g2d.drawLine(x1, y1, x1, y1 + TILE_SIZE);
        }
        g2d.drawArc(x2,
                y2,
                TILE_SIZE + 2,
                TILE_SIZE + 2,
                0,
                90);
    }

    private void drawDoubleInnerUpLeftCorner(boolean outOfBoundsAbove, Graphics2D g2d, int x, int y) {
        int x1 = x* TILE_SIZE + TILE_SIZE - 1;
        int x2 = x* TILE_SIZE - TILE_SIZE /2;
        int y1 = y* TILE_SIZE;
        int y2 = y* TILE_SIZE - TILE_SIZE /2;
        if(outOfBoundsAbove) {
            g2d.drawLine(x1, y1, x1 + TILE_SIZE, y1);
        } else {
            g2d.drawLine(x1, y1, x1, y1 + TILE_SIZE);
        }
        g2d.drawArc(x2,
                y2,
                TILE_SIZE + 2,
                TILE_SIZE + 2,
                0,
                -90);
    }

    private void drawDoubleInnerUpRightCorner(boolean outOfBoundsAbove, Graphics2D g2d, int x, int y) {
        int x1 = x* TILE_SIZE;
        int x2 = x* TILE_SIZE + TILE_SIZE /2 - 2;
        int y1 = y* TILE_SIZE;
        int y2 = y* TILE_SIZE - TILE_SIZE /2 - 1;
        if(outOfBoundsAbove) {
            g2d.drawLine(x1, y1, x1 + TILE_SIZE, y1);
        } else {
            g2d.drawLine(x1, y1, x1, y1 + TILE_SIZE);
        }
        g2d.drawArc(x2,
                y2,
                TILE_SIZE + 2,
                TILE_SIZE + 2,
                -90,
                -90);
    }

    private void drawSimpleHorizontalWall(boolean pathAbove, Graphics2D g2d, int x, int y) {
        int y1;
        if(pathAbove) {
            y1 = y* TILE_SIZE + TILE_SIZE /2 + 2;
        } else {
            y1 = y* TILE_SIZE + TILE_SIZE /2 - 2;
        }
        g2d.drawLine(x* TILE_SIZE,y1,(x+1)* TILE_SIZE,y1);
    }

    private void drawSimpleVerticalWall(boolean pathLeft, Graphics2D g2d, int x, int y) {
        int x1;
        if(pathLeft) {
            x1 = x* TILE_SIZE + TILE_SIZE /2 + 2;
        } else {
            x1 = x* TILE_SIZE + TILE_SIZE /2 - 2;
        }
        g2d.drawLine(x1,y* TILE_SIZE,x1,(y+1)* TILE_SIZE);
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
                g2d.drawArc(x* TILE_SIZE + TILE_SIZE /2 + 2,
                        y* TILE_SIZE + TILE_SIZE /2 + 2,
                        TILE_SIZE -2,
                        TILE_SIZE -2,
                        180,
                        -90);
            case SIMPLEOUTERDOWNLEFTCORNER ->
                g2d.drawArc(x* TILE_SIZE - TILE_SIZE /2,
                        y* TILE_SIZE + TILE_SIZE /2 + 2,
                        TILE_SIZE -2,
                        TILE_SIZE -2,
                        0,
                        90);
            case SIMPLEOUTERUPLEFTCORNER ->
            g2d.drawArc(x* TILE_SIZE - TILE_SIZE /2,
                    y* TILE_SIZE - TILE_SIZE /2,
                    TILE_SIZE -2,
                    TILE_SIZE -2,
                    0,
                    -90);
            case SIMPLEOUTERUPRIGHTCORNER ->
                g2d.drawArc(x* TILE_SIZE + TILE_SIZE /2 + 2,
                        y* TILE_SIZE - TILE_SIZE /2,
                        TILE_SIZE -2,
                        TILE_SIZE -2,
                        180,
                        90);
            case SIMPLEINNERDOWNRIGHTCORNER ->
                g2d.drawArc(x* TILE_SIZE + TILE_SIZE /2 - 2,
                        y* TILE_SIZE + TILE_SIZE /2 - 2,
                        TILE_SIZE +2,
                        TILE_SIZE +2,
                        180,
                        -90);
            case SIMPLEINNERDOWNLEFTCORNER ->
                g2d.drawArc(x* TILE_SIZE - TILE_SIZE /2,
                        y* TILE_SIZE + TILE_SIZE /2 - 2,
                        TILE_SIZE +2,
                        TILE_SIZE +2,
                        0,
                        90);
            case SIMPLEINNERUPLEFTCORNER ->
                g2d.drawArc(x* TILE_SIZE - TILE_SIZE /2,
                        y* TILE_SIZE - TILE_SIZE /2,
                        TILE_SIZE +2,
                        TILE_SIZE +2,
                        0,
                        -90);
            case SIMPLEINNERUPRIGHTCORNER ->
                g2d.drawArc(x* TILE_SIZE + TILE_SIZE /2 - 2,
                        y* TILE_SIZE - TILE_SIZE /2,
                        TILE_SIZE +2,
                        TILE_SIZE +2,
                        180,
                        90);
        }
    }

    private void drawGhost(Graphics2D g2d, Ghost ghost) {
        if (ghost.getState() == GhostState.EATEN) {
            drawGhostEyes(g2d, ghost);
        } else {
            if (ghost.getState() == GhostState.FRIGHTENED) {
                g2d.setColor(Color.BLUE);
                drawGhostBody(g2d,ghost);
                drawGhostSkirt(g2d, ghost);
                drawGhostScaredFace(g2d, ghost);
            } else {
                g2d.setColor(ghost.getColor());
                drawGhostBody(g2d,ghost);
                drawGhostSkirt(g2d, ghost);
                drawGhostEyes(g2d, ghost);
            }


        }
    }

    private void drawGhostScaredFace(Graphics2D g2d, Ghost ghost) {
        g2d.setColor(Color.PINK);
        int eyeSize = 2* TILE_SIZE /16;
        Point leftEyePosition = new Point(ghost.getPosition().x + 5* TILE_SIZE /16, ghost.getPosition().y + 6* TILE_SIZE /16);
        Point rightEyeOffset = new Point(3* TILE_SIZE /16 + eyeSize, 0);
        g2d.fillRect(leftEyePosition.x,
                leftEyePosition.y,
                eyeSize, eyeSize);
        g2d.fillRect(leftEyePosition.x + rightEyeOffset.x,
                leftEyePosition.y + rightEyeOffset.y,
                eyeSize, eyeSize);
        Point mouthLeftCorner = new Point (ghost.getPosition().x + 3 * TILE_SIZE / 16, ghost.getPosition().y + 11* TILE_SIZE /16);
        g2d.drawLine(mouthLeftCorner.x,
                mouthLeftCorner.y,
                mouthLeftCorner.x + TILE_SIZE / 16,
                mouthLeftCorner.y - TILE_SIZE / 16);
        g2d.drawLine(mouthLeftCorner.x + TILE_SIZE / 16,
                mouthLeftCorner.y - TILE_SIZE / 16,
                mouthLeftCorner.x + 3 * TILE_SIZE / 16,
                mouthLeftCorner.y);
        g2d.drawLine(mouthLeftCorner.x + 3 * TILE_SIZE / 16,
                mouthLeftCorner.y,
                mouthLeftCorner.x + 5 * TILE_SIZE / 16,
                mouthLeftCorner.y - TILE_SIZE / 16);
        g2d.drawLine(mouthLeftCorner.x + 5 * TILE_SIZE / 16,
                mouthLeftCorner.y - TILE_SIZE / 16,
                mouthLeftCorner.x + 7 * TILE_SIZE / 16,
                mouthLeftCorner.y);
        g2d.drawLine(mouthLeftCorner.x + 7 * TILE_SIZE / 16,
                mouthLeftCorner.y,
                mouthLeftCorner.x + 9 * TILE_SIZE / 16,
                mouthLeftCorner.y - TILE_SIZE / 16);
        g2d.drawLine(mouthLeftCorner.x + 9 * TILE_SIZE / 16,
                mouthLeftCorner.y - TILE_SIZE / 16,
                mouthLeftCorner.x + 10 * TILE_SIZE / 16,
                mouthLeftCorner.y);
    }

    private void drawGhostBody(Graphics2D g2d, Ghost ghost) {
        g2d.fillArc(ghost.getPosition().x + TILE_SIZE / 16,
                ghost.getPosition().y + TILE_SIZE / 16,
                TILE_SIZE - TILE_SIZE / 8,
                3 * TILE_SIZE / 4,
                0,
                180);
        g2d.fillRect(ghost.getPosition().x + TILE_SIZE / 16,
                ghost.getPosition().y + 3 * TILE_SIZE / 8,
                14 * TILE_SIZE / 16,
                7 * TILE_SIZE / 16);
    }

    private void drawGhostSkirt(Graphics2D g2d, Ghost ghost) {
        int[] x1 = new int[]{ghost.getPosition().x + TILE_SIZE /16,
                ghost.getPosition().x + TILE_SIZE /16,
                ghost.getPosition().x + 3* TILE_SIZE /16};
        int[] x2 = new int[]{ghost.getPosition().x + 4* TILE_SIZE /16,
                ghost.getPosition().x + 6* TILE_SIZE /16,
                ghost.getPosition().x + 6* TILE_SIZE /16};
        int[] x3 = new int[]{ghost.getPosition().x + 10* TILE_SIZE /16,
                ghost.getPosition().x + 10* TILE_SIZE /16,
                ghost.getPosition().x + 12* TILE_SIZE /16};
        int[] x4 = new int[]{ghost.getPosition().x + 13* TILE_SIZE /16,
                ghost.getPosition().x + 15* TILE_SIZE /16,
                ghost.getPosition().x + 15* TILE_SIZE /16};
        int[] y1 = new int[]{ghost.getPosition().y + 13* TILE_SIZE /16,
                ghost.getPosition().y + 15* TILE_SIZE /16,
                ghost.getPosition().y + 13* TILE_SIZE /16};
        int[] y2 = new int[]{ghost.getPosition().y + 13* TILE_SIZE /16,
                ghost.getPosition().y + 13* TILE_SIZE /16,
                ghost.getPosition().y + 15* TILE_SIZE /16};
        g2d.fillPolygon(x1, y1, 3);
        g2d.fillPolygon(x2, y2, 3);
        g2d.fillRect(x2[2], y1[0], TILE_SIZE /16, 2* TILE_SIZE /16);
        g2d.fillRect(x2[2] + 3* TILE_SIZE /16, y1[0], TILE_SIZE /16, 2* TILE_SIZE /16);
        g2d.fillPolygon(x3, y1, 3);
        g2d.fillPolygon(x4, y2, 3);
    }

    private void drawGhostEyes(Graphics2D g2d, Ghost ghost) {
        g2d.setColor(Color.WHITE);
        int eyeHeight = 6* TILE_SIZE /16;
        int eyeWidth = 4* TILE_SIZE /16;
        int pupilSize = 2* TILE_SIZE /16;
        Point leftEyePosition = new Point(ghost.getPosition().x + 4* TILE_SIZE /16, ghost.getPosition().y + 4* TILE_SIZE /16);
        Point rightEyeOffset = new Point(TILE_SIZE /16 + eyeWidth, 0);
        Point leftPupilPosition = new Point(ghost.getPosition().x + 6* TILE_SIZE /16, ghost.getPosition().y + 6* TILE_SIZE /16);
        Point[] orientationOffsets = getEyeAndPupilOrientationOffset(ghost.getOrientation());

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

    private Point[] getEyeAndPupilOrientationOffset(Orientation orientation) {
        Point eyeOrientationOffset;
        Point pupilOrientationOffset;
        if (orientation == Orientation.UP) {
            eyeOrientationOffset = new Point(-TILE_SIZE /16, -TILE_SIZE /8);
            pupilOrientationOffset = new Point(-TILE_SIZE /8, -TILE_SIZE /4);
        } else if (orientation == Orientation.LEFT) {
            eyeOrientationOffset = new Point(-TILE_SIZE /8, 0);
            pupilOrientationOffset = new Point(-TILE_SIZE /4, 0);
        } else if (orientation == Orientation.DOWN) {
            eyeOrientationOffset = new Point(-TILE_SIZE /16, TILE_SIZE /16);
            pupilOrientationOffset = new Point(-2* TILE_SIZE /16, 3* TILE_SIZE /16);
        } else {
            eyeOrientationOffset = new Point(0,0);
            pupilOrientationOffset = new Point(0, 0);
        }
        return new Point[]{eyeOrientationOffset, pupilOrientationOffset};
    }

    private void drawPacMan(Graphics2D g2d) {
        int mouthStartAngle;
        g2d.setColor(Color.YELLOW);
        if(PAC_MAN.getOrientation() == Orientation.UP) {
            mouthStartAngle = 90;
        } else if (PAC_MAN.getOrientation() == Orientation.RIGHT) {
            mouthStartAngle = 0;
        } else if (PAC_MAN.getOrientation() == Orientation.DOWN) {
            mouthStartAngle = -90;
        } else {
            mouthStartAngle = 180;
        }
        g2d.fillArc(PAC_MAN.getPosition().x,
                PAC_MAN.getPosition().y,
                TILE_SIZE,
                TILE_SIZE,
                mouthStartAngle + PAC_MAN.getCurrentMouthAngle()/2,
                360 - PAC_MAN.getCurrentMouthAngle());
    }

    public void update() {
        PAC_MAN.update(gameKeyHandler.getNextOrientation());
        BLINKY.update();
        PINKY.update();
        INKY.update();
        CLYDE.update();
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
