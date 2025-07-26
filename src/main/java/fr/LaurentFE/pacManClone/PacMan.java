package fr.LaurentFE.pacManClone;

import java.awt.*;

public class PacMan {
    private Orientation orientation;
    private final int maxMouthAngle;
    private int currentMouthAngle;
    private int mouthAngleIncrement;
    private final int moveSpeed;
    private final Rectangle hitBox;
    private final GameMap gameMap;

    public PacMan(Position startingPosition, Orientation startingOrientation, int moveSpeed) {
        hitBox = new Rectangle(startingPosition.x, startingPosition.y, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
        orientation = startingOrientation;
        this.moveSpeed = moveSpeed;
        maxMouthAngle = 90;
        currentMouthAngle = maxMouthAngle;
        mouthAngleIncrement = -5;
        gameMap = GameMap.getInstance();
    }

    public void animateMouth() {
        currentMouthAngle += mouthAngleIncrement;
        if (currentMouthAngle >= maxMouthAngle) {
            currentMouthAngle = maxMouthAngle;
            mouthAngleIncrement *= -1;
        } else if (currentMouthAngle <= 0) {
            currentMouthAngle = 0;
            mouthAngleIncrement *= -1;
        }
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

    public void bumpOutOfCollision(TileIndex collisionTileIndex) {
        Position collisionTilePosition = collisionTileIndex.toPosition();
        if (orientation == Orientation.LEFT) {
            hitBox.x = collisionTilePosition.x + GamePanel.TILE_SIZE;
        } else if (orientation == Orientation.RIGHT) {
            hitBox.x = collisionTilePosition.x - GamePanel.TILE_SIZE;
        } else if (orientation == Orientation.UP) {
            hitBox.y = collisionTilePosition.y + GamePanel.TILE_SIZE;
        } else if (orientation == Orientation.DOWN) {
            hitBox.y = collisionTilePosition.y - GamePanel.TILE_SIZE;
        }
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public int getCurrentMouthAngle() {
        return currentMouthAngle;
    }

    public Position getPosition() {
        return new Position(hitBox.x, hitBox.y);
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
                hitBox.y =pathTile.y;
                return true;
            }
        }
        return false;
    }

    private void updatePosition() {
        move();
        TileIndex upperLeftTile = new Position(hitBox.x,hitBox.y).toTileIndex();
        TileIndex upperRightTile = new Position(hitBox.x + hitBox.width-1, hitBox.y).toTileIndex();
        TileIndex lowerLeftTile = new Position(hitBox.x, hitBox.y + hitBox.height-1).toTileIndex();
        TileIndex lowerRightTile = new Position(hitBox.x + hitBox.width-1,hitBox.y + hitBox.height-1).toTileIndex();

        if (!canGoThroughTile(upperLeftTile)) {
            bumpOutOfCollision(upperLeftTile);
        } else if (!canGoThroughTile(upperRightTile)) {
            bumpOutOfCollision(upperRightTile);
        } else if (!canGoThroughTile(lowerLeftTile)) {
            bumpOutOfCollision(lowerLeftTile);
        } else if (!canGoThroughTile(lowerRightTile)) {
            bumpOutOfCollision(lowerRightTile);
        } else {
            animateMouth();
        }
    }

    public boolean canGoThroughTile(TileIndex tileIndex) {
        return gameMap.getTile(tileIndex) == TileType.PATH
                || gameMap.getTile(tileIndex) == TileType.GHOSTHOUSE;
    }

    private boolean tryToChangeDirection(Orientation nextOrientation) {
        if (nextOrientation == orientation)
            return false;

        if (canGetIntoPath(nextOrientation)) {
            orientation = nextOrientation;
            updatePosition();
            return true;
        }
        return false;
    }

    public void update(Orientation nextOrientation) {
        if (!tryToChangeDirection(nextOrientation)) {
            updatePosition();
        }
    }
}
