package fr.LaurentFE.pacManClone.entities;

import fr.LaurentFE.pacManClone.GamePanel;
import fr.LaurentFE.pacManClone.map.GameMap;
import fr.LaurentFE.pacManClone.map.Position;
import fr.LaurentFE.pacManClone.map.TileIndex;

import java.awt.*;

public abstract class MovingEntity {
    protected Orientation orientation;
    protected final Rectangle hitBox;
    protected final GameMap gameMap;
    protected final int moveSpeed;

    public MovingEntity(Position startingPosition,
                        Orientation startingOrientation,
                        int moveSpeed) {
        hitBox = new Rectangle(startingPosition.x, startingPosition.y, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
        orientation = startingOrientation;
        this.moveSpeed = moveSpeed;
        gameMap = GameMap.getInstance();
    }

    public Rectangle getHitBox() {
        return hitBox;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public Position getPosition() {
        return new Position(hitBox.x, hitBox.y);
    }

    public void move() {
        if (orientation == Orientation.LEFT) {
            hitBox.x = hitBox.x - moveSpeed;
            if (hitBox.x < -GamePanel.TILE_SIZE)
                hitBox.x = (gameMap.getMapWidthTile() + 1) * GamePanel.TILE_SIZE + hitBox.x;
        } else if (orientation == Orientation.RIGHT) {
            hitBox.x = hitBox.x + moveSpeed;
            if (hitBox.x >= gameMap.getMapWidthTile() * GamePanel.TILE_SIZE)
                hitBox.x = hitBox.x - (gameMap.getMapWidthTile() + 1) * GamePanel.TILE_SIZE;
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

    protected void tileLoopAroundHorizontal(TileIndex tile) {
        if (tile.x < 0)
            tile.x = gameMap.getMapWidthTile() - 1;
        if (tile.x >= gameMap.getMapWidthTile())
            tile.x = 0;
    }

    protected Rectangle getNextPathTileForOrientation(Orientation nextOrientation) {
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

        tileLoopAroundHorizontal(tileAIndex);
        tileLoopAroundHorizontal(tileBIndex);
        tileLoopAroundHorizontal(tileCIndex);

        Position tileAPosition = tileAIndex.toPosition();
        Position tileBPosition = tileBIndex.toPosition();
        Position tileCPosition = tileCIndex.toPosition();

        if (canGoThroughTile(tileBIndex)) {
            return new Rectangle(
                    tileBPosition.x,
                    tileBPosition.y,
                    GamePanel.TILE_SIZE,
                    GamePanel.TILE_SIZE
            );
        } else if (canGoThroughTile(tileAIndex)) {
            return new Rectangle(
                    tileAPosition.x,
                    tileAPosition.y,
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

    protected boolean canGetIntoPath(Orientation nextOrientation) {
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

    protected boolean checkForWallCollisions() {
        TileIndex upperLeftTile = new Position(hitBox.x, hitBox.y).toTileIndex();
        TileIndex upperRightTile = new Position(hitBox.x + hitBox.width-1, hitBox.y).toTileIndex();
        TileIndex lowerLeftTile = new Position(hitBox.x, hitBox.y + hitBox.height-1).toTileIndex();
        TileIndex lowerRightTile = new Position(hitBox.x + hitBox.width-1, hitBox.y + hitBox.height-1).toTileIndex();

        tileLoopAroundHorizontal(upperLeftTile);
        tileLoopAroundHorizontal(upperRightTile);
        tileLoopAroundHorizontal(lowerLeftTile);
        tileLoopAroundHorizontal(lowerRightTile);

        if (!canGoThroughTile(upperLeftTile)) {
            bumpOutOfCollision(upperLeftTile);
            return true;
        } else if (!canGoThroughTile(upperRightTile)) {
            bumpOutOfCollision(upperRightTile);
            return true;
        } else if (!canGoThroughTile(lowerLeftTile)) {
            bumpOutOfCollision(lowerLeftTile);
            return true;
        } else if (!canGoThroughTile(lowerRightTile)) {
            bumpOutOfCollision(lowerRightTile);
            return true;
        }
        return false;
    }

    protected void updatePosition() {
        move();
        checkForWallCollisions();
    }

    public abstract boolean canGoThroughTile(TileIndex tileIndex);
}
