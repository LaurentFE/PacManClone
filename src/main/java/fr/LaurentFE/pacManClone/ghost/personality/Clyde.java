package fr.LaurentFE.pacManClone.ghost.personality;

import fr.LaurentFE.pacManClone.GamePanel;
import fr.LaurentFE.pacManClone.Orientation;

import java.awt.*;

public class Clyde implements GhostPersonality {

    public Clyde() {}

    @Override
    public Orientation getNextMovementOrientation() {
        if (getSquareDistanceFromPacMan() >= 64) {
            Point targetTile = GamePanel.PAC_MAN.getPosition();
            targetTile.x = targetTile.x / GamePanel.TILE_SIZE;
            targetTile.y = targetTile.y / GamePanel.TILE_SIZE;
            Point nextMoveTile = GamePanel.CLYDE.getNextMoveTile(targetTile);

            return GamePanel.CLYDE.getOrientationToGoToTile(nextMoveTile);
        } else {
            return GamePanel.CLYDE.getNextScatterMovementOrientation();
        }
    }

    private int getSquareDistanceFromPacMan() {
        Point dist = new Point(
                GamePanel.PAC_MAN.getPosition().x / GamePanel.TILE_SIZE - GamePanel.CLYDE.getPosition().x / GamePanel.TILE_SIZE,
                GamePanel.PAC_MAN.getPosition().y / GamePanel.TILE_SIZE - GamePanel.CLYDE.getPosition().y / GamePanel.TILE_SIZE);

        return dist.x * dist.x + dist.y * dist.y;
    }
}
