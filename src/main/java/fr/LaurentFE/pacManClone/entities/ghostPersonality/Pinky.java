package fr.LaurentFE.pacManClone.entities.ghostPersonality;

import fr.LaurentFE.pacManClone.GamePanel;
import fr.LaurentFE.pacManClone.entities.Orientation;
import fr.LaurentFE.pacManClone.map.TileIndex;


public class Pinky implements GhostPersonality {

    public Pinky() {}

    @Override
    public Orientation getNextMovementOrientation() {
        TileIndex targetTile = GamePanel.PAC_MAN.getPosition().toTileIndex();
        switch (GamePanel.PAC_MAN.getOrientation()) {
            case UP -> targetTile.y -= 4;
            case LEFT -> targetTile.x -= 4;
            case DOWN -> targetTile.y +=4;
            case RIGHT -> targetTile.x +=4;
        }
        TileIndex nextMoveTile = GamePanel.PINKY.getNextMoveTile(targetTile);

        return GamePanel.PINKY.getOrientationToGoToTile(nextMoveTile);
    }
}
