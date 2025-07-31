package fr.LaurentFE.pacManClone.entities;

import fr.LaurentFE.pacManClone.map.Position;
import fr.LaurentFE.pacManClone.map.TileIndex;
import fr.LaurentFE.pacManClone.map.TileType;

public class PacMan extends MovingEntity {
    private final int maxMouthAngle;
    private int currentMouthAngle;
    private int mouthAngleIncrement;
    private boolean isAlive;
    private int lives;
    private boolean deathAnimationFinished;

    public PacMan(Position startingPosition, Orientation startingOrientation, int moveSpeed, int lives) {
        super(startingPosition, startingOrientation, moveSpeed);
        maxMouthAngle = 90;
        currentMouthAngle = maxMouthAngle;
        mouthAngleIncrement = -5;
        isAlive = true;
        this.lives = lives;
        deathAnimationFinished = false;
    }

    public boolean isDeathAnimationFinished() {
        return deathAnimationFinished;
    }

    public int getLives() {
        return lives;
    }

    public boolean isAlive() {
        return isAlive;
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

    public int getCurrentMouthAngle() {
        return currentMouthAngle;
    }

    protected void updatePosition() {
        move();
        if (!checkForWallCollisions())
            animateMouth();
    }

    public boolean canGoThroughTile(TileIndex tileIndex) {
        return gameMap.getTile(tileIndex) == TileType.PATH;
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

    public void kill() {
        isAlive = false;
        deathAnimationFinished = false;
    }

    private boolean animateDeath() {
        orientation = Orientation.UP;
        if (mouthAngleIncrement < 0)
            mouthAngleIncrement *= -1;
        currentMouthAngle += mouthAngleIncrement;
        return currentMouthAngle >= 360;
    }

    public void update(Orientation nextOrientation) {
        if (isAlive) {
            if (nextOrientation != null && !tryToChangeDirection(nextOrientation)) {
                updatePosition();
            }
        } else {
            if (!animateDeath())
               return;
            lives--;
            deathAnimationFinished = true;
        }
    }
}
