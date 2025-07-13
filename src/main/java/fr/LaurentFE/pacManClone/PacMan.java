package fr.LaurentFE.pacManClone;

public class PacMan {
    private int x;
    private int y;
    private Orientation orientation;
    private final int maxMouthAngle;
    private int currentMouthAngle;
    private int mouthAngleIncrement;

    public PacMan(int startingX, int startingY, Orientation startingOrientation) {
        x = startingX;
        y = startingY;
        orientation = startingOrientation;
        maxMouthAngle = 90;
        currentMouthAngle = maxMouthAngle;
        mouthAngleIncrement = -9;
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


    public int getX() {
        return x;
    }

    public void offsetX(int x) {
        this.x += x;
    }

    public int getY() {
        return y;
    }

    public void offsetY(int y) {
        this.y += y;
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
}
