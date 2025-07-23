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

    public PacMan(Point startingPosition, Orientation startingOrientation, int moveSpeed) {
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

    public Orientation getOrientation() {
        return orientation;
    }

    public int getCurrentMouthAngle() {
        return currentMouthAngle;
    }

    public Point getPosition() {
        return hitBox.getLocation();
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
                hitBox.y =pathTile.y;
                return true;
            }
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
        } else {
            animateMouth();
        }
    }

    public boolean canGoThroughTile(Point tileCoords) {
        return gameMap.getTile(tileCoords) == TileType.PATH
                || gameMap.getTile(tileCoords) == TileType.GHOSTHOUSE;
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
