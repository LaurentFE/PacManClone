package fr.LaurentFE.pacManClone.ghost.personality;

import fr.LaurentFE.pacManClone.GamePanel;
import fr.LaurentFE.pacManClone.Orientation;

import java.awt.*;

public class Pinky implements GhostPersonality {

    public Pinky() {}

    @Override
    public Orientation getNextMovementOrientation() {
        Point targetTile = GamePanel.PAC_MAN.getPosition();
        targetTile.x = targetTile.x / GamePanel.TILE_SIZE;
        targetTile.y = targetTile.y / GamePanel.TILE_SIZE;
        switch (GamePanel.PAC_MAN.getOrientation()) {
            case UP -> targetTile.y -= 4;
            case LEFT -> targetTile.x -= 4;
            case DOWN -> targetTile.y +=4;
            case RIGHT -> targetTile.x +=4;
        }
        Point nextMoveTile = GamePanel.PINKY.getNextMoveTile(targetTile);

        return GamePanel.PINKY.getOrientationToGoToTile(nextMoveTile);
    }
}
