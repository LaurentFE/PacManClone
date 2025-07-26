package fr.LaurentFE.pacManClone.ghost.personality;

import fr.LaurentFE.pacManClone.GamePanel;
import fr.LaurentFE.pacManClone.Orientation;
import fr.LaurentFE.pacManClone.TileIndex;

public final class Blinky implements GhostPersonality {

    public Blinky() {}

    @Override
    public Orientation getNextMovementOrientation() {
        TileIndex targetTile = GamePanel.PAC_MAN.getPosition().toTileIndex();
        TileIndex nextMoveTile = GamePanel.BLINKY.getNextMoveTile(targetTile);

        return GamePanel.BLINKY.getOrientationToGoToTile(nextMoveTile);
    }
}
