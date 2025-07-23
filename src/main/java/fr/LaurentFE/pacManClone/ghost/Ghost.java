package fr.LaurentFE.pacManClone.ghost;

import fr.LaurentFE.pacManClone.GamePanel;
import fr.LaurentFE.pacManClone.Orientation;
import fr.LaurentFE.pacManClone.ghost.personality.GhostPersonality;

import java.awt.*;

public class Ghost {
    private Orientation orientation;
    private final int moveSpeed;
    private final Rectangle hitBox;
    private final Color color;
    private GhostState state;
    private final GhostPersonality personality;

    public Ghost(Point startingPosition, Orientation startingOrientation, int moveSpeed, Color color, GhostPersonality personality) {
        hitBox = new Rectangle(startingPosition.x, startingPosition.y, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
        orientation = startingOrientation;
        this.moveSpeed = moveSpeed;
        this.color = color;
        state = GhostState.CHASE;
        this.personality = personality;
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

    public Rectangle getHitBox() {
        return hitBox;
    }

    public int getMoveSpeed() {
        return moveSpeed;
    }

    public Point getPosition() {
        return hitBox.getLocation();
    }

    public GhostState getState() {
        return state;
    }
}
