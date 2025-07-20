package fr.LaurentFE.pacManClone;

import java.awt.*;

public class Ghost {
    private Orientation orientation;
    private final int moveSpeed;
    private final Rectangle hitBox;
    private final int size;
    private final Color color;

    public Ghost(Point startingPosition, int tileSize, int size, Orientation startingOrientation, int moveSpeed, Color color) {
        this.size = size;
        hitBox = new Rectangle(startingPosition.x, startingPosition.y, tileSize, tileSize);
        orientation = startingOrientation;
        this.moveSpeed = moveSpeed;
        this.color = color;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public int getSize() {
        return size;
    }

    public Color getColor() {
        return color;
    }

    public Rectangle getHitBox() {
        return hitBox;
    }

    public int getMoveSpeed() {
        return moveSpeed;
    }

    public Point getPosition() {
        return hitBox.getLocation();
    }
}
