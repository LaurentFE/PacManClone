package fr.LaurentFE.pacManClone.ghost.personality;

import fr.LaurentFE.pacManClone.GameMap;
import fr.LaurentFE.pacManClone.GamePanel;
import fr.LaurentFE.pacManClone.Orientation;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Inky implements GhostPersonality {

    private static final Point SCATTER_TARGET_TILE = new Point(GameMap.getInstance().getMapWidthTile() - 1, GameMap.getInstance().getMapHeightTile() - 1);
    private static final Point EATEN_TARGET_TILE = GameMap.getInstance().getGhostHouse();

    public Inky() {}

    @Override
    public Orientation getNextMovementOrientation() {
        if (alreadyDecidedAtThisCrossroad()
                && !isFacingAWall())
            return GamePanel.INKY.getOrientation();

        return switch (GamePanel.INKY.getState()) {
            case CHASE -> getNextChaseMovementOrientation();
            case SCATTER -> getNextScatterMovementOrientation();
            case FRIGHTENED -> getNextFrightenedMovementOrientation();
            case EATEN -> getNextEatenMovementOrientation();
        };
    }

    private boolean alreadyDecidedAtThisCrossroad() {
        Point currentTile = GamePanel.INKY.getPosition();
        currentTile.x = currentTile.x / GamePanel.TILE_SIZE;
        currentTile.y = currentTile.y / GamePanel.TILE_SIZE;
        return GamePanel.INKY.getLastCrossroadTile().x == currentTile.x
                && GamePanel.INKY.getLastCrossroadTile().y == currentTile.y;
    }

    private boolean isFacingAWall() {
        HashMap<Orientation, Point> directionModifier = new HashMap<>();
        directionModifier.put(Orientation.UP, new Point(0, -1));
        directionModifier.put(Orientation.LEFT, new Point(-1, 0));
        directionModifier.put(Orientation.DOWN, new Point(0, 1));
        directionModifier.put(Orientation.RIGHT, new Point(1, 0));

        Orientation currentOrientation = GamePanel.INKY.getOrientation();
        Point currentPosition = GamePanel.INKY.getPosition();
        Point tileInFront = new Point(currentPosition.x / GamePanel.TILE_SIZE, currentPosition.y / GamePanel.TILE_SIZE);
        tileInFront.x += directionModifier.get(currentOrientation).x;
        tileInFront.y += directionModifier.get(currentOrientation).y;

        return !GamePanel.INKY.canGoThroughTile(tileInFront);
    }

    private ArrayList<Point> getConsideredMoveTiles() {
        Point currentPosition = GamePanel.INKY.getPosition();
        Orientation currentOrientation = GamePanel.INKY.getOrientation();

        HashMap<Orientation, Point> directionModifier = new HashMap<>();
        directionModifier.put(Orientation.UP, new Point(0, -1));
        directionModifier.put(Orientation.LEFT, new Point(-1, 0));
        directionModifier.put(Orientation.DOWN, new Point(0, 1));
        directionModifier.put(Orientation.RIGHT, new Point(1, 0));

        Point tileAbovePosition = new Point(
                currentPosition.x / GamePanel.TILE_SIZE + directionModifier.get(Orientation.UP).x,
                currentPosition.y / GamePanel.TILE_SIZE + directionModifier.get(Orientation.UP).y);
        Point tileOnLeftPosition = new Point(
                currentPosition.x / GamePanel.TILE_SIZE + directionModifier.get(Orientation.LEFT).x,
                currentPosition.y / GamePanel.TILE_SIZE + directionModifier.get(Orientation.LEFT).y);
        Point tileBelowPosition = new Point(
                currentPosition.x / GamePanel.TILE_SIZE + directionModifier.get(Orientation.DOWN).x,
                currentPosition.y / GamePanel.TILE_SIZE + directionModifier.get(Orientation.DOWN).y);
        Point tileOnRightPosition = new Point(
                currentPosition.x / GamePanel.TILE_SIZE + directionModifier.get(Orientation.RIGHT).x,
                currentPosition.y / GamePanel.TILE_SIZE + directionModifier.get(Orientation.RIGHT).y);

        ArrayList<Point> consideredMoveTiles = new ArrayList<>();
        if (currentOrientation != Orientation.DOWN && GamePanel.INKY.canGoThroughTile(tileAbovePosition))
            consideredMoveTiles.add(tileAbovePosition);
        if (currentOrientation != Orientation.RIGHT && GamePanel.INKY.canGoThroughTile(tileOnLeftPosition))
            consideredMoveTiles.add(tileOnLeftPosition);
        if (currentOrientation != Orientation.UP && GamePanel.INKY.canGoThroughTile(tileBelowPosition))
            consideredMoveTiles.add(tileBelowPosition);
        if (currentOrientation != Orientation.LEFT && GamePanel.INKY.canGoThroughTile(tileOnRightPosition))
            consideredMoveTiles.add(tileOnRightPosition);

        return consideredMoveTiles;
    }

    private Point getBehindTile() {
        Orientation currentOrientation = GamePanel.INKY.getOrientation();
        Point currentPosition = GamePanel.INKY.getPosition();
        HashMap<Orientation, Point> directionModifier = new HashMap<>();
        directionModifier.put(Orientation.UP, new Point(0, -1));
        directionModifier.put(Orientation.LEFT, new Point(-1, 0));
        directionModifier.put(Orientation.DOWN, new Point(0, 1));
        directionModifier.put(Orientation.RIGHT, new Point(1, 0));
        Point tileAbovePosition = new Point(
                currentPosition.x / GamePanel.TILE_SIZE + directionModifier.get(Orientation.UP).x,
                currentPosition.y / GamePanel.TILE_SIZE + directionModifier.get(Orientation.UP).y);
        Point tileOnLeftPosition = new Point(
                currentPosition.x / GamePanel.TILE_SIZE + directionModifier.get(Orientation.LEFT).x,
                currentPosition.y / GamePanel.TILE_SIZE + directionModifier.get(Orientation.LEFT).y);
        Point tileBelowPosition = new Point(
                currentPosition.x / GamePanel.TILE_SIZE + directionModifier.get(Orientation.DOWN).x,
                currentPosition.y / GamePanel.TILE_SIZE + directionModifier.get(Orientation.DOWN).y);
        Point tileOnRightPosition = new Point(
                currentPosition.x / GamePanel.TILE_SIZE + directionModifier.get(Orientation.RIGHT).x,
                currentPosition.y / GamePanel.TILE_SIZE + directionModifier.get(Orientation.RIGHT).y);
        if (currentOrientation == Orientation.DOWN)
            return tileAbovePosition;
        else if (currentOrientation == Orientation.RIGHT)
            return tileOnLeftPosition;
        else if (currentOrientation == Orientation.UP)
            return tileBelowPosition;
        else
            return tileOnRightPosition;
    }

    private Point getNextMoveTile(Point targetTile) {
        Point finalTile = new Point();
        int squaredDist = Integer.MAX_VALUE;
        for (Point consideredTile : getConsideredMoveTiles()) {
            int relativeXDist = targetTile.x - consideredTile.x;
            int relativeYDist = targetTile.y - consideredTile.y;
            int squaredDistanceToTarget = relativeXDist * relativeXDist + relativeYDist * relativeYDist;
            if (squaredDistanceToTarget < squaredDist) {
                squaredDist = squaredDistanceToTarget;
                finalTile = consideredTile;
            }
        }

        return finalTile;
    }

    private Orientation getOrientationToGoToTile(Point tile) {
        Point currentPosition = GamePanel.INKY.getPosition();
        currentPosition.x = currentPosition.x / GamePanel.TILE_SIZE;
        currentPosition.y = currentPosition.y / GamePanel.TILE_SIZE;
        if (tile.x == currentPosition.x && tile.y < currentPosition.y)
            return Orientation.UP;
        else if (tile.x < currentPosition.x && tile.y == currentPosition.y)
            return Orientation.LEFT;
        else if (tile.x == currentPosition.x && tile.y > currentPosition.y)
            return Orientation.DOWN;
        else if (tile.x > currentPosition.x && tile.y == currentPosition.y)
            return Orientation.RIGHT;
        else
            return GamePanel.INKY.getOrientation();
    }

    private Orientation getNextChaseMovementOrientation() {
        Point currentPosition = GamePanel.INKY.getPosition();
        currentPosition.x = currentPosition.x / GamePanel.TILE_SIZE;
        currentPosition.y = currentPosition.y / GamePanel.TILE_SIZE;

        Point pacManTile = GamePanel.PAC_MAN.getPosition();
        pacManTile.x = pacManTile.x / GamePanel.TILE_SIZE;
        pacManTile.y = pacManTile.y / GamePanel.TILE_SIZE;
        switch (GamePanel.PAC_MAN.getOrientation()) {
            case UP -> pacManTile.y -= 2;
            case LEFT -> pacManTile.x -= 2;
            case DOWN -> pacManTile.y += 2;
            case RIGHT -> pacManTile.x += 2;
        }
        Point blinkyTile = GamePanel.BLINKY.getPosition();
        blinkyTile.x = pacManTile.x / GamePanel.TILE_SIZE;
        blinkyTile.y = pacManTile.y / GamePanel.TILE_SIZE;
        Point targetTile = new Point(
                -(blinkyTile.x - pacManTile.x),
                -(blinkyTile.y - pacManTile.y));


        Point nextMoveTile = getNextMoveTile(targetTile);

        
        return getOrientationToGoToTile(nextMoveTile);
    }

    private Orientation getNextScatterMovementOrientation() {
        Point currentPosition = GamePanel.INKY.getPosition();
        currentPosition.x = currentPosition.x / GamePanel.TILE_SIZE;
        currentPosition.y = currentPosition.y / GamePanel.TILE_SIZE;

        Point nextMoveTile = getNextMoveTile(SCATTER_TARGET_TILE);

        return getOrientationToGoToTile(nextMoveTile);
    }

    private Orientation getNextFrightenedMovementOrientation() {
        ArrayList<Point> consideredMoveTiles = getConsideredMoveTiles();
        if (consideredMoveTiles.size() > 1) {
            consideredMoveTiles.add(getBehindTile());
            Random random = new Random();
            return getOrientationToGoToTile(consideredMoveTiles.get(random.nextInt(consideredMoveTiles.size())));
        }
        return getOrientationToGoToTile(consideredMoveTiles.getFirst());
    }

    private Orientation getNextEatenMovementOrientation() {
        Point currentPosition = GamePanel.INKY.getPosition();
        currentPosition.x = currentPosition.x / GamePanel.TILE_SIZE;
        currentPosition.y = currentPosition.y / GamePanel.TILE_SIZE;

        if (currentPosition.x != EATEN_TARGET_TILE.x || currentPosition.y != EATEN_TARGET_TILE.y)
            return getOrientationToGoToTile(getNextMoveTile(EATEN_TARGET_TILE));
        System.out.println("GO DOWN");
        return Orientation.DOWN;
    }
}
