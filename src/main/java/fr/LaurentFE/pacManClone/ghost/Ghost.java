package fr.LaurentFE.pacManClone.ghost;

import fr.LaurentFE.pacManClone.GameMap;
import fr.LaurentFE.pacManClone.GamePanel;
import fr.LaurentFE.pacManClone.Orientation;
import fr.LaurentFE.pacManClone.TileType;
import fr.LaurentFE.pacManClone.ghost.personality.GhostPersonality;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Ghost {
    private Orientation orientation;
    private final int moveSpeed;
    private final Rectangle hitBox;
    private final Color color;
    private GhostState state;
    private final GhostPersonality chasePersonality;
    private final Point scatterTargetTile;
    private  final Point eatenTargetTile = GameMap.getInstance().getGhostHouse();
    private final GameMap gameMap;
    private Point lastCrossroadTile;

    public Ghost(Point startingPosition,
                 Orientation startingOrientation,
                 int moveSpeed,
                 Color color,
                 GhostPersonality chasePersonality,
                 Point scatterTargetTile) {
        hitBox = new Rectangle(startingPosition.x, startingPosition.y, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
        orientation = startingOrientation;
        this.moveSpeed = moveSpeed;
        this.color = color;
        state = GhostState.CHASE;
        this.chasePersonality = chasePersonality;
        this.scatterTargetTile = scatterTargetTile;
        gameMap = GameMap.getInstance();
        lastCrossroadTile = new Point(0, 0);
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public Color getColor() {
        return color;
    }

    public Point getPosition() {
        return hitBox.getLocation();
    }

    public GhostState getState() {
        return state;
    }

    public Point getLastCrossroadTile() {
        return lastCrossroadTile;
    }

    public void move() {
        if (orientation == Orientation.LEFT) {
            hitBox.x = hitBox.x - moveSpeed;
        } else if (orientation == Orientation.RIGHT) {
            hitBox.x = hitBox.x + moveSpeed;
        } else if (orientation == Orientation.UP) {
            hitBox.y = hitBox.y - moveSpeed;
        } else if (orientation == Orientation.DOWN) {
            hitBox.y = hitBox.y + moveSpeed;
        }
    }

    public void bumpOutOfCollision(Point collisionTileMapPosition) {
        if (orientation == Orientation.LEFT) {
            hitBox.x = collisionTileMapPosition.x * GamePanel.TILE_SIZE + GamePanel.TILE_SIZE;
        } else if (orientation == Orientation.RIGHT) {
            hitBox.x = collisionTileMapPosition.x * GamePanel.TILE_SIZE - GamePanel.TILE_SIZE;
        } else if (orientation == Orientation.UP) {
            hitBox.y = collisionTileMapPosition.y * GamePanel.TILE_SIZE + GamePanel.TILE_SIZE;
        } else if (orientation == Orientation.DOWN) {
            hitBox.y = collisionTileMapPosition.y * GamePanel.TILE_SIZE - GamePanel.TILE_SIZE;
        }
    }

    private Rectangle getNextPathTileForOrientation(Orientation nextOrientation) {
        Point directionModifier;
        if (nextOrientation == Orientation.UP) {
            directionModifier = new Point(0, -1);
        } else if (nextOrientation == Orientation.LEFT) {
            directionModifier = new Point(-1, 0);
        } else if (nextOrientation == Orientation.DOWN) {
            directionModifier = new Point(0, 1);
        } else {
            directionModifier = new Point(1, 0);
        }
        Point tileAPosition = new Point(
                hitBox.x / GamePanel.TILE_SIZE + directionModifier.x,
                hitBox.y / GamePanel.TILE_SIZE + directionModifier.y);
        Point tileBPosition = new Point(
                hitBox.x / GamePanel.TILE_SIZE + directionModifier.x,
                hitBox.y / GamePanel.TILE_SIZE + directionModifier.y);
        Point tileCPosition = new Point(
                hitBox.x / GamePanel.TILE_SIZE + directionModifier.x,
                hitBox.y / GamePanel.TILE_SIZE + directionModifier.y);

        if (nextOrientation == Orientation.UP || nextOrientation == Orientation.DOWN) {
            tileAPosition.x -= 1;
            tileCPosition.x += 1;
        } else {
            tileAPosition.y -= 1;
            tileCPosition.y += 1;
        }

        if (canGoThroughTile(tileAPosition)) {
            return new Rectangle(
                    tileAPosition.x * GamePanel.TILE_SIZE,
                    tileAPosition.y * GamePanel.TILE_SIZE,
                    GamePanel.TILE_SIZE,
                    GamePanel.TILE_SIZE
            );
        } else if (canGoThroughTile(tileBPosition)) {
            return new Rectangle(
                    tileBPosition.x * GamePanel.TILE_SIZE,
                    tileBPosition.y * GamePanel.TILE_SIZE,
                    GamePanel.TILE_SIZE,
                    GamePanel.TILE_SIZE
            );
        } else if (canGoThroughTile(tileCPosition)) {
            return new Rectangle(
                    tileCPosition.x * GamePanel.TILE_SIZE,
                    tileCPosition.y * GamePanel.TILE_SIZE,
                    GamePanel.TILE_SIZE,
                    GamePanel.TILE_SIZE
            );
        } else {
            return new Rectangle();
        }
    }

    private boolean canGetIntoPath(Orientation nextOrientation) {
        Rectangle pathTile = getNextPathTileForOrientation(nextOrientation);
        if (pathTile.equals(new Rectangle()))
            return false;

        if (nextOrientation == Orientation.UP
                || nextOrientation == Orientation.DOWN) {
            if (pathTile.x - hitBox.x < moveSpeed
                    && pathTile.x - hitBox.x > -moveSpeed) {
                hitBox.x = pathTile.x;
                return true;
            }
        } else {
            if (pathTile.y - hitBox.y < moveSpeed
                    && pathTile.y - hitBox.y > -moveSpeed) {
                hitBox.y = pathTile.y;
                return true;
            }
        }
        return false;
    }

    private boolean isCurrentTileACrossroad() {
        HashMap<Orientation, Point> directionModifier = new HashMap<>();
        directionModifier.put(Orientation.UP, new Point(0, -1));
        directionModifier.put(Orientation.LEFT, new Point(-1, 0));
        directionModifier.put(Orientation.DOWN, new Point(0, 1));
        directionModifier.put(Orientation.RIGHT, new Point(1, 0));

        Point tileAbovePosition = new Point(
                hitBox.x / GamePanel.TILE_SIZE + directionModifier.get(Orientation.UP).x,
                hitBox.y / GamePanel.TILE_SIZE + directionModifier.get(Orientation.UP).y);
        Point tileOnLeftPosition = new Point(
                hitBox.x / GamePanel.TILE_SIZE + directionModifier.get(Orientation.LEFT).x,
                hitBox.y / GamePanel.TILE_SIZE + directionModifier.get(Orientation.LEFT).y);
        Point tileBelowPosition = new Point(
                hitBox.x / GamePanel.TILE_SIZE + directionModifier.get(Orientation.DOWN).x,
                hitBox.y / GamePanel.TILE_SIZE + directionModifier.get(Orientation.DOWN).y);
        Point tileOnRightPosition = new Point(
                hitBox.x / GamePanel.TILE_SIZE + directionModifier.get(Orientation.RIGHT).x,
                hitBox.y / GamePanel.TILE_SIZE + directionModifier.get(Orientation.RIGHT).y);

        int possibleDirections=0;
        if (canGoThroughTile(tileAbovePosition))
            possibleDirections++;
        if (canGoThroughTile(tileOnLeftPosition))
            possibleDirections++;
        if (canGoThroughTile(tileBelowPosition))
            possibleDirections++;
        if (canGoThroughTile(tileOnRightPosition))
            possibleDirections++;

        return possibleDirections > 2;
    }

    private boolean alreadyDecidedAtThisCrossroad() {
        Point currentTile = new Point(
                hitBox.x / GamePanel.TILE_SIZE,
                hitBox.y / GamePanel.TILE_SIZE);
        return getLastCrossroadTile().x == currentTile.x
                && getLastCrossroadTile().y == currentTile.y;
    }

    private boolean isFacingAWall() {
        HashMap<Orientation, Point> directionModifier = new HashMap<>();
        directionModifier.put(Orientation.UP, new Point(0, -1));
        directionModifier.put(Orientation.LEFT, new Point(-1, 0));
        directionModifier.put(Orientation.DOWN, new Point(0, 1));
        directionModifier.put(Orientation.RIGHT, new Point(1, 0));

        Point tileInFront = new Point(hitBox.x / GamePanel.TILE_SIZE, hitBox.y / GamePanel.TILE_SIZE);
        tileInFront.x += directionModifier.get(orientation).x;
        tileInFront.y += directionModifier.get(orientation).y;

        return !canGoThroughTile(tileInFront);
    }

    public ArrayList<Point> getConsideredMoveTiles() {
        HashMap<Orientation, Point> directionModifier = new HashMap<>();
        directionModifier.put(Orientation.UP, new Point(0, -1));
        directionModifier.put(Orientation.LEFT, new Point(-1, 0));
        directionModifier.put(Orientation.DOWN, new Point(0, 1));
        directionModifier.put(Orientation.RIGHT, new Point(1, 0));

        Point tileAbovePosition = new Point(
                hitBox.x / GamePanel.TILE_SIZE + directionModifier.get(Orientation.UP).x,
                hitBox.y / GamePanel.TILE_SIZE + directionModifier.get(Orientation.UP).y);
        Point tileOnLeftPosition = new Point(
                hitBox.x / GamePanel.TILE_SIZE + directionModifier.get(Orientation.LEFT).x,
                hitBox.y / GamePanel.TILE_SIZE + directionModifier.get(Orientation.LEFT).y);
        Point tileBelowPosition = new Point(
                hitBox.x / GamePanel.TILE_SIZE + directionModifier.get(Orientation.DOWN).x,
                hitBox.y / GamePanel.TILE_SIZE + directionModifier.get(Orientation.DOWN).y);
        Point tileOnRightPosition = new Point(
                hitBox.x / GamePanel.TILE_SIZE + directionModifier.get(Orientation.RIGHT).x,
                hitBox.y / GamePanel.TILE_SIZE + directionModifier.get(Orientation.RIGHT).y);

        ArrayList<Point> consideredMoveTiles = new ArrayList<>();
        if (orientation != Orientation.DOWN && GamePanel.CLYDE.canGoThroughTile(tileAbovePosition))
            consideredMoveTiles.add(tileAbovePosition);
        if (orientation != Orientation.RIGHT && GamePanel.CLYDE.canGoThroughTile(tileOnLeftPosition))
            consideredMoveTiles.add(tileOnLeftPosition);
        if (orientation != Orientation.UP && GamePanel.CLYDE.canGoThroughTile(tileBelowPosition))
            consideredMoveTiles.add(tileBelowPosition);
        if (orientation != Orientation.LEFT && GamePanel.CLYDE.canGoThroughTile(tileOnRightPosition))
            consideredMoveTiles.add(tileOnRightPosition);

        return consideredMoveTiles;
    }

    public Point getNextMoveTile(Point targetTile) {
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

    public Orientation getOrientationToGoToTile(Point tile) {
        Point currentPosition = new Point(
                hitBox.x / GamePanel.TILE_SIZE,
                hitBox.y / GamePanel.TILE_SIZE);
        if (tile.x == currentPosition.x && tile.y < currentPosition.y)
            return Orientation.UP;
        else if (tile.x < currentPosition.x && tile.y == currentPosition.y)
            return Orientation.LEFT;
        else if (tile.x == currentPosition.x && tile.y > currentPosition.y)
            return Orientation.DOWN;
        else if (tile.x > currentPosition.x && tile.y == currentPosition.y)
            return Orientation.RIGHT;
        else
            return orientation;
    }

    public Orientation getNextScatterMovementOrientation() {
        Point nextMoveTile = getNextMoveTile(scatterTargetTile);
        return getOrientationToGoToTile(nextMoveTile);
    }

    public Point getBehindTile() {
        HashMap<Orientation, Point> directionModifier = new HashMap<>();
        directionModifier.put(Orientation.UP, new Point(0, -1));
        directionModifier.put(Orientation.LEFT, new Point(-1, 0));
        directionModifier.put(Orientation.DOWN, new Point(0, 1));
        directionModifier.put(Orientation.RIGHT, new Point(1, 0));
        Point tileAbovePosition = new Point(
                hitBox.x / GamePanel.TILE_SIZE + directionModifier.get(Orientation.UP).x,
                hitBox.y / GamePanel.TILE_SIZE + directionModifier.get(Orientation.UP).y);
        Point tileOnLeftPosition = new Point(
                hitBox.x / GamePanel.TILE_SIZE + directionModifier.get(Orientation.LEFT).x,
                hitBox.y / GamePanel.TILE_SIZE + directionModifier.get(Orientation.LEFT).y);
        Point tileBelowPosition = new Point(
                hitBox.x / GamePanel.TILE_SIZE + directionModifier.get(Orientation.DOWN).x,
                hitBox.y / GamePanel.TILE_SIZE + directionModifier.get(Orientation.DOWN).y);
        Point tileOnRightPosition = new Point(
                hitBox.x / GamePanel.TILE_SIZE + directionModifier.get(Orientation.RIGHT).x,
                hitBox.y / GamePanel.TILE_SIZE + directionModifier.get(Orientation.RIGHT).y);
        return switch (orientation) {
            case DOWN -> tileAbovePosition;
            case RIGHT -> tileOnLeftPosition;
            case UP -> tileBelowPosition;
            case LEFT -> tileOnRightPosition;
        };
    }

    public Orientation getNextFrightenedMovementOrientation() {
        ArrayList<Point> consideredMoveTiles = getConsideredMoveTiles();
        if (consideredMoveTiles.size() > 1) {
            consideredMoveTiles.add(getBehindTile());
            Random random = new Random();
            return getOrientationToGoToTile(consideredMoveTiles.get(random.nextInt(consideredMoveTiles.size())));
        }
        return getOrientationToGoToTile(consideredMoveTiles.getFirst());
    }

    public Orientation getNextEatenMovementOrientation() {
        Point currentPosition = new Point(
                hitBox.x / GamePanel.TILE_SIZE,
                hitBox.y / GamePanel.TILE_SIZE);

        if (currentPosition.x != eatenTargetTile.x || currentPosition.y != eatenTargetTile.y)
            return getOrientationToGoToTile(getNextMoveTile(eatenTargetTile));
        return Orientation.DOWN;
    }

    public Orientation getNextMovementOrientation() {
        if (alreadyDecidedAtThisCrossroad()
                && !isFacingAWall())
            return orientation;

        return switch (state) {
            case CHASE -> chasePersonality.getNextMovementOrientation();
            case SCATTER -> getNextScatterMovementOrientation();
            case FRIGHTENED -> getNextFrightenedMovementOrientation();
            case EATEN -> getNextEatenMovementOrientation();
        };
    }

    private boolean mustChangeDirection() {
        Orientation nextOrientation = getNextMovementOrientation();
        if (nextOrientation == orientation)
            return false;

        if (canGetIntoPath(nextOrientation)) {
            if (isCurrentTileACrossroad())
                lastCrossroadTile = new Point(hitBox.x / GamePanel.TILE_SIZE, hitBox.y / GamePanel.TILE_SIZE);
            setOrientation(nextOrientation);
            updatePosition();
            return true;
        }
        return false;
    }

    private void updatePosition() {
        move();
        Point upperLeftTile = new Point(
                (hitBox.x / GamePanel.TILE_SIZE),
                (hitBox.y / GamePanel.TILE_SIZE));
        Point upperRightTile = new Point(
                ((hitBox.x + hitBox.width-1) / GamePanel.TILE_SIZE),
                (hitBox.y / GamePanel.TILE_SIZE));
        Point lowerLeftTile = new Point(
                (hitBox.x / GamePanel.TILE_SIZE),
                ((hitBox.y + hitBox.height-1) / GamePanel.TILE_SIZE));
        Point lowerRightTile = new Point(
                ((hitBox.x + hitBox.width-1) / GamePanel.TILE_SIZE),
                ((hitBox.y + hitBox.height-1) / GamePanel.TILE_SIZE));

        if (!canGoThroughTile(upperLeftTile)) {
            bumpOutOfCollision(upperLeftTile);
        } else if (!canGoThroughTile(upperRightTile)) {
            bumpOutOfCollision(upperRightTile);
        } else if (!canGoThroughTile(lowerLeftTile)) {
            bumpOutOfCollision(lowerLeftTile);
        } else if (!canGoThroughTile(lowerRightTile)) {
            bumpOutOfCollision(lowerRightTile);
        }
    }

    public boolean canGoThroughTile(Point tileCoords) {
        return gameMap.getTile(tileCoords) == TileType.PATH
                || gameMap.getTile(tileCoords) == TileType.GHOSTHOUSE
                || (gameMap.getTile(tileCoords) == TileType.DOOR && state == GhostState.EATEN);
    }

    public void update() {
        if (!mustChangeDirection()) {
            updatePosition();
        }
    }
}
