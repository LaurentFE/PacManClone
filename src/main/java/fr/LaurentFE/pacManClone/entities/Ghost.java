package fr.LaurentFE.pacManClone.entities;

import fr.LaurentFE.pacManClone.*;
import fr.LaurentFE.pacManClone.entities.ghostPersonality.GhostPersonality;
import fr.LaurentFE.pacManClone.map.GameMap;
import fr.LaurentFE.pacManClone.map.Position;
import fr.LaurentFE.pacManClone.map.TileIndex;
import fr.LaurentFE.pacManClone.map.TileType;

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
    private final TileIndex scatterTargetTile;
    private  final TileIndex eatenTargetTile = GameMap.getInstance().getGhostHouse();
    private final GameMap gameMap;
    private TileIndex lastCrossroadTile;

    public Ghost(Position startingPosition,
                 Orientation startingOrientation,
                 int moveSpeed,
                 Color color,
                 GhostPersonality chasePersonality,
                 TileIndex scatterTargetTile) {
        hitBox = new Rectangle(startingPosition.x, startingPosition.y, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
        orientation = startingOrientation;
        this.moveSpeed = moveSpeed;
        this.color = color;
        state = GhostState.CHASE;
        this.chasePersonality = chasePersonality;
        this.scatterTargetTile = scatterTargetTile;
        gameMap = GameMap.getInstance();
        lastCrossroadTile = new TileIndex(0, 0);
    }

    public Rectangle getHitBox() {
        return hitBox;
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

    public Position getPosition() {
        return new Position(hitBox.x, hitBox.y);
    }

    public GhostState getState() {
        return state;
    }

    public TileIndex getLastCrossroadTile() {
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

    public void bumpOutOfCollision(TileIndex collisionTile) {
        Position collisionPosition = collisionTile.toPosition();
        if (orientation == Orientation.LEFT) {
            hitBox.x = collisionPosition.x + GamePanel.TILE_SIZE;
        } else if (orientation == Orientation.RIGHT) {
            hitBox.x = collisionPosition.x - GamePanel.TILE_SIZE;
        } else if (orientation == Orientation.UP) {
            hitBox.y = collisionPosition.y + GamePanel.TILE_SIZE;
        } else if (orientation == Orientation.DOWN) {
            hitBox.y = collisionPosition.y - GamePanel.TILE_SIZE;
        }
    }

    private Rectangle getNextPathTileForOrientation(Orientation nextOrientation) {
        TileIndex directionModifier;
        if (nextOrientation == Orientation.UP) {
            directionModifier = new TileIndex(0, -1);
        } else if (nextOrientation == Orientation.LEFT) {
            directionModifier = new TileIndex(-1, 0);
        } else if (nextOrientation == Orientation.DOWN) {
            directionModifier = new TileIndex(0, 1);
        } else {
            directionModifier = new TileIndex(1, 0);
        }
        TileIndex tileAIndex = new Position(hitBox.x, hitBox.y).toTileIndex()
                .add(directionModifier);
        TileIndex tileBIndex = new Position(hitBox.x, hitBox.y).toTileIndex()
                .add(directionModifier);
        TileIndex tileCIndex = new Position(hitBox.x, hitBox.y).toTileIndex()
                .add(directionModifier);

        if (nextOrientation == Orientation.UP || nextOrientation == Orientation.DOWN) {
            tileAIndex.x -= 1;
            tileCIndex.x += 1;
        } else {
            tileAIndex.y -= 1;
            tileCIndex.y += 1;
        }

        Position tileAPosition = tileAIndex.toPosition();
        Position tileBPosition = tileBIndex.toPosition();
        Position tileCPosition = tileCIndex.toPosition();

        if (canGoThroughTile(tileAIndex)) {
            return new Rectangle(
                    tileAPosition.x,
                    tileAPosition.y,
                    GamePanel.TILE_SIZE,
                    GamePanel.TILE_SIZE
            );
        } else if (canGoThroughTile(tileBIndex)) {
            return new Rectangle(
                    tileBPosition.x,
                    tileBPosition.y,
                    GamePanel.TILE_SIZE,
                    GamePanel.TILE_SIZE
            );
        } else if (canGoThroughTile(tileCIndex)) {
            return new Rectangle(
                    tileCPosition.x,
                    tileCPosition.y,
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
        HashMap<Orientation, TileIndex> directionModifier = new HashMap<>();
        directionModifier.put(Orientation.UP, new TileIndex(0, -1));
        directionModifier.put(Orientation.LEFT, new TileIndex(-1, 0));
        directionModifier.put(Orientation.DOWN, new TileIndex(0, 1));
        directionModifier.put(Orientation.RIGHT, new TileIndex(1, 0));

        TileIndex tileAbovePosition = new Position(hitBox.x, hitBox.y).toTileIndex()
                .add(directionModifier.get(Orientation.UP));
        TileIndex tileOnLeftPosition = new Position(hitBox.x, hitBox.y).toTileIndex()
                .add(directionModifier.get(Orientation.LEFT));
        TileIndex tileBelowPosition = new Position(hitBox.x, hitBox.y).toTileIndex()
                .add(directionModifier.get(Orientation.DOWN));
        TileIndex tileOnRightPosition = new Position(hitBox.x, hitBox.y).toTileIndex()
                .add(directionModifier.get(Orientation.RIGHT));

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
        TileIndex currentTile = new Position(hitBox.x, hitBox.y).toTileIndex();
        return getLastCrossroadTile().x == currentTile.x
                && getLastCrossroadTile().y == currentTile.y;
    }

    private boolean isFacingAWall() {
        HashMap<Orientation, TileIndex> directionModifier = new HashMap<>();
        directionModifier.put(Orientation.UP, new TileIndex(0, -1));
        directionModifier.put(Orientation.LEFT, new TileIndex(-1, 0));
        directionModifier.put(Orientation.DOWN, new TileIndex(0, 1));
        directionModifier.put(Orientation.RIGHT, new TileIndex(1, 0));

        TileIndex tileInFront = new Position(hitBox.x, hitBox.y).toTileIndex()
                .add(directionModifier.get(orientation));

        return !canGoThroughTile(tileInFront);
    }

    public ArrayList<TileIndex> getConsideredMoveTiles() {
        HashMap<Orientation, TileIndex> directionModifier = new HashMap<>();
        directionModifier.put(Orientation.UP, new TileIndex(0, -1));
        directionModifier.put(Orientation.LEFT, new TileIndex(-1, 0));
        directionModifier.put(Orientation.DOWN, new TileIndex(0, 1));
        directionModifier.put(Orientation.RIGHT, new TileIndex(1, 0));

        TileIndex tileAbovePosition = new Position(hitBox.x, hitBox.y).toTileIndex()
                .add(directionModifier.get(Orientation.UP));
        TileIndex tileOnLeftPosition = new Position(hitBox.x, hitBox.y).toTileIndex()
                .add(directionModifier.get(Orientation.LEFT));
        TileIndex tileBelowPosition = new Position(hitBox.x, hitBox.y).toTileIndex()
                .add(directionModifier.get(Orientation.DOWN));
        TileIndex tileOnRightPosition = new Position(hitBox.x, hitBox.y).toTileIndex()
                .add(directionModifier.get(Orientation.RIGHT));

        ArrayList<TileIndex> consideredMoveTiles = new ArrayList<>();
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

    public TileIndex getNextMoveTile(TileIndex targetTile) {
        TileIndex finalTile = new TileIndex(0,0);
        int squaredDist = Integer.MAX_VALUE;
        for (TileIndex consideredTile : getConsideredMoveTiles()) {
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

    public Orientation getOrientationToGoToTile(TileIndex tile) {
        TileIndex currentTile = new Position(hitBox.x, hitBox.y).toTileIndex();
        if (tile.x == currentTile.x && tile.y < currentTile.y)
            return Orientation.UP;
        else if (tile.x < currentTile.x && tile.y == currentTile.y)
            return Orientation.LEFT;
        else if (tile.x == currentTile.x && tile.y > currentTile.y)
            return Orientation.DOWN;
        else if (tile.x > currentTile.x && tile.y == currentTile.y)
            return Orientation.RIGHT;
        else
            return orientation;
    }

    public Orientation getNextScatterMovementOrientation() {
        TileIndex nextMoveTile = getNextMoveTile(scatterTargetTile);
        return getOrientationToGoToTile(nextMoveTile);
    }

    public TileIndex getBehindTile() {
        HashMap<Orientation, TileIndex> directionModifier = new HashMap<>();
        directionModifier.put(Orientation.UP, new TileIndex(0, -1));
        directionModifier.put(Orientation.LEFT, new TileIndex(-1, 0));
        directionModifier.put(Orientation.DOWN, new TileIndex(0, 1));
        directionModifier.put(Orientation.RIGHT, new TileIndex(1, 0));

        TileIndex tileAbovePosition = new Position(hitBox.x, hitBox.y).toTileIndex()
                .add(directionModifier.get(Orientation.UP));
        TileIndex tileOnLeftPosition = new Position(hitBox.x, hitBox.y).toTileIndex()
                .add(directionModifier.get(Orientation.LEFT));
        TileIndex tileBelowPosition = new Position(hitBox.x, hitBox.y).toTileIndex()
                .add(directionModifier.get(Orientation.DOWN));
        TileIndex tileOnRightPosition = new Position(hitBox.x, hitBox.y).toTileIndex()
                .add(directionModifier.get(Orientation.RIGHT));
        return switch (orientation) {
            case DOWN -> tileAbovePosition;
            case RIGHT -> tileOnLeftPosition;
            case UP -> tileBelowPosition;
            case LEFT -> tileOnRightPosition;
        };
    }

    public Orientation getNextFrightenedMovementOrientation() {
        ArrayList<TileIndex> consideredMoveTiles = getConsideredMoveTiles();
        if (consideredMoveTiles.size() > 1) {
            consideredMoveTiles.add(getBehindTile());
            Random random = new Random();
            return getOrientationToGoToTile(consideredMoveTiles.get(random.nextInt(consideredMoveTiles.size())));
        }
        return getOrientationToGoToTile(consideredMoveTiles.getFirst());
    }

    public Orientation getNextEatenMovementOrientation() {
        TileIndex currentTile = new Position(hitBox.x, hitBox.y).toTileIndex();

        if (currentTile.x != eatenTargetTile.x || currentTile.y != eatenTargetTile.y)
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
                lastCrossroadTile = new Position(hitBox.x, hitBox.y).toTileIndex();
            setOrientation(nextOrientation);
            updatePosition();
            return true;
        }
        return false;
    }

    private void updatePosition() {
        move();
        TileIndex upperLeftTile = new Position(hitBox.x, hitBox.y).toTileIndex();
        TileIndex upperRightTile = new Position(hitBox.x + hitBox.width-1, hitBox.y).toTileIndex();
        TileIndex lowerLeftTile = new Position(hitBox.x, hitBox.y + hitBox.height-1).toTileIndex();
        TileIndex lowerRightTile = new Position(hitBox.x + hitBox.width-1, hitBox.y + hitBox.height-1).toTileIndex();

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

    public boolean canGoThroughTile(TileIndex tileIndex) {
        return gameMap.getTile(tileIndex) == TileType.PATH
                || gameMap.getTile(tileIndex) == TileType.GHOSTHOUSE
                || (gameMap.getTile(tileIndex) == TileType.DOOR && state == GhostState.EATEN);
    }

    public void update() {
        if (!mustChangeDirection()) {
            updatePosition();
        }
    }
}
