package fr.LaurentFE.pacManClone.ghost.personality;

import fr.LaurentFE.pacManClone.GamePanel;
import fr.LaurentFE.pacManClone.Orientation;

import java.awt.*;

public final class Blinky implements GhostPersonality {

    public Blinky() {}

    @Override
    public Orientation getNextMovementOrientation() {
        Point targetTile = GamePanel.PAC_MAN.getPosition();
        targetTile.x = targetTile.x / GamePanel.TILE_SIZE;
        targetTile.y = targetTile.y / GamePanel.TILE_SIZE;
        Point nextMoveTile = GamePanel.BLINKY.getNextMoveTile(targetTile);

        return GamePanel.BLINKY.getOrientationToGoToTile(nextMoveTile);
    }
}
