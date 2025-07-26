package fr.LaurentFE.pacManClone.entities.ghostPersonality;

import fr.LaurentFE.pacManClone.GamePanel;
import fr.LaurentFE.pacManClone.entities.Orientation;
import fr.LaurentFE.pacManClone.map.TileIndex;

public final class Blinky implements GhostPersonality {

    public Blinky() {}

    @Override
    public Orientation getNextMovementOrientation() {
        TileIndex targetTile = GamePanel.PAC_MAN.getPosition().toTileIndex();
        TileIndex nextMoveTile = GamePanel.BLINKY.getNextMoveTile(targetTile);

        return GamePanel.BLINKY.getOrientationToGoToTile(nextMoveTile);
    }
}
