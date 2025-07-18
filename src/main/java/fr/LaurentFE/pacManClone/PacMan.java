package fr.LaurentFE.pacManClone;

import java.awt.*;

public class PacMan {
    private Orientation orientation;
    private final int maxMouthAngle;
    private int currentMouthAngle;
    private int mouthAngleIncrement;
    private final int moveSpeed;
    private final Rectangle hitBox;
    private final int tileSize;
    private final int size;

    public PacMan(Point startingPosition, int tileSize, int size, Orientation startingOrientation, int moveSpeed) {
        this.size = size;
        this.tileSize = tileSize;
        hitBox = new Rectangle(startingPosition.x, startingPosition.y, tileSize, tileSize);
        orientation = startingOrientation;
        this.moveSpeed = moveSpeed;
        maxMouthAngle = 90;
        currentMouthAngle = maxMouthAngle;
        mouthAngleIncrement = -5;
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
            hitBox.x = collisionTileMapPosition.x * tileSize + tileSize;
        } else if (orientation == Orientation.RIGHT) {
            hitBox.x = collisionTileMapPosition.x * tileSize - tileSize;
        } else if (orientation == Orientation.UP) {
            hitBox.y = collisionTileMapPosition.y * tileSize + tileSize;
        } else if (orientation == Orientation.DOWN) {
            hitBox.y = collisionTileMapPosition.y * tileSize - tileSize;
        }
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public int getCurrentMouthAngle() {
        return currentMouthAngle;
    }

    public Rectangle getHitBox() {
        return hitBox;
    }

    public int getSize() {
        return size;
    }

    public Point getPosition() {
        return hitBox.getLocation();
    }

    public int getMoveSpeed() {
        return moveSpeed;
    }

    public void setX(int x) {
        hitBox.x = x;
    }

    public void setY(int y) {
        hitBox.y = y;
    }
}
