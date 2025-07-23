package fr.LaurentFE.pacManClone.ghost.personality;

import fr.LaurentFE.pacManClone.GameMap;
import fr.LaurentFE.pacManClone.GamePanel;
import fr.LaurentFE.pacManClone.Orientation;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public final class Blinky implements GhostPersonality {

    private static final Point SCATTER_TARGET_TILE = new Point(GameMap.getInstance().getMapWidthTile() - 1, 0);

    public Blinky() {}

    @Override
    public Orientation getNextMovementOrientation() {
        if (alreadyDecidedAtThisCrossroad()
                && !isFacingAWall())
            return GamePanel.BLINKY.getOrientation();

        return switch (GamePanel.BLINKY.getState()) {
            case CHASE -> getNextChaseMovementOrientation();
            case SCATTER -> getNextScatterMovementOrientation();
            case FRIGHTENED -> getNextFrightenedMovementOrientation();
            case EATEN -> getNextEatenMovementOrientation();
        };
    }

    private boolean alreadyDecidedAtThisCrossroad() {
        Point currentTile = GamePanel.BLINKY.getPosition();
        currentTile.x = currentTile.x / GamePanel.TILE_SIZE;
        currentTile.y = currentTile.y / GamePanel.TILE_SIZE;
        return GamePanel.BLINKY.getLastCrossroadTile().x == currentTile.x
                && GamePanel.BLINKY.getLastCrossroadTile().y == currentTile.y;
    }

    private boolean isFacingAWall() {
        HashMap<Orientation, Point> directionModifier = new HashMap<>();
        directionModifier.put(Orientation.UP, new Point(0, -1));
        directionModifier.put(Orientation.LEFT, new Point(-1, 0));
        directionModifier.put(Orientation.DOWN, new Point(0, 1));
        directionModifier.put(Orientation.RIGHT, new Point(1, 0));

        Orientation currentOrientation = GamePanel.BLINKY.getOrientation();
        Point currentPosition = GamePanel.BLINKY.getPosition();
        Point tileInFront = new Point(currentPosition.x / GamePanel.TILE_SIZE, currentPosition.y / GamePanel.TILE_SIZE);
        tileInFront.x += directionModifier.get(currentOrientation).x;
        tileInFront.y += directionModifier.get(currentOrientation).y;

        return !GamePanel.BLINKY.canGoThroughTile(tileInFront);
    }

    private ArrayList<Point> getConsideredMoveTiles() {
        Point currentPosition = GamePanel.BLINKY.getPosition();
        Orientation currentOrientation = GamePanel.BLINKY.getOrientation();

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
        if (currentOrientation != Orientation.DOWN && GamePanel.BLINKY.canGoThroughTile(tileAbovePosition))
            consideredMoveTiles.add(tileAbovePosition);
        if (currentOrientation != Orientation.RIGHT && GamePanel.BLINKY.canGoThroughTile(tileOnLeftPosition))
            consideredMoveTiles.add(tileOnLeftPosition);
        if (currentOrientation != Orientation.UP && GamePanel.BLINKY.canGoThroughTile(tileBelowPosition))
            consideredMoveTiles.add(tileBelowPosition);
        if (currentOrientation != Orientation.LEFT && GamePanel.BLINKY.canGoThroughTile(tileOnRightPosition))
            consideredMoveTiles.add(tileOnRightPosition);

        return consideredMoveTiles;
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
        Point currentPosition = GamePanel.BLINKY.getPosition();
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
            return GamePanel.BLINKY.getOrientation();
    }

    private Orientation getNextChaseMovementOrientation() {
        Point currentPosition = GamePanel.BLINKY.getPosition();
        currentPosition.x = currentPosition.x / GamePanel.TILE_SIZE;
        currentPosition.y = currentPosition.y / GamePanel.TILE_SIZE;

        Point targetTile = GamePanel.PAC_MAN.getPosition();
        targetTile.x = targetTile.x / GamePanel.TILE_SIZE;
        targetTile.y = targetTile.y / GamePanel.TILE_SIZE;
        Point nextMoveTile = getNextMoveTile(targetTile);

        return getOrientationToGoToTile(nextMoveTile);
    }

    private Orientation getNextScatterMovementOrientation() {
        Point currentPosition = GamePanel.BLINKY.getPosition();
        currentPosition.x = currentPosition.x / GamePanel.TILE_SIZE;
        currentPosition.y = currentPosition.y / GamePanel.TILE_SIZE;

        Point nextMoveTile = getNextMoveTile(SCATTER_TARGET_TILE);

        return getOrientationToGoToTile(nextMoveTile);
    }

    private Orientation getNextFrightenedMovementOrientation() {
        ArrayList<Point> consideredMoveTiles = getConsideredMoveTiles();
        Orientation currentOrientation = GamePanel.BLINKY.getOrientation();
        if (consideredMoveTiles.size() > 1) {
            Point currentPosition = GamePanel.BLINKY.getPosition();
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
                consideredMoveTiles.add(tileAbovePosition);
            else if (currentOrientation == Orientation.RIGHT)
                consideredMoveTiles.add(tileOnLeftPosition);
            else if (currentOrientation == Orientation.UP)
                consideredMoveTiles.add(tileBelowPosition);
            else if (currentOrientation == Orientation.LEFT)
                consideredMoveTiles.add(tileOnRightPosition);

            Random random = new Random();
            return getOrientationToGoToTile(consideredMoveTiles.get(random.nextInt(consideredMoveTiles.size())));
        }
        return getOrientationToGoToTile(consideredMoveTiles.getFirst());
    }

    private Orientation getNextEatenMovementOrientation() {
        return null;
    }
}
