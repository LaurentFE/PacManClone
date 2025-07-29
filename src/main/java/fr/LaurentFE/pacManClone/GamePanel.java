package fr.LaurentFE.pacManClone;

import fr.LaurentFE.pacManClone.entities.*;
import fr.LaurentFE.pacManClone.entities.ghostPersonality.Blinky;
import fr.LaurentFE.pacManClone.entities.ghostPersonality.Clyde;
import fr.LaurentFE.pacManClone.entities.ghostPersonality.Inky;
import fr.LaurentFE.pacManClone.entities.ghostPersonality.Pinky;
import fr.LaurentFE.pacManClone.map.GameMap;
import fr.LaurentFE.pacManClone.map.Position;
import fr.LaurentFE.pacManClone.map.TileIndex;
import fr.LaurentFE.pacManClone.map.TileType;

import javax.swing.*;
import java.awt.*;
import java.util.*;


public class GamePanel extends JPanel implements Runnable {

    private final GameKeyHandler gameKeyHandler;
    private Thread gameThread;

    public static final int TILE_SIZE = 32;
    private static final Orientation DEFAULT_ORIENTATION = Orientation.RIGHT;
    private static final int MOVE_SPEED = TILE_SIZE /8;
    private static final int INITIAL_LIVES = 3;
    public static PacMan PAC_MAN;
    public static Ghost BLINKY;
    public static Ghost PINKY;
    public static Ghost INKY;
    public static Ghost CLYDE;

    private final GameMap gameMap;
    private final Set<Pellet> pellets;
    private final int pelletSize = 4;
    private final int pelletOffset = (TILE_SIZE - pelletSize) / 2;
    private final int powerPelletSize = 16;
    private final int powerPelletOffset = (TILE_SIZE - powerPelletSize) / 2;
    private final Set<Ghost> ghosts = new HashSet<>();

