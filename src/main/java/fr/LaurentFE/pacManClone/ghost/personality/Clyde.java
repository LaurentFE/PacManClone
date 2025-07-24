package fr.LaurentFE.pacManClone.ghost.personality;

import fr.LaurentFE.pacManClone.GameMap;
import fr.LaurentFE.pacManClone.GamePanel;
import fr.LaurentFE.pacManClone.Orientation;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Clyde implements GhostPersonality {
    
    private static final Point SCATTER_TARGET_TILE = new Point(0, GameMap.getInstance().getMapHeightTile() - 1);
    private static final Point EATEN_TARGET_TILE = GameMap.getInstance().getGhostHouse();

    public Clyde() {}

    @Override
    public Orientation getNextMovementOrientation() {
        if (alreadyDecidedAtThisCrossroad()
                && !isFacingAWall())
            return GamePanel.CLYDE.getOrientation();

        return switch (GamePanel.CLYDE.getState()) {
            case CHASE -> getNextChaseMovementOrientation();
            case SCATTER -> getNextScatterMovementOrientation();
            case FRIGHTENED -> getNextFrightenedMovementOrientation();
            case EATEN -> getNextEatenMovementOrientation();
        };
    }

    private boolean alreadyDecidedAtThisCrossroad() {
        Point currentTile = GamePanel.CLYDE.getPosition();
        currentTile.x = currentTile.x / GamePanel.TILE_SIZE;
        currentTile.y = currentTile.y / GamePanel.TILE_SIZE;
        return GamePanel.CLYDE.getLastCrossroadTile().x == currentTile.x
                && GamePanel.CLYDE.getLastCrossroadTile().y == currentTile.y;
    }

    private boolean isFacingAWall() {
        HashMap<Orientation, Point> directionModifier = new HashMap<>();
        directionModifier.put(Orientation.UP, new Point(0, -1));
        directionModifier.put(Orientation.LEFT, new Point(-1, 0));
        directionModifier.put(Orientation.DOWN, new Point(0, 1));
        directionModifier.put(Orientation.RIGHT, new Point(1, 0));

        Orientation currentOrientation = GamePanel.CLYDE.getOrientation();
        Point currentPosition = GamePanel.CLYDE.getPosition();
        Point tileInFront = new Point(currentPosition.x / GamePanel.TILE_SIZE, currentPosition.y / GamePanel.TILE_SIZE);
        tileInFront.x += directionModifier.get(currentOrientation).x;
        tileInFront.y += directionModifier.get(currentOrientation).y;

        return !GamePanel.CLYDE.canGoThroughTile(tileInFront);
    }

    private ArrayList<Point> getConsideredMoveTiles() {
        Point currentPosition = GamePanel.CLYDE.getPosition();
        Orientation currentOrientation = GamePanel.CLYDE.getOrientation();

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
        if (currentOrientation != Orientation.DOWN && GamePanel.CLYDE.canGoThroughTile(tileAbovePosition))
            consideredMoveTiles.add(tileAbovePosition);
        if (currentOrientation != Orientation.RIGHT && GamePanel.CLYDE.canGoThroughTile(tileOnLeftPosition))
            consideredMoveTiles.add(tileOnLeftPosition);
        if (currentOrientation != Orientation.UP && GamePanel.CLYDE.canGoThroughTile(tileBelowPosition))
            consideredMoveTiles.add(tileBelowPosition);
        if (currentOrientation != Orientation.LEFT && GamePanel.CLYDE.canGoThroughTile(tileOnRightPosition))
            consideredMoveTiles.add(tileOnRightPosition);

        return consideredMoveTiles;
    }

    private Point getBehindTile() {
        Orientation currentOrientation = GamePanel.CLYDE.getOrientation();
        Point currentPosition = GamePanel.CLYDE.getPosition();
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
        Point currentPosition = GamePanel.CLYDE.getPosition();
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
            return GamePanel.CLYDE.getOrientation();
    }

    private int getSquareDistanceFromPacMan() {
        Point dist = new Point(
                GamePanel.PAC_MAN.getPosition().x / GamePanel.TILE_SIZE - GamePanel.CLYDE.getPosition().x / GamePanel.TILE_SIZE,
                GamePanel.PAC_MAN.getPosition().y / GamePanel.TILE_SIZE - GamePanel.CLYDE.getPosition().y / GamePanel.TILE_SIZE);

        return dist.x * dist.x + dist.y * dist.y;
    }

    private Orientation getNextChaseMovementOrientation() {
        if (getSquareDistanceFromPacMan() >= 64) {
            Point targetTile = GamePanel.PAC_MAN.getPosition();
            targetTile.x = targetTile.x / GamePanel.TILE_SIZE;
            targetTile.y = targetTile.y / GamePanel.TILE_SIZE;
            Point nextMoveTile = getNextMoveTile(targetTile);

            return getOrientationToGoToTile(nextMoveTile);
        } else {
            return getNextScatterMovementOrientation();
        }
    }

    private Orientation getNextScatterMovementOrientation() {
        Point currentPosition = GamePanel.CLYDE.getPosition();
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
        Point currentPosition = GamePanel.CLYDE.getPosition();
        currentPosition.x = currentPosition.x / GamePanel.TILE_SIZE;
        currentPosition.y = currentPosition.y / GamePanel.TILE_SIZE;

        if (currentPosition.x != EATEN_TARGET_TILE.x || currentPosition.y != EATEN_TARGET_TILE.y)
            return getOrientationToGoToTile(getNextMoveTile(EATEN_TARGET_TILE));
        System.out.println("GO DOWN");
        return Orientation.DOWN;
    }
}