    public GamePanel() {
        this.gameMap = GameMap.getInstance();
        pellets = gameMap.loadPellets("../resources/level0_pellets");
        instantiateGhosts();
        instantiatePacMan(INITIAL_LIVES);
        setPreferredSize(new Dimension(gameMap.getMapWidthTile()* TILE_SIZE, gameMap.getMapHeightTile()* TILE_SIZE));
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

    private void resetLevel() {
        instantiateGhosts();
        instantiatePacMan(PAC_MAN.getLives());
        gameKeyHandler.resetInput();
    }

    private void instantiatePacMan(int lives) {
        PAC_MAN = new PacMan(
                new TileIndex(13, 26).toPosition(),
                DEFAULT_ORIENTATION,
                MOVE_SPEED,
                lives);
    }

    private void instantiateGhosts() {
        ghosts.clear();
        BLINKY = new Ghost(
                new TileIndex(9, 14).toPosition(),
                DEFAULT_ORIENTATION,
                MOVE_SPEED,
                Color.RED,
                new Blinky(),
                new TileIndex(0,0));
        PINKY = new Ghost(
                new TileIndex(18, 14).toPosition(),
                DEFAULT_ORIENTATION,
                MOVE_SPEED,
                Color.PINK,
                new Pinky(),
                new TileIndex(GameMap.getInstance().getMapWidthTile() - 1, 0));
        INKY = new Ghost(
                new TileIndex(12, 14).toPosition(),
                DEFAULT_ORIENTATION,
                MOVE_SPEED,
                Color.CYAN,
                new Inky(),
                new TileIndex(GameMap.getInstance().getMapWidthTile() - 1, GameMap.getInstance().getMapHeightTile() - 1));
        CLYDE = new Ghost(
                new TileIndex(15, 14).toPosition(),
                DEFAULT_ORIENTATION,
                MOVE_SPEED,
                Color.ORANGE,
                new Clyde(),
                new TileIndex(0, GameMap.getInstance().getMapHeightTile() - 1));
        ghosts.add(BLINKY);
        ghosts.add(PINKY);
        ghosts.add(INKY);
        ghosts.add(CLYDE);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        drawMap(g2d);
        drawPellets(g2d);
        drawPacMan(g2d);
        if(PAC_MAN.isAlive()) {
            for (Ghost ghost : ghosts)
                drawGhost(g2d, ghost);
        }

    }

    private void drawMap(Graphics2D g2d) {
        for (int y = 0; y < gameMap.getMapHeightTile(); y++) {
            for (int x = 0; x < gameMap.getMapWidthTile(); x++) {
                TileType currentTile = gameMap.getTile(new TileIndex(x,y));
                if (currentTile == TileType.PATH
                        || currentTile == TileType.OUTOFBOUNDS) {
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(x* TILE_SIZE,
                            y* TILE_SIZE,
                            TILE_SIZE,
                            TILE_SIZE);
                } else if (currentTile == TileType.DOOR || currentTile == TileType.DECORATIVEDOOR) {
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
        neighbours.put("above",gameMap.getTile(new TileIndex(x, y-1)));
        neighbours.put("left",gameMap.getTile(new TileIndex(x-1, y)));
        neighbours.put("below",gameMap.getTile(new TileIndex(x, y+1)));

        g2d.setColor(Color.BLUE);
        switch(gameMap.getTile(new TileIndex(x,y))){
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
        Position leftEyePosition = new Position(ghost.getPosition().x + 5* TILE_SIZE /16, ghost.getPosition().y + 6* TILE_SIZE /16);
        Position rightEyeOffset = new Position(3* TILE_SIZE /16 + eyeSize, 0);
        Position rightEyePosition = rightEyeOffset.add(leftEyePosition);
        g2d.fillRect(leftEyePosition.x,
                leftEyePosition.y,
                eyeSize, eyeSize);
        g2d.fillRect(rightEyePosition.x,
                rightEyePosition.y,
                eyeSize, eyeSize);
        Position mouthLeftCorner = new Position (ghost.getPosition().x + 3 * TILE_SIZE / 16, ghost.getPosition().y + 11* TILE_SIZE /16);
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
        Position leftEyePosition = new Position(ghost.getPosition().x + 4* TILE_SIZE /16, ghost.getPosition().y + 4* TILE_SIZE /16);
        Position rightEyePosition = new Position(TILE_SIZE /16 + eyeWidth, 0).add(leftEyePosition);
        Position leftPupilPosition = new Position(ghost.getPosition().x + 6* TILE_SIZE /16, ghost.getPosition().y + 6* TILE_SIZE /16);
        Position rightPupilPosition = new Position(TILE_SIZE /16 + eyeWidth, 0).add(leftPupilPosition);
        Position[] orientationOffsets = getEyeAndPupilOrientationOffset(ghost.getOrientation());

        leftEyePosition.add(orientationOffsets[0]);
        leftPupilPosition.add(orientationOffsets[1]);
        rightEyePosition.add(orientationOffsets[0]);
        rightPupilPosition.add(orientationOffsets[1]);

        g2d.fillArc(leftEyePosition.x,
                leftEyePosition.y,
                eyeWidth, eyeHeight, 0, 360);
        g2d.fillArc(rightEyePosition.x,
                rightEyePosition.y,
                eyeWidth, eyeHeight, 0, 360);
        g2d.setColor(Color.BLUE);
        g2d.fillArc(leftPupilPosition.x,
                leftPupilPosition.y,
                pupilSize, pupilSize, 0, 360);
        g2d.fillArc(rightPupilPosition.x,
                rightPupilPosition.y,
                pupilSize, pupilSize, 0, 360);

    }

    private Position[] getEyeAndPupilOrientationOffset(Orientation orientation) {
        Position eyeOrientationOffset;
        Position pupilOrientationOffset;
        if (orientation == Orientation.UP) {
            eyeOrientationOffset = new Position(-TILE_SIZE /16, -TILE_SIZE /8);
            pupilOrientationOffset = new Position(-TILE_SIZE /8, -TILE_SIZE /4);
        } else if (orientation == Orientation.LEFT) {
            eyeOrientationOffset = new Position(-TILE_SIZE /8, 0);
            pupilOrientationOffset = new Position(-TILE_SIZE /4, 0);
        } else if (orientation == Orientation.DOWN) {
            eyeOrientationOffset = new Position(-TILE_SIZE /16, TILE_SIZE /16);
            pupilOrientationOffset = new Position(-2* TILE_SIZE /16, 3* TILE_SIZE /16);
        } else {
            eyeOrientationOffset = new Position(0,0);
            pupilOrientationOffset = new Position(0, 0);
        }
        return new Position[]{eyeOrientationOffset, pupilOrientationOffset};
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

    private void drawPellets(Graphics2D g2d) {
        g2d.setColor(Color.PINK);
        for(Pellet pellet : pellets) {
            Position pelletPosition = pellet.getTileIndex().toPosition();
            if (pellet.isPowerPellet())
                g2d.fillOval(pelletPosition.x + powerPelletOffset,
                        pelletPosition.y + powerPelletOffset,
                        powerPelletSize,
                        powerPelletSize);
            else
                g2d.fillRect(pelletPosition.x + pelletOffset,
                        pelletPosition.y + pelletOffset,
                        pelletSize,
                        pelletSize);
        }
    }

    private Rectangle getPelletHitBox(Pellet pellet) {
        Position pelletPosition = pellet.getTileIndex().toPosition();
        if (pellet.isPowerPellet()) {
            return new Rectangle(
                    pelletPosition.x + powerPelletOffset,
                    pelletPosition.y + powerPelletOffset,
                    powerPelletSize,
                    powerPelletSize);
        } else {
            return new Rectangle(
                    pelletPosition.x + pelletOffset,
                    pelletPosition.y + pelletOffset,
                    pelletSize,
                    pelletSize);
        }
    }

    private void frightenGhosts() {
        for (Ghost ghost : ghosts)
            ghost.setState(GhostState.FRIGHTENED);
    }

    private void checkCollisionWithPellets() {
        if(pellets != null) {
            Set<Pellet> eatenPellets = new HashSet<>();
            for(Pellet pellet : pellets) {
                Rectangle pelletHitBox = getPelletHitBox(pellet);
                Rectangle pacManHitBox = PAC_MAN.getHitBox();
                if (pelletHitBox.intersection(pacManHitBox).getSize().equals(pelletHitBox.getSize())) {
                    eatenPellets.add(pellet);
                    if (pellet.isPowerPellet())
                        frightenGhosts();
                }
            }
            for(Pellet pellet : eatenPellets) {
                pellets.remove(pellet);
            }
        }
    }

    private boolean ghostCollidesWithPacMan(Ghost ghost) {
        Rectangle ghostHitBox =  ghost.getHitBox();
        Rectangle pacManHitBox = PAC_MAN.getHitBox();
        Rectangle collisionBox = ghostHitBox.intersection(pacManHitBox);
        if (collisionBox.height >= 0 && collisionBox.width >= 0) {
            double collisionBoxArea = collisionBox.height * collisionBox.width;
            double tileArea = TILE_SIZE * TILE_SIZE;
            return collisionBoxArea / tileArea >= 0.3;
        }
        return false;
    }

    private void checkCollisionWithGhosts() {
        for (Ghost ghost : ghosts) {
            if (ghostCollidesWithPacMan(ghost)) {
                if (ghost.getState() == GhostState.FRIGHTENED)
                    ghost.setState(GhostState.EATEN);
                else if (ghost.getState() != GhostState.EATEN){
                    PAC_MAN.kill();
                    return;
                    }
            }
        }
    }

    private void closeOnVictory() {
        JOptionPane.showMessageDialog(
                null,
                """
                        Congratulations !
                        You have eaten all the pellets on the map.
                        Victory is yours !""",
                "VICTORY",
                JOptionPane.INFORMATION_MESSAGE);
        stopGameThread();
        GameFrame parentFrame = (GameFrame) SwingUtilities.getWindowAncestor(this);
        parentFrame.closeGame();
    }

    public void closeOnDeath() {
        JOptionPane.showMessageDialog(
                null,
                """
                        Oh no !
                        You have died you last life.
                        This is Game Over.""",
                "GAME OVER",
                JOptionPane.INFORMATION_MESSAGE);
        stopGameThread();
        GameFrame parentFrame = (GameFrame) SwingUtilities.getWindowAncestor(this);
        parentFrame.closeGame();
    }

    public void update() {
        PAC_MAN.update(gameKeyHandler.getNextOrientation());
        if(PAC_MAN.isAlive()) {
            for (Ghost ghost : ghosts)
                ghost.update();
            checkCollisionWithGhosts();
            if (pellets.isEmpty())
                closeOnVictory();
            checkCollisionWithPellets();
        }
        if(PAC_MAN.isDeathAnimationFinished()) {
            if (PAC_MAN.getLives() == 0)
                closeOnDeath();
            else
                resetLevel();
        }
    }

    @Override
    public void run() {
        final double FPS = 60;
        double drawIntervalNanoSec = 1_000_000_000L/FPS;
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
